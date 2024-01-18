package demo01_helloworld

import (
	"testing"

	"socketd"
)

func TestServer(t *testing.T) {
	SetLog()

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
}
