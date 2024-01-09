package impl

import (
	"bytes"
	"fmt"

	"socketd/transport/core"
)

type Processor struct {
	listener core.Listener
}

func NewProcessor() core.Processor {
	return &Processor{}
}

func (p *Processor) SetListener(listener core.Listener) {
	p.listener = listener
}

func (p *Processor) OnReceive(channel core.Channel, frame core.Frame) {
	//TODO 日志记录
	fmt.Println("OnReceive", frame)
	switch frame.Flag {
	case core.ConnectFrame:
		var handshake = core.NewHandshake(frame.Message)
		channel.SetHandshake(handshake)

		channel.OnOpenFuture(func(r bool, e error) {
			if channel.IsValid() {
				if e != nil {
					channel.Close(3)
					p.OnCloseInternal(channel)
				} else {
					var err = channel.SendConnectAck(frame.Message)
					if err != nil {
						p.OnError(channel, err)
					}
				}
			}

		})
		p.OnOpen(channel)
	case core.ConnackFrame:
		var handshake = core.NewHandshake(frame.Message)
		channel.SetHandshake(handshake)

		p.OnOpen(channel)
	default:
		if channel.GetHandshake() == nil {
			channel.Close(1)

			// 握手失败
			if frame.Flag == core.CloseFrame {
				channel.SendPong()
			}

			// TODO 日志记录
			return
		}

		switch frame.Flag {
		case core.PingFrame:
			channel.SendPong()
		case core.PongFrame:
			channel.SendPong()
		case core.CloseFrame:
			channel.Close(1)
			p.OnCloseInternal(channel)
		case core.AlarmFrame:
			//TODO
		case core.MessageFrame:
			fallthrough
		case core.RequestFrame:
			fallthrough
		case core.SubscribeFrame:
			p.OnReceiveDo(channel, frame, false)
		case core.ReplyFrame:
			p.OnReceiveDo(channel, frame, true)
		case core.ReplyEndFrame:
			p.OnReceiveDo(channel, frame, true)
		default:
			channel.Close(2)
			p.OnCloseInternal(channel)
		}
	}

}

func (p *Processor) OnReceiveDo(channel core.Channel, frame core.Frame, reply bool) {
	var buf = &bytes.Buffer{}

	// TODO 聚合，分片

	if reply {
		channel.Retrieve(frame, buf)
	} else {
		p.OnMessage(channel, frame.Message)
	}
}

func (p *Processor) OnOpen(channel core.Channel) {
	p.listener.OnOpen(channel.GetSession())
	channel.DoOpenFuture(true, nil)
}

func (p *Processor) OnMessage(channel core.Channel, message core.Message) {
	var err = p.listener.OnMessage(channel.GetSession(), message)
	if err != nil {
		p.OnError(channel, err)
	}
}

func (p *Processor) OnClose(channel core.Channel) {
	if channel.IsClosed() == 0 {
		p.OnCloseInternal(channel)
	}
}

func (p *Processor) OnCloseInternal(channel core.Channel) {
	p.listener.OnClose(channel.GetSession())
}

func (p *Processor) OnError(channel core.Channel, err error) {
	p.listener.OnError(channel.GetSession(), err)
}
