package tcp

import (
	"fmt"
	"log/slog"
	"net"

	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/impl"
	"socketd/transport/server"
)

type TcpServer struct {
	*server.ServerBase[*ChannelAssistant, *net.TCPConn] //继承ServerBase

	cfg      *server.Config
	listener *net.TCPListener
	connChan chan *net.TCPConn
}

func NewTcpServer(preCfg core.PreConfig) *TcpServer {
	var s = &TcpServer{}
	s.cfg = &server.Config{
		Config:    impl.DefaultConfig(false),
		PreConfig: preCfg,
	}
	s.ServerBase = server.NewServerBase[*ChannelAssistant, *net.TCPConn](s.cfg, NewChannelAssistant(s.cfg))
	return s
}

//func (s *TcpServer) GetConfig() *server.Config {
//	return s.cfg
//}

func (s *TcpServer) GetTitle() string {
	return "tcp/go-tcp/"
}

func (s *TcpServer) CreateServer() (err error) {
	return
}

func (s *TcpServer) Listen(listener core.Listener) server.Server {
	s.ServerBase.Listen(listener)
	return s
}

//func (s *TcpServer) GetConfig() *server.Config {
//	return s.cfg
//}

func (s *TcpServer) Start() (err error) {
	tcpAddr, err := net.ResolveTCPAddr(s.cfg.GetSchema(), s.cfg.GetAddress())
	if err != nil {
		return err
	}
	s.listener, err = net.ListenTCP(s.cfg.GetSchema(), tcpAddr)
	slog.Info(fmt.Sprintf("Socket.D server listening: %s", s.listener.Addr()))

	s.connChan = make(chan *net.TCPConn)
	go s.Receive()

	for {
		conn, err := s.listener.AcceptTCP()
		if err != nil {
			slog.Warn("conn interrupt", "err", err)
			continue
		}
		s.connChan <- conn
	}
}

func (s *TcpServer) Receive() {
	for conn := range s.connChan {
		go func(conn *net.TCPConn) {
			// 处理来自 conn 的数据
			defer conn.Close()

			var channel = impl.NewChannelDefault[*ChannelAssistant, *net.TCPConn, *server.Config](conn, s)

			for {

				frame, err := s.GetAssistant().Read(conn)
				if err != nil {
					slog.Debug("conn interrupt", "err", err)
					channel.Close(constant.CLOSE3_ERROR)
					s.GetProcessor().OnError(channel, err)
					s.GetProcessor().OnClose(channel)
					return
				}
				if frame.Len != 0 {
					s.GetProcessor().OnReceive(channel, frame)
				}
			}
		}(conn)
	}
}

func (s *TcpServer) Close() {
	if err := s.listener.Close(); err != nil {
		slog.Error("server stop", "err", err)
	}
}
