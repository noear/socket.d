package demo01_helloworld

import (
	"log/slog"
	"os"
	"testing"
	"time"

	"socketd/transport/core/message"

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

	//启动服务器
	go func() {
		err := socketd.SocketD.Config(
			socketd.WithSchema("tcp"),
			socketd.WithHost("0.0.0.0"),
			socketd.WithPort(8602),
			//socketd.WithLink("sd:tcp://0.0.0.0:8602?user=abc"),
		).CreateServer().Listen(new(MyListner)).Start()
		if err != nil {
			t.Error(err)
			return
		}
	}()

	//等待3秒，先启动服务器
	time.Sleep(time.Second * 3)

	clientSession, err := socketd.SocketD.Config(
		socketd.WithLink("sd:tcp://127.0.0.1:8602?user=abc"),
	).CreateClient().Open()
	if err != nil {
		t.Error(err)
		return
	}

	request, err := clientSession.SendAndRequest("/demo", message.NewEntity(nil, []byte("hello world!")), 0)
	if err != nil {
		t.Error(err)
		return
	}
	request.Await()

}
