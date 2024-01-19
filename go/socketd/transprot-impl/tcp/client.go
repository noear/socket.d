package tcp

import (
	"net"

	"socketd/transport/client"
	"socketd/transport/core"
	"socketd/transport/core/impl"
)

type Client struct {
	*client.ClientBase[*ChannelAssistant, *net.TCPConn]

	cfg *client.Config
}

func NewTcpClient(preCfg core.PreConfig) *Client {
	var c = new(Client)
	c.cfg = client.NewConfig(impl.DefaultConfig(true), preCfg)
	c.ClientBase = client.NewClientBase[*ChannelAssistant, *net.TCPConn](c.cfg, NewChannelAssistant(c.cfg))
	c.ClientBase.Connect(NewClientConnector(c))
	return c
}
