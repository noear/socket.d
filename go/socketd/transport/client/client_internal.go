package client

import (
	"time"

	"socketd/transport/core"
)

type ClientInternal interface {
	Client //继承

	GetHeartbeatHandler() core.HeartbeatHandler
	GetHeartbeatInterval() time.Duration
	GetConfig() *Config
	GetProcessor() core.Processor
}
