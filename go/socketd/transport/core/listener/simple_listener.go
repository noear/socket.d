package listener

import (
	"socketd/transport/core"
	"socketd/transport/core/message"
)

//var _ core.Listener = new(SimpleListener)

type SimpleListener struct {
}

func (s SimpleListener) OnOpen(session core.Session) error {
	return nil
}

func (s SimpleListener) OnMessage(session core.Session, message *message.Frame) error {
	return nil
}

func (s SimpleListener) OnClose(session core.Session) {

}

func (s SimpleListener) OnError(session core.Session, err error) {

}
