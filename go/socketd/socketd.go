package socketd

import (
	"fmt"

	"socketd/transport/client"

	"socketd/transport/server"
	"socketd/transprot-impl/tcp"
)

var SocketD = new(socketD)

type socketD struct {
	cfg *Config
}

func (sd *socketD) Version() string {
	return "0.1.0"
}

func (sd *socketD) ProtocolVersion() string {
	return "1.0"
}

func (sd *socketD) Config(options ...ConfigOption) *socketD {
	sd.cfg = new(Config)
	for _, opts := range options {
		opts(sd.cfg)
	}
	return sd
}

func (sd *socketD) CreateServer() server.Server {
	if sd.cfg == nil {
		panic("use Config(…) before creating")
	}
	switch sd.cfg.GetSchema() {
	case "tcp":
		return tcp.NewTcpServer(sd.cfg)
	}
	panic(fmt.Errorf("%s server not implement", sd.cfg.schema))
}

func (sd *socketD) CreateClient() client.Client {
	if sd.cfg == nil {
		panic("use Config(…) before creating")
	}
	switch sd.cfg.GetSchema() {
	case "tcp":
		return tcp.NewTcpClient(sd.cfg)
	}
	panic(fmt.Errorf("%s server not implement", sd.cfg.schema))
}
