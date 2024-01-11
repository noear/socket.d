package impl

import (
	"bytes"
	"fmt"

	"socketd/transport/core"
	"socketd/transport/core/message"
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

func (p *Processor) OnReceive(channel core.Channel, frame *message.Frame) {
	//TODO 日志记录
	fmt.Println("OnReceive", frame)
	switch frame.Flag {
	case message.ConnectFrame:
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
	case message.ConnackFrame:
		var handshake = core.NewHandshake(frame.Message)
		channel.SetHandshake(handshake)

		p.OnOpen(channel)
	default:
		if channel.GetHandshake() == nil {
			channel.Close(1)

			// 握手失败
			if frame.Flag == message.CloseFrame {
				channel.SendPong()
			}

			// TODO 日志记录
			return
		}

		switch frame.Flag {
		case message.PingFrame:
			channel.SendPong()
		case message.PongFrame:
			channel.SendPong()
		case message.CloseFrame:
			channel.Close(1)
			p.OnCloseInternal(channel)
		case message.AlarmFrame:
			//TODO
		case message.MessageFrame:
			fallthrough
		case message.RequestFrame:
			fallthrough
		case message.SubscribeFrame:
			p.OnReceiveDo(channel, frame, false)
		case message.ReplyFrame:
			p.OnReceiveDo(channel, frame, true)
		case message.ReplyEndFrame:
			p.OnReceiveDo(channel, frame, true)
		default:
			channel.Close(2)
			p.OnCloseInternal(channel)
		}
	}

}

func (p *Processor) OnReceiveDo(channel core.Channel, frame *message.Frame, reply bool) {
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

func (p *Processor) OnMessage(channel core.Channel, message *message.Message) {
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
