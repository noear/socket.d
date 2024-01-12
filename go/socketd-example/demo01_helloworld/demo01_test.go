package demo01_helloworld

import (
	"log/slog"
	"os"
	"testing"

	"socketd"
)

func TestDemo01(t *testing.T) {
	slog.SetDefault(slog.New(slog.NewTextHandler(os.Stdout, nil)))

	socketd.SocketD.Config(
		socketd.WithDebug(),
		socketd.WithLink("sd:tcp://0.0.0.0:8602?user=abc"),
	).CreateServer().Listen(new(MyListner)).Start()

}
