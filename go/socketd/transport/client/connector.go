package client

import (
	"socketd/transport/core"
	"time"
)

type Connector interface {
	GetHeartbeatHandler() core.HeartbeatHandler //获取心跳处理
	GetHeartbeatInterval() time.Duration        //获取心跳频率
	AutoReconnect() bool                        //是否自动重连
	Connect() (channel core.Channel, err error) //连接
	Close()                                     //关闭
}
