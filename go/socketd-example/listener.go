package main

import (
	"fmt"
	"socketd/transport/core"
)

type MyListner struct {
}

func (l *MyListner) OnClose(session core.Session) {
	fmt.Println("OnClose")
}
func (l *MyListner) OnError(session core.Session, err error) {
	fmt.Println("OnError")
}
func (l *MyListner) OnOpen(session core.Session) error {
	fmt.Println("OnOpen")
	return nil
}
func (l *MyListner) OnMessage(session core.Session, message core.Message) error {
	fmt.Println("OnMessage")
	return nil
}
