package main

import (
	"fmt"
	"strings"

	"socketd/transport/core"
	"socketd/transport/core/message"
)

type MyListner struct {
}

func (l *MyListner) OnClose(session core.Session) {
	fmt.Println("-> OnClose")
}
func (l *MyListner) OnError(session core.Session, err error) {
	fmt.Println("-> OnError")
}
func (l *MyListner) OnOpen(session core.Session) error {
	fmt.Println("-> OnOpen")
	return nil
}
func (l *MyListner) OnMessage(session core.Session, msg *message.Frame) error {
	fmt.Println("-> OnMessage")
	if strings.HasPrefix(msg.Message.Event, "/demo") {

		err := session.Reply(msg, msg.Message.Entity)
		if err != nil {
			fmt.Println(err)
			return err
		}
	}
	return nil
}
