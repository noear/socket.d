package client

import "socketd/transport/core"

type Client interface {
	HeartbeatHandler(HeartbeatHandler core.HeartbeatHandler) Client //心跳
	//Config(ClientConfigHandler configHandler) Client                //配置
	Listen(listener core.Listener) Client //监听
	Open() (Session, error)               //打开会话
}
