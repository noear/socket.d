package socketd

import (
	"fmt"

	"socketd/transport/server"
	"socketd/transprot-impl/tcp"
)

var SocketD = new(socketD)

type socketD struct {
	cfg *server.Config
}

func (sd *socketD) Version() string {
	return "0.1.0"
}

func (sd *socketD) ProtocolVersion() string {
	return "1.0"
}

func (sd *socketD) Config(options ...ConfigOption) *socketD {
	sd.cfg = new(server.Config)
	for _, opts := range options {
		opts(sd.cfg)
	}
	return sd
}

func (sd *socketD) CreateServer() server.Server {
	if sd.cfg == nil {
		panic("use Config(…) before creating")
	}
	switch sd.cfg.Protocol {
	case "tcp":
		return tcp.NewTcpServer(sd.cfg)
	}
	panic(fmt.Errorf("%s server not implement"))
}
