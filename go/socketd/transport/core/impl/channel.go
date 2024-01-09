package impl

import (
	"bufio"
	"errors"
	"io"
	"net/netip"
	"strings"

	"socketd/transport/core"

	"github.com/google/uuid"
)

type Channel struct {
	handshake   *core.Handshake
	isClosed    int
	attachments map[string]any
}

func NewChannel() core.Channel {
	return &Channel{}
}

func (c *Channel) SetSession(session core.Session) {}

func (c *Channel) GetStream() bufio.Reader {
	return bufio.Reader{}
}

func (c *Channel) OnOpenFuture(func(bool, error)) {}

func (c *Channel) DoOpenFuture(bool, error) {}

func (c *Channel) Close(code int) {
	c.isClosed = code
	c.attachments = make(map[string]any)
}

func (c *Channel) IsValid() bool {
	return true
}

func (c *Channel) IsClosed() int {
	return c.isClosed
}

func (c *Channel) GetLocalAddress() netip.Addr {
	return netip.Addr{}
}

func (c *Channel) GetRemoteAddress() netip.Addr {
	return netip.Addr{}
}

func (c *Channel) GetSession() core.Session {
	return nil
}

func (c *Channel) SetHandshake(handshake *core.Handshake) {
	c.handshake = handshake
}

func (c *Channel) GetHandshake() *core.Handshake {
	return c.handshake
}

func (c *Channel) Reconnect() error {
	return nil
}

func (c *Channel) SendConnect(uri string) (err error) {
	var frame = core.Frame{}
	frame.Sid = strings.Replace(uuid.NewString(), "-", "", -1)
	frame.Flag = core.ConnectFrame
	return c.Send(frame, nil)
}

func (c *Channel) SendConnectAck(connectMsg core.Message) (err error) {
	var frame = core.Frame{}
	frame.Flag = core.ConnackFrame
	frame.Message = connectMsg
	return c.Send(frame, nil)
}

func (c *Channel) SendPing() (err error) {
	var frame = core.Frame{}
	frame.Flag = core.PingFrame
	return c.Send(frame, nil)
}

func (c *Channel) SendPong() (err error) {
	var frame = core.Frame{}
	frame.Flag = core.PongFrame
	return c.Send(frame, nil)
}

func (c *Channel) SendClose() (err error) {
	var frame = core.Frame{}
	frame.Flag = core.CloseFrame
	return c.Send(frame, nil)
}

func (c *Channel) SendAlarm(from core.Message, alarm string) (err error) {
	return nil
}

func (c *Channel) Send(frame core.Frame, stream io.Writer) (err error) {
	if c.isClosed != 0 {
		return errors.New("connection lose")
	}

	return nil
}

func (c *Channel) Retrieve(frame core.Frame, stream io.Reader) {
	
}

func (c *Channel) OnError(f func(err error)) {

}
