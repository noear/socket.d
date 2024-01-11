package stream

import (
	"socketd/transport/core/message"
)

type RequestStream interface {
	Stream

	Await() *message.Message
	ThenReply(onReply func(message *message.Message)) RequestStream
}
