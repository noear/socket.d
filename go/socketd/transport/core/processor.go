package core

import (
	"socketd/transport/core/message"
)

type Processor interface {
	SetListener(listener Listener)                         // 设置监听器
	OnOpen(channel Channel)                                // 打开处理
	OnReceive(channel Channel, frame *message.Frame) error // 接收处理
	OnMessage(channel Channel, msg *message.Frame)         // 收消息时处理
	OnClose(channel Channel)                               // 关闭处理
	OnError(channel Channel, err error)                    // 错误处理
}
