package main

import (
	"log/slog"
	"os"
	"testing"

	socketd "socketd-transport-tcp"
	"socketd/transport/server"
)

// 测试服务器启动
func TestServer(t *testing.T) {
	slog.SetDefault(slog.New(slog.NewTextHandler(os.Stdout, nil)))

	var cfg = &server.Config{
		Protocol: "tcp",
		Host:     "127.0.0.1",
		Port:     8602,
		Debug:    true,
	}

	var srv = socketd.NewTcpServer(cfg)
	srv.Listen(new(MyListner))

	if err := srv.Start(); err != nil {
		t.Error(err)
	}
}
