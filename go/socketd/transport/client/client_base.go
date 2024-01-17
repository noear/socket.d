package client

import (
	"time"

	"socketd/transport/core"
	"socketd/transport/core/impl"
)

type ClientBase[T core.ChannelAssistant[U], U any] struct {
	processor        core.Processor
	heartbeatHandler core.HeartbeatHandler
	config           *Config
	assistant        T
	connect          Connector
}

func NewClientBase[T core.ChannelAssistant[U], U any](config *Config, assistant T) *ClientBase[T, U] {
	var cb = new(ClientBase[T, U])
	cb.processor = impl.NewProcessor()
	cb.config = config
	cb.assistant = assistant
	return cb
}

func (cb *ClientBase[T, U]) Connect(connect Connector) {
	cb.connect = connect
}

func (cb *ClientBase[T, U]) GetAssistant() T {
	return cb.assistant
}

func (cb *ClientBase[T, U]) GetHeartbeatHandler() core.HeartbeatHandler {
	return cb.heartbeatHandler
}

func (cb *ClientBase[T, U]) GetHeartbeatInterval() time.Duration {
	return cb.config.GetHeartbeatInterval()
}

func (cb *ClientBase[T, U]) GetConfig() *Config {
	return cb.config
}

func (cb *ClientBase[T, U]) GetProcessor() core.Processor {
	return cb.processor
}

func (cb *ClientBase[T, U]) HeartbeatHandler(handler core.HeartbeatHandler) Client {
	cb.heartbeatHandler = handler
	return cb
}

//func (cb *ClientBase[T, U]) Config(configHandler ClientConfigHandler) Client {
//	//TODO implement me
//	panic("implement me")
//}

func (cb *ClientBase[T, U]) Listen(listener core.Listener) Client {
	cb.processor.SetListener(listener)
	return cb
}

func (cb *ClientBase[T, U]) Open() (Session, error) {
	conn, err := cb.connect.Connect()
	if err != nil {
		return nil, err
	}
	clientChannel := NewClientChannel(conn, cb.connect)
	clientChannel.SetHandshake(conn.GetHandshake())
	session := impl.NewSessionDefault(clientChannel)
	return session, nil
}
