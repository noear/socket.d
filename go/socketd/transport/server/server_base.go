package server

import (
	"socketd/transport/core"
	"socketd/transport/core/impl"
)

type ServerBase[T any] struct {
	*impl.ConfigBase

	processor    core.Processor
	serverConfig *Config
	assistant    core.ChannelAssistant[T]
	IsStarted    bool
}

func NewServerBase[T any](cfg *Config) *ServerBase[T] {
	var s = &ServerBase[T]{}
	s.serverConfig = cfg
	s.processor = impl.NewProcessor()
	s.ConfigBase = impl.DefualtConfig(false)
	return s
}

func (s *ServerBase[T]) GetConfig() core.Config {
	return s.ConfigBase
}

func (s *ServerBase[T]) Listen(listener core.Listener) {
	s.processor.SetListener(listener)
}

func (s *ServerBase[T]) GetChannelAssistant() core.ChannelAssistant[T] {
	return s.assistant
}

func (s *ServerBase[T]) GetProcessor() core.Processor {
	return s.processor
}
