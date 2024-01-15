package message

import (
	"fmt"
)

type Message struct {
	Sid   string
	Event string
	*Entity
}

func NewMessage(sid, event string, entity *Entity) *Message {
	return &Message{
		Sid:    sid,
		Event:  event,
		Entity: entity,
	}
}

func (m *Message) String() string {
	if m.Entity != nil {
		return fmt.Sprintf("%+v", m.Entity)
	}
	return fmt.Sprintf("{Sid: %s, Event: %s}", m.Sid, m.Event)
}
