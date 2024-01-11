package core

import (
	"socketd/transport/core/message"
)

type Session interface {
	LocalAddress() string
	RemoteAddress() string
	Handshake() Handshake
	SessionId() string

	Param(name string)                   // 握手参数
	ParamOrDefault(name string, def any) // 获取握手参数/默认值
	Path() string                        // 握手路径
	PathNew(path string) error           // 设置新握手路径

	AttrHas(name string) bool               // 是否有该属性
	AttrOrDefault(name string, def any) any // 获取属性值/默认值
	Attr(name string) any                   // 获取属性值
	AttrPut(name string, value any) error   // 放置属性值

	Reconnect() error                                             // 重新连接(手动)
	SendPing() error                                              // 手动发送一个ping包
	SendAlarm(from *message.Message, alarm string)                // 发送一个告警
	Reply(from *message.Message, entity *message.Entity) error    // 回复
	ReplyEnd(from *message.Message, entity *message.Entity) error // 最后一个回复，回复即断开
}
