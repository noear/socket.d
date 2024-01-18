package impl

import (
	"fmt"
	"log/slog"
	"net"
	"strconv"
	"sync"

	"socketd/transport/core"
	"socketd/transport/core/constant"
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
	session       core.Session
	onOpenFuture  func(bool, error)

	send_lock *sync.Mutex
}

func NewChannelDefault[T core.ChannelAssistant[U], U any, V core.Config](source U, su core.ChannelSupporter[T, U, V]) *ChannelDefault[T, U] {
	var cd = &ChannelDefault[T, U]{
		ChannelBase:   NewChannelBase(su.GetConfig()),
		source:        source,
		processor:     su.GetProcessor(),
		assistant:     su.GetAssistant(),
		send_lock:     new(sync.Mutex),
		streamManager: su.GetConfig().GetStreamManager(),
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

	slog.Debug("Channel.Send", slog.String("frame", frame.String()))
	if frame.Message != nil {

		// 注册流接收器
		if stream != nil {
			c.streamManager.Add(frame.Message.Sid, stream)
		}

		// 如果有实体（尝试分片）
		if frame.Message.Entity != nil {
			//确保用完自动关闭

			if frame.Message.DataSize() > c.GetConfig().GetFragmentSize() {
				frame.Message.MetaPut(constant.META_DATA_LENGTH, strconv.Itoa(frame.Message.DataSize()))
			}

			err = c.GetConfig().GetFragmentHandler().SplitFragment(c, stream, frame.Message, func(entity *message.Entity) (err error) {
				frame.Message.Entity = entity
				return c.assistant.Write(c.source, frame)
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
func (c *ChannelDefault[T, U]) Retrieve(frame *message.Frame, stream stream.StreamInternal) {
	if stream != nil {
		if stream.Demands() < constant.DEMANDS_MULTIPLE || frame.Flag == constant.FrameReplyEnd {
			//如果是单收或者答复结束，则移除流接收器
			c.streamManager.Remove(frame.Message.Sid)
		}

		if stream.Demands() < constant.DEMANDS_MULTIPLE {
			stream.OnReply(frame)
			return
		}
		go func() {
			stream.OnReply(frame)
		}()

		return
	}
	slog.Debug(fmt.Sprintf("%s stream not found, sid=%s, sessionId=%s",
		c.GetConfig().GetRoleName(), frame.Message.Sid, c.session.SessionId()))
}

func (c *ChannelDefault[T, U]) Reconnect() error {
	//由 ClientChannel 实现
	return nil
}

func (c *ChannelDefault[T, U]) OnError(err error) {
	c.processor.OnError(c, err)
}

func (c *ChannelDefault[T, U]) SetSession(session core.Session) {
	c.session = session
}

func (c *ChannelDefault[T, U]) GetSession() core.Session {
	if c.session == nil {
		c.session = NewSessionDefault(c.Channel)
	}
	return c.session
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
	slog.Debug(fmt.Sprintf("%s channel will be closed, sessionId = %s", c.GetConfig().GetRoleName(), c.GetSession().SessionId()))

	c.ChannelBase.Close(code)
	if err := c.assistant.Close(c.source); err != nil {
		slog.Warn(fmt.Sprintf("%s channel close error, sessionId = %s", c.GetConfig().GetRoleName(), c.GetSession().SessionId()))
	}
}
