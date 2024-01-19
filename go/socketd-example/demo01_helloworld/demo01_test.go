package demo01_helloworld

import (
	"fmt"
	"testing"
	"time"

	"socketd"
	"socketd/transport/core/message"
)

func TestDemo01(t *testing.T) {
	SetLog()

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
	msg, err := request.Await()
	if err != nil {
		fmt.Println("error:", err)
	} else {
		fmt.Println("msg:", msg)
	}

	stopChan := make(chan struct{})
	<-stopChan
}
