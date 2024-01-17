package impl

import (
	"time"

	"socketd/transport/stream"

	"socketd/transport/core/constant"
	"socketd/transport/core/message"
)

type RequestStream struct {
	*StreamBase

	done bool
}

func NewRequestStream(sid string, timeout time.Duration) *RequestStream {
	return &RequestStream{StreamBase: NewStreamBase(sid, constant.DEMANDS_SIGNLE, timeout)}
}

func (s *RequestStream) IsDone() bool {
	return s.done
}

func (s *RequestStream) OnReply(frame *message.Frame) {

}

func (s *RequestStream) Await() *message.Message {

	return nil
}

func (s *RequestStream) ThenReply(onReply func(message *message.Message)) stream.RequestStream {
	panic("impl me!")
	return nil
}
