package main

import (
	"log/slog"
	"os"
	"socketd/transport/server"
	"testing"

	socketd "socketd-transport-tcp"
)

// 测试服务器启动
func TestServer(t *testing.T) {
	slog.SetDefault(slog.New(slog.NewTextHandler(os.Stdout, nil)))

	var srv = socketd.Server{}
	srv.ServerBase = server.NewServerBase(new(server.Config))

	var cfg = srv.GetConfig()
	cfg.WithSchema("tcp://0.0.0.0:8602")
	cfg.DebugMode()

	srv.Listen(&MyListner{})
	srv.Start()
}
