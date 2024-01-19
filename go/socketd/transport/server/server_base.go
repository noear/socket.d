package server

import (
	"socketd/transport/core"
	"socketd/transport/core/impl"
)

type ServerBase[T core.ChannelAssistant[U], U any] struct {
	processor core.Processor
	config    *Config
	assistant T
	IsStarted bool
}

func NewServerBase[T core.ChannelAssistant[U], U any](cfg *Config, assistant T) *ServerBase[T, U] {
	var s = &ServerBase[T, U]{}
	s.config = cfg
	s.assistant = assistant
	s.processor = impl.NewProcessor()
	return s
}

func (s *ServerBase[T, U]) GetConfig() *Config {
	return s.config
}

func (s *ServerBase[T, U]) Listen(listener core.Listener) {
	s.processor.SetListener(listener)
}

func (s *ServerBase[T, U]) GetAssistant() T {
	return s.assistant
}

func (s *ServerBase[T, U]) GetProcessor() core.Processor {
	return s.processor
}
