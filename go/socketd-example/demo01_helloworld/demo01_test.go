package demo01_helloworld

import (
	"log/slog"
	"os"
	"testing"
	"time"

	"socketd"
)

func TestDemo01(t *testing.T) {
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

	socketd.SocketD.Config(
		socketd.WithDebug(),
		socketd.WithLink("sd:tcp://0.0.0.0:8602?user=abc"),
	).CreateServer().Listen(new(MyListner)).Start()

}
