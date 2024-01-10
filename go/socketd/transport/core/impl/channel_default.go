package impl

import (
	"bufio"
	"net"
	"socketd/transport/core"
	"strconv"
	"sync"
)

var _ core.ChannelInternal = new(ChannelDefault[net.Conn])

type ChannelDefault[T any] struct {
	*ChannelBase //继承
	source       T

	processor core.Processor           //处理器
	assistant core.ChannelAssistant[T] //助力

	seesion      core.Session
	onOpenFuture func(bool)

	send_lock *sync.Mutex
}

func NewChannelDefault[T any](source T, su core.ChannelSupporter[T]) *ChannelDefault[T] {
	return &ChannelDefault[T]{
		ChannelBase: NewChannelBase(su.GetConfig()),
		source:      source,
		processor:   su.GetProcessor(),
		assistant:   su.GetChannelAssistant(),
		send_lock:   new(sync.Mutex),
	}
}

func (c *ChannelDefault[T]) IsValid() bool {
	return c.isClosed == 0 && c.assistant.IsValid(c.source)
}

func (c *ChannelDefault[T]) GetLocalAddress() net.Addr {
	return c.assistant.GetLocalAddress(c.source)
}

func (c *ChannelDefault[T]) GetRemoteAddress() net.Addr {
	return c.assistant.GetRemoteAddress(c.source)
}

func (c *ChannelDefault[T]) Send(frame *core.Frame, stream any) (err error) {
	c.send_lock.Lock()
	defer c.send_lock.Unlock()

	if frame.Message != nil {

		// 注册流接收器
		if stream != nil {

		}

		// 如果有实体（尝试分片）
		if frame.Entity != nil {
			//确保用完自动关闭

			if frame.DataSize() > c.GetConfig().GetFragmentSize() {
				frame.MetaPut(core.META_DATA_LENGTH, strconv.Itoa(frame.DataSize()))
			}

			// TODO 分片
			//c.GetConfig()

			err = c.assistant.Write(c.source, frame)
			return
		}
	}

	err = c.assistant.Write(c.source, frame)
	//if stream != nil {
	//
	//}
	return
}

func (c *ChannelDefault[T]) SetSession(session core.Session) {
	//TODO implement me
	panic("implement me")
}

func (c *ChannelDefault[T]) GetSession() core.Session {
	return c.seesion
}

func (c *ChannelDefault[T]) GetStream(sid string) bufio.Reader {
	//TODO implement me
	panic("implement me")
}

func (c *ChannelDefault[T]) OnOpenFuture(future func(bool, error)) {
	//TODO implement me
	panic("implement me")
}

func (c *ChannelDefault[T]) DoOpenFuture(isOk bool, err error) {
	//TODO implement me
	panic("implement me")
}
