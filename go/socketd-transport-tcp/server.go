package socketd_transport_tcp

import (
	"fmt"
	"net"

	"socketd"
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

func NewTcpServer(cfg *server.Config) *TcpServer {
	var s = &TcpServer{}
	s.cfg = cfg
	s.ServerBase = server.NewServerBase[*ChannelAssistant, *net.TCPConn](cfg, NewChannelAssistant(cfg))
	return s
}

func (s *TcpServer) GetTitle() string {
	return "tcp/go-tcp/" + (&socketd.SocketD{}).Version()
}

func (s *TcpServer) CreateServer() (err error) {
	return
}

//func (s *TcpServer) GetConfig() *server.Config {
//	return s.cfg
//}

func (s *TcpServer) Start() (err error) {
	tcpAddr, err := net.ResolveTCPAddr(s.cfg.Protocol, fmt.Sprintf("%v:%v", s.cfg.Host, s.cfg.Port))
	if err != nil {
		return err
	}
	s.listener, err = net.ListenTCP(s.cfg.Protocol, tcpAddr)

	s.connChan = make(chan *net.TCPConn)
	go s.Receive()

	for {
		conn, err := s.listener.AcceptTCP()
		if err != nil {
			// TODO 日志记录
			fmt.Println(err)
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

			var channel = impl.NewChannelDefault[*ChannelAssistant, *net.TCPConn](conn, s)

			for {

				frame, err := s.GetAssistant().Read(conn)
				if err != nil {
					fmt.Println(err)
					channel.Close(constant.CLOSE3_ERROR)
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
		// TODO 日志记录
		fmt.Println(err)
	}
}
