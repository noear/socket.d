package server

import (
	"socketd/transport/core"
)

type Server interface {
	GetTitle() string
	GetConfig() core.Config
	Listen(listener core.Listener) Server
	Start() error
	//Stop()
}
