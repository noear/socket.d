package impl

import (
	"fmt"
	"net"
	"time"

	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/message"
	"socketd/transport/stream"
	"socketd/transport/stream/impl"
)

//var _ core.Session = new(SessionDefault)

// SessionDefault
/**
 * @Description: 会话默认实现
 */
type SessionDefault struct {
	*SessionBase // 继承

	pathNew string
}

func NewSessionDefault(channel core.Channel) *SessionDefault {
	return &SessionDefault{SessionBase: NewSessionBase(channel)}
}

// IsValid
/**
 * @Description 是否有效
 */
func (d *SessionDefault) IsValid() bool {
	return d.channel.IsValid()
}

// LocalAddress
/**
 * @Description 获取本地地址
 */
func (d *SessionDefault) LocalAddress() net.Addr {
	return d.channel.GetLocalAddress()
}

// RemoteAddress
/**
 * @Description 获取远程地址
 */
func (d *SessionDefault) RemoteAddress() net.Addr {
	return d.channel.GetRemoteAddress()
}

// Handshake
/**
 * @Description 获取握手信息
 */
func (d *SessionDefault) Handshake() *core.Handshake {
	return d.channel.GetHandshake()
}

// Param
/**
 * @Description 获取握手参数
 */
func (d *SessionDefault) Param(name string) string {
	return d.Handshake().Param(name)
}

// ParamOrDefault
/**
 * @Description 获取握手参数或默认值
 */
func (d *SessionDefault) ParamOrDefault(name, def string) string {
	return d.Handshake().ParamOrDefault(name, def)
}

// Path
/**
 * @Description 获取路径
 */
func (d *SessionDefault) Path() string {
	if d.pathNew == "" {
		return d.Handshake().Path()
	}
	return d.pathNew
}

// PathNew
/**
 * @Description 设置新路径
 */
func (d *SessionDefault) PathNew(pathNew string) {
	d.pathNew = pathNew
}

// Reconnect
/**
 * @Description 手动重连（一般是自动）
 * @return error
 */
func (d *SessionDefault) Reconnect() error {
	return d.channel.Reconnect()
}

// SendPing
/**
 * @Description 手动发送 Ping（一般是自动）
 * @return error
 */
func (d *SessionDefault) SendPing() error {
	return d.channel.SendPing()
}

func (d *SessionDefault) SendAlarm(from *message.Frame, alarm string) error {
	//TODO implement me
	panic("implement me")
}

func (d *SessionDefault) Send(event string, entity *message.Entity) (stream.SendStream, error) {
	var frame = message.NewFrame(constant.FrameMessage, message.NewMessage(d.GenerateId(), event, entity))
	stm := impl.NewSendStream(frame.Message.Sid)
	err := d.channel.Send(frame, stm)
	return stm, err
}

func (d *SessionDefault) SendAndRequest(event string, entity *message.Entity, timeout time.Duration) (stream.SendStream, error) {
	var frame = message.NewFrame(constant.FrameMessage, message.NewMessage(d.GenerateId(), event, entity))

	if timeout < 0 {
		timeout = d.channel.GetConfig().GetStreamTimeout()
	}
	if timeout == 0 {
		timeout = d.channel.GetConfig().GetRequestTimeout()
	}

	stm := impl.NewRequestStream(frame.Message.Sid, timeout)
	err := d.channel.Send(frame, stm)
	return stm, err
}

func (d *SessionDefault) Reply(from *message.Frame, entity *message.Entity) error {
	from.Flag = constant.FrameReply
	from.Message = message.NewMessage(from.Message.Sid, from.Message.Event, entity)
	return d.channel.Send(from, nil)
}

func (d *SessionDefault) ReplyEnd(from *message.Frame, entity *message.Entity) error {
	from.Flag = constant.FrameReplyEnd
	from.Message = message.NewMessage(from.Message.Sid, from.Message.Event, entity)
	return d.channel.Send(from, nil)
}

func (d *SessionDefault) Close() {
	//TODO log
	fmt.Printf("%s  session will be closed, sessionId = %s", d.channel.GetConfig().GetRoleName(), d.sessionId)

	if d.channel.IsValid() {
		if err := d.channel.SendClose(); err != nil {
			fmt.Printf("%s channel send_close, error %s", d.channel.GetConfig().GetRoleName(), d.sessionId)
		}
	}
	d.channel.Close(constant.CLOSE4_USER)
}
