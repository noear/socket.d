package ws

import (
	"socketd/transport/client"
	"socketd/transport/core"
	"socketd/transport/core/impl"

	"github.com/gorilla/websocket"
)

type Client struct {
	*client.ClientBase[*ChannelAssistant, *websocket.Conn]

	cfg *client.Config
}

func NewWebsocketClient(preCfg core.PreConfig) *Client {
	var c = new(Client)
	c.cfg = client.NewConfig(impl.DefaultConfig(true), preCfg)
	c.ClientBase = client.NewClientBase[*ChannelAssistant, *websocket.Conn](c.cfg, NewChannelAssistant(c.cfg))
	c.ClientBase.Connect(NewClientConnector(c))
	return c
}
