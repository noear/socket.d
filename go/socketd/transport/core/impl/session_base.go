package impl

import (
	"net/netip"
	"strings"

	"github.com/google/uuid"
	"socketd/transport/core"
)

// SessionBase
/**
 * @Description:会话基类
 * @Date 2024-01-11 20:50:20
 */
type SessionBase struct {
	channel       core.Channel
	sessionId     string
	localAddress  netip.Addr
	remoteAddress netip.Addr

	attMap map[string]any
}

func NewSessionBase(channel core.Channel) *SessionBase {
	return &SessionBase{channel: channel, attMap: make(map[string]any)}
}

// AttrHas
/**
 * @Description 会话的附件与通道的各自独立
 * @Date 2024-01-11 20:50:10
 */
func (b *SessionBase) AttrHas(name string) (ok bool) {
	_, ok = b.attMap[name]
	return
}

// Attr
/**
 * @Description 获取附件
 * @Date 2024-01-11 20:50:30
 */
func (b *SessionBase) Attr(name string) (val any) {
	val, _ = b.attMap[name]
	return
}

// AttrOrDefault
/**
 * @Description 获取属性或默认值
 * @Date 2024-01-11 20:53:13
 */
func (b *SessionBase) AttrOrDefault(name string, def any) any {
	val, ok := b.attMap[name]
	if ok {
		return val
	}
	return def
}

// AttrPut
/**
 * @Description 设置附件
 * @Date 2024-01-11 20:55:27
 */
func (b *SessionBase) AttrPut(name string, val any) {
	b.attMap[name] = val
}

func (b *SessionBase) SessionId() string {
	return b.sessionId
}

func (b *SessionBase) GenerateId() string {
	return strings.Replace(uuid.NewString(), "-", "", -1)
}
