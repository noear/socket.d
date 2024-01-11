package core

import (
	"socketd/transport/core/message"
)

type Listener interface {
	OnOpen(session Session) error
	OnMessage(session Session, message *message.Message) error
	OnClose(session Session)
	OnError(session Session, err error)
}
