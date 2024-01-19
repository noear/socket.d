package ws

import (
	"fmt"
	"log/slog"
	"net/http"

	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/impl"
	"socketd/transport/server"

	"github.com/gorilla/websocket"
)

type WebsocketServer struct {
	*server.ServerBase[*ChannelAssistant, *websocket.Conn] //继承ServerBase

	cfg      *server.Config
	connChan chan *websocket.Conn
}

func NewWebsocketServer(preCfg core.PreConfig) *WebsocketServer {
	var s = &WebsocketServer{}
	s.cfg = &server.Config{
		Config:    impl.DefaultConfig(false),
		PreConfig: preCfg,
	}
	s.ServerBase = server.NewServerBase[*ChannelAssistant, *websocket.Conn](s.cfg, NewChannelAssistant(s.cfg))
	return s
}

//func (s *WsServer) GetConfig() *server.Config {
//	return s.cfg
//}

func (s *WebsocketServer) GetTitle() string {
	return "ws/go-websocket/"
}

func (s *WebsocketServer) CreateServer() (err error) {
	return
}

func (s *WebsocketServer) Listen(listener core.Listener) server.Server {
	s.ServerBase.Listen(listener)
	return s
}

//func (s *WsServer) GetConfig() *server.Config {
//	return s.cfg
//}

func (s *WebsocketServer) Start() (err error) {
	var upgrader = websocket.Upgrader{
		ReadBufferSize:  s.cfg.GetReadBufferSize(),
		WriteBufferSize: s.cfg.GetWriteBufferSize(),
		CheckOrigin: func(r *http.Request) bool {
			return true
		},
	}
	s.connChan = make(chan *websocket.Conn)
	var handler = func(w http.ResponseWriter, r *http.Request) {
		conn, err := upgrader.Upgrade(w, r, nil)
		if err != nil {
			slog.Error(fmt.Sprintf("websocket upgrade error: %s", err))
			return
		}
		s.connChan <- conn
	}
	go s.Receive()

	slog.Info(fmt.Sprintf("Socket.D server listening: %s", s.cfg.GetAddress()))
	http.HandleFunc("/", handler)
	http.ListenAndServe(s.cfg.GetAddress(), nil)
	return
}

func (s *WebsocketServer) Receive() {
	for conn := range s.connChan {
		go func(conn *websocket.Conn) {
			// 处理来自 conn 的数据
			defer conn.Close()

			var channel = impl.NewChannelDefault[*ChannelAssistant, *websocket.Conn, *server.Config](conn, s)

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

func (s *WebsocketServer) Close() {

}
