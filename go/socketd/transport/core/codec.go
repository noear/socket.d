package core

import (
	"socketd/transport/core/message"
)

type Codec interface {
	Decode(r []byte) *message.Frame
	Encode(f *message.Frame) []byte
}
