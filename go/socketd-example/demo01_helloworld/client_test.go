package demo01_helloworld

import (
	"fmt"
	"testing"

	"socketd"
	"socketd/transport/core/message"
)

func TestClient(t *testing.T) {
	SetLog()
	
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
	msg, err := request.Await()
	if err != nil {
		fmt.Println("error:", err)
	} else {
		fmt.Println("msg:", msg)
	}
}
