package stream

import (
	"socketd/transport/core/message"
)

type SubscribeStream interface {
	Stream
	ThenReply(onReply func(message *message.Message)) SubscribeStream
}
