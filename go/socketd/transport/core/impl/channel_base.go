package impl

import (
	"strings"

	"socketd/transport/core"

	"github.com/google/uuid"
)

var _ core.Channel = new(ChannelBase)

type ChannelBase struct {
	config      core.Config
	attachments map[string]any
	handshake   *core.Handshake
	isClosed    int

	channel core.Channel
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

func (c *ChannelBase) close(code int) {
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

func (c *ChannelBase) SendConnect(uri string) (err error) {
	var frame = core.Frame{}
	frame.Sid = strings.Replace(uuid.NewString(), "-", "", -1)
	frame.Flag = core.ConnectFrame
	return c.channel.Send(frame, nil)
}

func (c *ChannelBase) SendConnectAck(connectMsg *core.Message) (err error) {
	var frame = core.Frame{}
	frame.Flag = core.ConnackFrame
	frame.Message = connectMsg
	return c.channel.Send(frame, nil)
}

func (c *ChannelBase) SendPing() (err error) {
	var frame = core.Frame{}
	frame.Flag = core.PingFrame
	return c.channel.Send(frame, nil)
}

func (c *ChannelBase) SendPong() (err error) {
	var frame = core.Frame{}
	frame.Flag = core.PongFrame
	return c.channel.Send(frame, nil)
}

func (c *ChannelBase) SendClose() (err error) {
	var frame = core.Frame{}
	frame.Flag = core.CloseFrame
	return c.channel.Send(frame, nil)
}

func (c *ChannelBase) SendAlarm(from *core.Message, alarm string) (err error) {
	return nil
}
