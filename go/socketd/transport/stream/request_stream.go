package stream

import (
	"socketd/transport/core/message"
)

type RequestStream interface {
	Stream

	Await() (*message.Message, error)
	ThenReply(onReply func(message *message.Message)) RequestStream
}
