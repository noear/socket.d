package impl

import (
	"fmt"
	"time"

	"socketd/transport/stream"

	"socketd/transport/core/constant"
	"socketd/transport/core/message"
)

type RequestStream struct {
	*StreamBase //继承

	doOnReply func(message *message.Message)
	replyChan chan *message.Message
	done      bool
}

func NewRequestStream(sid string, timeout time.Duration) *RequestStream {
	return &RequestStream{StreamBase: NewStreamBase(sid, constant.DEMANDS_SIGNLE, timeout), replyChan: make(chan *message.Message)}
}

func (s *RequestStream) IsDone() bool {
	return s.done
}

func (s *RequestStream) OnReply(frame *message.Frame) {
	s.done = true
	s.replyChan <- frame.Message
}

func (s *RequestStream) Await() (*message.Message, error) {
	timer := time.NewTimer(time.Millisecond * 30) // 30毫秒超时
	for {
		select {
		case <-timer.C:
			return nil, fmt.Errorf("request reply timeout, sid = %s", s.StreamBase.Sid())
		case reply := <-s.replyChan:
			return reply, nil
		}
	}
}

func (s *RequestStream) ThenReply(onReply func(message *message.Message)) stream.RequestStream {
	s.doOnReply = onReply
	return s
}
