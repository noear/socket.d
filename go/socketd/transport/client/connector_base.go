package client

import (
	"time"

	"socketd/transport/core"
)

// ConnectBase
/**
 * @Description: 客户端连接器基类
 * @Date 2024-01-17 14:25:10
 */
type ConnectBase[T ClientInternal] struct {
	client T
}

func NewConnectorBase[T ClientInternal](client T) *ConnectBase[T] {
	var cb = new(ConnectBase[T])
	cb.client = client
	return cb
}

func (cb *ConnectBase[T]) GetClient() T {
	return cb.client
}

func (cb *ConnectBase[T]) GetHeartbeatHandler() core.HeartbeatHandler {
	return cb.client.GetHeartbeatHandler()
}

func (cb *ConnectBase[T]) GetHeartbeatInterval() time.Duration {
	return cb.client.GetHeartbeatInterval()
}

func (cb *ConnectBase[T]) AutoReconnect() bool {
	return cb.client.GetConfig().IsAutoReconnect()
}
