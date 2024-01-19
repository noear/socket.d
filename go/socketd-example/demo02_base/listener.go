package demo02_base

import (
	"fmt"
	"log/slog"
	"os"
	"strings"
	"time"

	"socketd/transport/core"
	"socketd/transport/core/message"
)

func SetLog() {
	slog.SetDefault(slog.New(slog.NewTextHandler(os.Stdout, &slog.HandlerOptions{
		//AddSource: true,
		Level: slog.LevelDebug,
		ReplaceAttr: func(groups []string, a slog.Attr) slog.Attr {
			if a.Key == slog.TimeKey {
				if t, ok := a.Value.Any().(time.Time); ok {
					a.Value = slog.StringValue(t.Format(time.DateTime))
				}
			}
			return a
		},
	})))
}

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

		ent := message.NewEntity(nil, []byte("Nice to meet U :)"))

		err := session.Reply(msg, ent)
		if err != nil {
			fmt.Println(err)
			return err
		}
	}
	return nil
}
