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

const (
	META_SOCKETD_VERSION           = "Socket.D"    // 框架版本号
	META_DATA_LENGTH               = "Data-Length" // 数据长度
	META_DATA_TYPE                 = "Data-Type"   // 数据类型
	META_DATA_FRAGMENT_IDX         = "Data-Fragment-Idx"
	META_DATA_FRAGMENT_TOTAL       = "Data-Fragment-Total"
	META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename"
	META_RANGE_START               = "Data-Range-Start"
	META_RANGE_SIZE                = "Data-Range-Size"
)

type Frame struct {
	Len  uint32
	Flag uint32
	*Message
}

func NewFrame() *Frame {
	return &Frame{
		Len:     0,
		Flag:    0,
		Message: NewMessage(),
	}
}

func (f *Frame) String() string {
	return fmt.Sprintf("{Len:%d, Flag:%d, Sid:%s, Event:%s, Meta:%s, Data:%s}", f.Len, f.Flag, f.Sid, f.Event, f.Meta, f.Data)
}

func (f *Frame) CopyFrame(frame *Frame) {
	f.Len = frame.Len
	f.Flag = frame.Flag
	f.CopyMessage(frame.Message)
}

type Message struct {
	Sid   string
	Event string
	*Entity
}

func NewMessage() *Message {
	return &Message{
		Sid:    "",
		Event:  "",
		Entity: NewEntity(),
	}
}

func (m Message) CopyMessage(msg *Message) {
	m.Sid = msg.Sid
	m.Event = msg.Event
}

type Entity struct {
	Meta url.Values
	Data []byte
}

func NewEntity() *Entity {
	return &Entity{
		Meta: make(url.Values),
		Data: make([]byte, 0),
	}
}

func (e *Entity) MetaPut(name, val string) *Entity {
	e.Meta.Set(name, val)
	return e
}

func (e *Entity) MetaMapPut(m url.Values) *Entity {
	for k, v := range m {
		e.Meta[k] = v
	}
	return e
}

func (e *Entity) DataSet(data []byte) *Entity {
	e.Data = data
	return e
}

func (e *Entity) DataSize() int {
	return len(e.Data)
}

func (e *Entity) CopyEntity(entity *Entity) {
	e.MetaMapPut(entity.Meta)
	copy(e.Data, entity.Data)
}
