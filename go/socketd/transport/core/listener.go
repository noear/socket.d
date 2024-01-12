package core

import (
	"socketd/transport/core/message"
)

type Listener interface {
	OnOpen(session Session) error
	OnMessage(session Session, message *message.Frame) error
	OnClose(session Session)
	OnError(session Session, err error)
}
