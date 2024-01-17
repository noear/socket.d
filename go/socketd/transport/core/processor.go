package core

import (
	"socketd/transport/core/message"
)

type Processor interface {
	SetListener(listener Listener)                                 // 设置监听器
	OnOpen(channel ChannelInternal)                                // 打开处理
	OnReceive(channel ChannelInternal, frame *message.Frame) error // 接收处理
	OnMessage(channel ChannelInternal, msg *message.Frame)         // 收消息时处理
	OnClose(channel ChannelInternal)                               // 关闭处理
	OnError(channel ChannelInternal, err error)                    // 错误处理
}
