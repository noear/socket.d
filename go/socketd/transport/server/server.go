package server

import (
	"socketd/transport/core"
)

type Server interface {
	GetTitle() string
	//GetConfig() *Config
	Listen(listener core.Listener) error
	Start() error
	//Stop()
}
