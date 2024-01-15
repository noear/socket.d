package impl

import (
	"socketd/transport/core/constant"
	"socketd/transport/core/message"
)

type SendStream struct {
	*StreamBase
}

func NewSendStream(sid string) *SendStream {
	return &SendStream{StreamBase: NewStreamBase(sid, constant.DEMANDS_ZERO, 0)}
}

// IsDone
/**
 * @Description 是否完成的
 */
func (s *SendStream) IsDone() bool {
	return true
}

func (s *SendStream) OnReply(reply *message.Frame) {

}
