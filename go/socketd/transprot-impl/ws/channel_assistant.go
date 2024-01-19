package ws

import (
	"fmt"
	"log/slog"
	"net"

	"socketd/transport/core"
	"socketd/transport/core/message"

	"github.com/gorilla/websocket"
)

type ChannelAssistant struct {
	Config core.Config
}

func NewChannelAssistant(cfg core.Config) *ChannelAssistant {
	return &ChannelAssistant{
		Config: cfg,
	}
}

func (c *ChannelAssistant) IsValid(conn *websocket.Conn) bool {
	// TODO: check connection
	return true
}

func (c *ChannelAssistant) Close(conn *websocket.Conn) (err error) {
	return conn.Close()
}

func (c *ChannelAssistant) GetLocalAddress(conn *websocket.Conn) net.Addr {
	return conn.LocalAddr()
}

func (c *ChannelAssistant) GetRemoteAddress(conn *websocket.Conn) net.Addr {
	return conn.RemoteAddr()
}

func (c *ChannelAssistant) Write(conn *websocket.Conn, frame *message.Frame) (err error) {
	var bbs = c.Config.GetCodec().Encode(frame)
	_, err = conn.NetConn().Write(bbs)
	return
}

func (c *ChannelAssistant) Read(conn *websocket.Conn) (*message.Frame, error) {
	// 读缓冲
	buf := make([]byte, c.Config.GetReadBufferSize())
	n, err := conn.NetConn().Read(buf)
	if err != nil {
		slog.Warn(fmt.Sprintf("connect read error %s", err))
		return nil, err
	}
	return c.Config.GetCodec().Decode(buf[:n]), nil
}
