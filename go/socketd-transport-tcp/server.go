package socketd_transport_tcp

import (
	"fmt"
	"net"

	"socketd"
	"socketd/transport/core"
	"socketd/transport/core/impl"
	"socketd/transport/server"
)

type TcpServer struct {
	*server.ServerBase[*net.TCPConn] //继承ServerBase

	cfg      *server.Config
	listener *net.TCPListener
	connChan chan *net.TCPConn
}

func NewTcpServer(cfg *server.Config) *TcpServer {
	var s = &TcpServer{}
	s.cfg = cfg
	s.ServerBase = server.NewServerBase[*net.TCPConn](cfg)
	return s
}

func (s *TcpServer) GetTitle() string {
	return "tcp/go-tcp/" + (&socketd.SocketD{}).Version()
}

func (s *TcpServer) CreateServer() (err error) {
	return
}

func (s *TcpServer) GetConfig() *server.Config {
	return s.cfg
}

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
	var codec = s.ConfigBase.GetCodec()

	for conn := range s.connChan {
		go func(conn *net.TCPConn) {
			// 处理来自 conn 的数据
			defer conn.Close()

			for {
				// 读缓冲
				buf := make([]byte, s.ConfigBase.GetReadBufferSize())
				n, err := conn.Read(buf)
				if err != nil {
					// TODO 日志记录
					fmt.Println(err)
					return
				}

				var frame = new(core.Frame)
				codec.Decode(frame, buf[:n])
				fmt.Println("Frame", frame)

				var channel = impl.NewChannel()
				s.GetProcessor().OnReceive()

				// var conn2 = impl.NewChannel()
				// var session = new(core.Session)
				// s.processor.OnReceive(conn2, *frame)
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
