package impl

import (
	"net/url"
	"strings"

	"github.com/google/uuid"
	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/message"
)

//var _ core.Channel = new(ChannelBase)

type ChannelBase struct {
	core.Channel

	config      core.Config
	attachments map[string]any
	handshake   *core.Handshake
	isClosed    int
}

func NewChannelBase(config core.Config) *ChannelBase {
	return &ChannelBase{config: config}
}

func (c *ChannelBase) GetConfig() core.Config {
	return c.config
}

func (c *ChannelBase) GetAttachment(name string) any {
	val, ok := c.attachments[name]
	if ok {
		return val
	}
	return nil
}

func (c *ChannelBase) PutAttachment(name string, val any) {
	if val == nil {
		delete(c.attachments, name)
		return
	}
	c.attachments[name] = val
}

func (c *ChannelBase) IsClosed() int {
	return c.isClosed
}

func (c *ChannelBase) Close(code int) {
	c.isClosed = code
	//for k := range c.attachments {
	//	delete(c.attachments, k)
	//}
}

func (c *ChannelBase) SetHandshake(handshake *core.Handshake) {
	c.handshake = handshake
}

func (c *ChannelBase) GetHandshake() *core.Handshake {
	return c.handshake
}

func (c *ChannelBase) SendConnect(event string, metas map[string]string) (err error) {
	sid := strings.Replace(uuid.NewString(), "-", "", -1)
	var uv = url.Values{}
	for k, v := range metas {
		uv.Set(k, v)
	}
	frame := message.NewFrame(constant.FrameConnect, message.NewMessage(sid, event, message.NewEntity(uv, nil)))
	return c.Send(frame, nil)
}

func (c *ChannelBase) SendConnectAck(connectMsg *message.Message) (err error) {
	var frame = &message.Frame{}
	frame.Flag = constant.FrameConnack
	frame.Message = connectMsg
	return c.Send(frame, nil)
}

func (c *ChannelBase) SendPing() (err error) {
	var frame = &message.Frame{}
	frame.Flag = constant.FramePing
	return c.Send(frame, nil)
}

func (c *ChannelBase) SendPong() (err error) {
	var frame = &message.Frame{}
	frame.Flag = constant.FramePong
	return c.Send(frame, nil)
}

func (c *ChannelBase) SendClose() (err error) {
	var frame = &message.Frame{}
	frame.Flag = constant.FrameClose
	return c.Send(frame, nil)
}

func (c *ChannelBase) SendAlarm(from *message.Message, alarm string) (err error) {
	// TODO panic("implement me")
	panic("implement me")
	return nil
}
