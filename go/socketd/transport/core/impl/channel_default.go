package impl

import (
	"fmt"
	"io"
	"net"
	"strconv"
	"sync"

	"socketd/transport/core"
	"socketd/transport/core/message"
	"socketd/transport/stream"
)

//var _ core.ChannelInternal = new(ChannelDefault[net.Conn])

type ChannelDefault[T core.ChannelAssistant[U], U any] struct {
	*ChannelBase //继承
	source       U

	processor     core.Processor //处理器
	assistant     T              //助力
	streamManager stream.StreamManager
	seesion       core.Session
	onOpenFuture  func(bool, error)

	send_lock *sync.Mutex
}

func NewChannelDefault[T core.ChannelAssistant[U], U any](source U, su core.ChannelSupporter[T, U]) *ChannelDefault[T, U] {
	var cd = &ChannelDefault[T, U]{
		ChannelBase: NewChannelBase(su.GetConfig()),
		source:      source,
		processor:   su.GetProcessor(),
		assistant:   su.GetAssistant(),
		send_lock:   new(sync.Mutex),
	}
	cd.ChannelBase.Channel = cd
	return cd
}

func (c *ChannelDefault[T, U]) IsValid() bool {
	return c.isClosed == 0 && c.assistant.IsValid(c.source)
}

func (c *ChannelDefault[T, U]) GetLocalAddress() net.Addr {
	return c.assistant.GetLocalAddress(c.source)
}

func (c *ChannelDefault[T, U]) GetRemoteAddress() net.Addr {
	return c.assistant.GetRemoteAddress(c.source)
}

// Send
/**
 * @Description 发送
 * @Date 2024-01-11 10:14:15
 */
func (c *ChannelDefault[T, U]) Send(frame *message.Frame, stream stream.StreamInternal) (err error) {
	c.send_lock.Lock()
	defer c.send_lock.Unlock()

	if frame.Message != nil {

		// 注册流接收器
		if stream != nil {
			c.streamManager.Add(frame.Sid, stream)
		}

		// 如果有实体（尝试分片）
		if frame.Entity != nil {
			//确保用完自动关闭

			if frame.DataSize() > c.GetConfig().GetFragmentSize() {
				frame.MetaPut(message.META_DATA_LENGTH, strconv.Itoa(frame.DataSize()))
			}

			err = c.GetConfig().GetFragmentHandler().SplitFragment(c, stream, frame.Message, func(entity *message.Entity) (err error) {
				var f = message.NewFrame()
				f.Flag = frame.Flag
				f.CopyEntity(entity)
				return c.assistant.Write(c.source, f)
			})
			return
		}
	}

	err = c.assistant.Write(c.source, frame)
	if stream != nil {
		stream.OnProgress(true, 1, 1)
	}
	return
}

// Retrieve
/**
 * @Description 接收（接收答复帧）
 * @Date 2024-01-11 10:14:42
 */
func (c *ChannelDefault[T, U]) Retrieve(frame *message.Frame, stream io.Reader) {

}

func (c *ChannelDefault[T, U]) Reconnect() error {
	return nil
}

func (c *ChannelDefault[T, U]) OnError(err error) {
	c.processor.OnError(c, err)
}

func (c *ChannelDefault[T, U]) SetSession(session core.Session) {
	//TODO implement me
	panic("implement me")
}

func (c *ChannelDefault[T, U]) GetSession() core.Session {
	return c.seesion
}

func (c *ChannelDefault[T, U]) GetStream(sid string) stream.StreamInternal {
	return c.streamManager.Get(sid)
}

func (c *ChannelDefault[T, U]) OnOpenFuture(future func(bool, error)) {
	c.onOpenFuture = future
}

func (c *ChannelDefault[T, U]) DoOpenFuture(isOk bool, err error) {
	if isOk {
		c.onOpenFuture(true, nil)
		return
	}
	c.onOpenFuture(false, err)
}

func (c *ChannelDefault[T, U]) Close(code int) {
	//TODO 日志记录
	fmt.Printf("%s channel will be closed, sessionId = %s/n", c.GetConfig().GetRoleName(), c.GetSession().SessionId())

	c.ChannelBase.Close(code)
	if err := c.assistant.Close(c.source); err != nil {
		fmt.Printf("%s channel close error, sessionId=%s", c.GetConfig().GetRoleName(), c.GetSession().SessionId())
	}
}
