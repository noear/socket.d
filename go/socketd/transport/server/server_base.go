package server

import (
	"socketd/transport/core"
	"socketd/transport/core/impl"
)

type ServerBase struct {
	processor core.Processor
	serverCfg *Config
	IsStarted bool
}

func NewServerBase(cfg *Config) *ServerBase {
	return &ServerBase{
		serverCfg: cfg,
		processor: impl.NewProcessor(),
	}
}

func (s *ServerBase) GetConfig() *Config {
	return s.serverCfg
}

func (s *ServerBase) Listen(listener core.Listener) {
	s.processor.SetListener(listener)
}
