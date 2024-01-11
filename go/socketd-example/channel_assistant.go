package main

import (
	"net"

	"socketd/transport/core"
	"socketd/transport/core/message"
)

var _ core.ChannelAssistant[*net.TCPConn] = new(TcpChannelAssistant)

type TcpChannelAssistant struct {
	Config core.Config
}

func NewTcpChannelAssistant(cfg core.Config) *TcpChannelAssistant {
	return &TcpChannelAssistant{
		Config: cfg,
	}
}

func (t *TcpChannelAssistant) GetLocalAddress(conn *net.TCPConn) net.Addr {
	return conn.LocalAddr()
}

func (t *TcpChannelAssistant) GetRemoteAddress(conn *net.TCPConn) net.Addr {
	return conn.RemoteAddr()
}

func (t *TcpChannelAssistant) IsValid(conn *net.TCPConn) bool {
	return true
}

func (t *TcpChannelAssistant) Write(conn *net.TCPConn, frame *message.Frame) (err error) {
	_, err = conn.Write(t.Config.GetCodec().Encode(frame))
	return err
}

func (t *TcpChannelAssistant) Close(conn *net.TCPConn) error {
	return conn.Close()
}
