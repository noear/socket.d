package core

import (
	"socketd/transport/core/message"
)

type Codec interface {
	Decode(f *message.Frame, body []byte)
	Encode(f *message.Frame) []byte
}
