package core

import (
	"fmt"
	"net/url"
)

const (
	ConnectFrame = 10 // 握手：连接(c->s)，提交客户端握手信息，请求服务端握手信息
	ConnackFrame = 11 // 握手：确认(c<-s)，响应服务端握手信息

	PingFrame = 20 //心跳:ping(c-->s)
	PongFrame = 21 //心跳:pong(c<--s)

	CloseFrame = 30 // 客户端关闭连接
	AlarmFrame = 31 // 客户端异常

	MessageFrame   = 40 // 消息(c<->s)
	RequestFrame   = 41 // 请求(c<->s)
	SubscribeFrame = 42 // 订阅
	ReplyFrame     = 48 // 回复
	ReplyEndFrame  = 49 // 结束回复
)

type Frame struct {
	Len  uint32
	Flag uint32
	Message
}

type Message struct {
	Sid   string
	Event string
	Entity
}

type Entity struct {
	Meta url.Values
	Data []byte
}

func (f *Frame) String() string {
	return fmt.Sprintf("{Len:%d, Flag:%d, Sid:%s, Event:%s, Meta:%s, Data:%s}", f.Len, f.Flag, f.Sid, f.Event, f.Meta, f.Data)
}
