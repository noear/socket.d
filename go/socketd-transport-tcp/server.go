package socketd_transport_tcp

import (
	"fmt"
	"net"

	"socketd/transport/core"
	"socketd/transport/core/impl"
	"socketd/transport/server"
)

type Server struct {
	*server.ServerBase

	codec    core.Codec
	connChan chan net.Conn
}

func (s *Server) GetTitle() string {
	return "tcp"
}

func (s *Server) Debug() {
	s.GetConfig().Debug = true
}

func (s *Server) Start() (err error) {
	// 启动前初始化
	if s.codec == nil {
		s.codec = &impl.CodecDefault{}
	}
	if s.connChan == nil {
		s.connChan = make(chan net.Conn)
	}

	go s.HandleConn()

	var cfg = s.GetConfig()

	tcpAddr, err := net.ResolveTCPAddr(cfg.Protocol, fmt.Sprintf("%v:%v", cfg.Host, cfg.Port))
	if err != nil {
		return err
	}
	listener, err := net.ListenTCP(cfg.Protocol, tcpAddr)
	if err != nil {
		return err
	}
	defer listener.Close()

	// 接受连接并放到 channel 中
	for {
		conn, err := listener.AcceptTCP()
		if err != nil {
			fmt.Println("accept error:", err)
			continue
		}
		s.connChan <- conn
	}
}

func (s *Server) HandleConn() {
	// 处理连接
	for conn := range s.connChan {
		go func(conn net.Conn) {
			// 处理来自 conn 的数据
			defer conn.Close()

			for {
				// 读缓冲暂定1024
				buf := make([]byte, 1024)
				n, err := conn.Read(buf)
				if err != nil {
					return
				}

				var frame = new(core.Frame)
				s.codec.Decode(frame, buf[:n])

				fmt.Println("Frame", frame)

				// var conn2 = impl.NewChannel()
				// var session = new(core.Session)
				// s.processor.OnReceive(conn2, *frame)
			}
		}(conn)
	}
}

func (s *Server) Receive(channel core.ChannelInternal, conn net.Conn) {

}
