package socketd_transport_tcp

import (
	"fmt"
	"net"

	"socketd/transport/core"
	"socketd/transport/core/message"
)

type ChannelAssistant struct {
	Config core.Config
}

func NewChannelAssistant(cfg core.Config) *ChannelAssistant {
	return &ChannelAssistant{
		Config: cfg,
	}
}

func (c *ChannelAssistant) IsValid(conn *net.TCPConn) bool {
	// TODO: check connection
	return true
}

func (c *ChannelAssistant) Close(conn *net.TCPConn) (err error) {
	return conn.Close()
}

func (c *ChannelAssistant) GetLocalAddress(conn *net.TCPConn) net.Addr {
	return conn.LocalAddr()
}

func (c *ChannelAssistant) GetRemoteAddress(conn *net.TCPConn) net.Addr {
	return conn.RemoteAddr()
}

func (c *ChannelAssistant) Write(conn *net.TCPConn, frame *message.Frame) (err error) {
	var bbs = c.Config.GetCodec().Encode(frame)
	_, err = conn.Write(bbs)
	return
}

func (c *ChannelAssistant) Read(conn *net.TCPConn) (*message.Frame, error) {
	// 读缓冲
	buf := make([]byte, c.Config.GetReadBufferSize())
	n, err := conn.Read(buf)
	if err != nil {
		// TODO 日志记录
		fmt.Println(err)
		return nil, err
	}
	return c.Config.GetCodec().Decode(buf[:n]), nil
}
