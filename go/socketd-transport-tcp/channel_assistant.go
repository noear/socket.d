package socketd_transport_tcp

import (
	"net"

	"socketd/transport/core"
)

type ChannelAssistant struct {
	Config core.Config
}

func NewChannelAssistant(cfg core.Config) *ChannelAssistant {
	return &ChannelAssistant{
		Config: cfg,
	}
}

func (c *ChannelAssistant) IsValid(target *net.TCPConn) bool {
	// TODO: check connection
	return true
}

func (c *ChannelAssistant) Close(target *net.TCPConn) (err error) {
	return target.Close()
}

func (c *ChannelAssistant) GetLocalAddress(target *net.TCPConn) net.Addr {
	return target.LocalAddr()
}

func (c *ChannelAssistant) GetRemoteAddress(target *net.TCPConn) net.Addr {
	return target.RemoteAddr()
}

func (c *ChannelAssistant) Write(target *net.TCPConn, frame *core.Frame) (err error) {
	var bbs = c.Config.GetCodec().Encode(frame)
	_, err = target.Write(bbs)
	return
}
