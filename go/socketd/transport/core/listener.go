package core

type Listener interface {
	OnOpen(session Session) error
	OnMessage(session Session, message Message) error
	OnClose(session Session)
	OnError(session Session, err error)
}
