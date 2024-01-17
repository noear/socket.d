package impl

import (
	"errors"
	"fmt"
	"log/slog"
	"strconv"

	"socketd/transport/core/constant"
	"socketd/transport/stream"

	"socketd/transport/core"
	"socketd/transport/core/message"
)

type Processor struct {
	listener core.Listener
}

func NewProcessor() *Processor {
	return &Processor{}
}

func (p *Processor) SetListener(listener core.Listener) {
	p.listener = listener
}

func (p *Processor) OnReceive(channel core.ChannelInternal, frame *message.Frame) error {
	slog.Debug("OnReceive", "frame", frame)

	switch frame.Flag {
	case constant.FrameConnect:
		var handshake = core.NewHandshake(frame.Message)
		channel.SetHandshake(handshake)

		channel.OnOpenFuture(func(r bool, e error) {
			if channel.IsValid() {
				if e != nil {
					channel.Close(constant.CLOSE3_ERROR)
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
	case constant.FrameConnack:
		var handshake = core.NewHandshake(frame.Message)
		channel.SetHandshake(handshake)

		p.OnOpen(channel)
	default:
		if channel.GetHandshake() == nil {
			channel.Close(constant.CLOSE1_PROTOCOL)

			// 握手失败
			if frame.Flag == constant.FrameClose {
				return errors.New("connection request was rejected")
			}

			slog.Warn(fmt.Sprintf("%s channel handshake is null, sessionId = %s", channel.GetConfig().GetRoleName(), channel.GetSession().SessionId()))
			return nil
		}

		switch frame.Flag {
		case constant.FramePing:
			channel.SendPong()
		case constant.FramePong:
			channel.SendPong()
		case constant.FrameClose:
			channel.Close(constant.CLOSE1_PROTOCOL)
			p.OnCloseInternal(channel)
		case constant.FrameAlarm:
			//TODO
		case constant.FrameMessage:
			fallthrough
		case constant.FrameRequest:
			fallthrough
		case constant.FrameSubscribe:
			p.OnReceiveDo(channel, frame, false)
		case constant.FrameReply:
			p.OnReceiveDo(channel, frame, true)
		case constant.FrameReplyEnd:
			p.OnReceiveDo(channel, frame, true)
		default:
			channel.Close(constant.CLOSE2_PROTOCOL_ILLEGAL)
			p.OnCloseInternal(channel)
		}
	}
	return nil
}

func (p *Processor) OnReceiveDo(channel core.ChannelInternal, frame *message.Frame, reply bool) {
	var stm stream.StreamInternal
	var streamIndex = 0
	var streamTotal = 1
	if reply {
		stm = channel.GetStream(frame.Message.Sid)
	}
	if channel.GetConfig().GetFragmentHandler().AggrEnable() {
		var fragmentIdxStr = frame.Message.Meta.Get(constant.META_DATA_FRAGMENT_IDX)
		if fragmentIdxStr != "" {
			streamIndex, _ = strconv.Atoi(fragmentIdxStr)
			frameNew, _ := channel.GetConfig().GetFragmentHandler().AggrFragment(channel, streamIndex, frame.Message)

			if stm != nil {
				streamTotal, _ = strconv.Atoi(frame.Message.Meta.Get(constant.META_DATA_FRAGMENT_TOTAL))
			}
			if frameNew == nil {
				if stm != nil {
					stm.OnProgress(false, streamIndex, streamTotal)
				}
				return
			}
			frame.Message = frameNew
		}
	}

	//执行接收处理
	if reply {
		if stm != nil {
			stm.OnProgress(false, streamIndex, streamTotal)
		}
		channel.Retrieve(frame, stm)
	} else {
		p.OnMessage(channel, frame)
	}
}

func (p *Processor) OnOpen(channel core.ChannelInternal) {
	p.listener.OnOpen(channel.GetSession())
	channel.DoOpenFuture(true, nil)
}

func (p *Processor) OnMessage(channel core.ChannelInternal, message *message.Frame) {
	var err = p.listener.OnMessage(channel.GetSession(), message)
	if err != nil {
		p.OnError(channel, err)
	}
}

func (p *Processor) OnClose(channel core.ChannelInternal) {
	if channel.IsClosed() == 0 {
		p.OnCloseInternal(channel)
	}
}

func (p *Processor) OnCloseInternal(channel core.ChannelInternal) {
	p.listener.OnClose(channel.GetSession())
}

func (p *Processor) OnError(channel core.ChannelInternal, err error) {
	p.listener.OnError(channel.GetSession(), err)
}
