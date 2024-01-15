package core

import (
	"net/url"

	"socketd/transport/core/message"
)

// // 握手信息
// type Handshake interface {
// 	Version() string //协议版本
// 	Uri() string
// 	Param(string) string                  //获取参数
// 	ParamMap() map[string]string          //获取参数集
// 	ParamOrDefault(string, string) string //获取参数，不存在则返回默认值
// 	ParamPut(string, string)              //放置参数
// }

type Handshake struct {
	uri     string
	path    string
	version string
	params  url.Values
	source  *message.Message
}

func NewHandshake(source *message.Message) *Handshake {
	var hs = new(Handshake)

	hs.source = source
	hs.uri = string(source.Data)
	hs.version = source.Meta.Get("Socket.D")
	hs.params = source.Meta
	return &Handshake{}
}

func (h *Handshake) Version() string {
	return h.version
}

func (h *Handshake) Uri() string {
	return h.uri
}

func (h *Handshake) Path() string {
	return h.path
}

func (h *Handshake) Param(name string) string {
	return h.params.Get(name)
}

func (h *Handshake) Params() url.Values {
	return h.params
}

func (h *Handshake) ParamOrDefault(name string, def string) (value string) {
	value = h.params.Get(value)
	if value == "" {
		value = def
	}
	return def
}

func (h *Handshake) ParamPut(name string, value string) {
	h.params.Set(name, value)
}
