package client

import (
	"context"
	"fmt"
	"log/slog"
	"net"
	"time"

	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/impl"
	"socketd/transport/core/message"
	"socketd/transport/stream"
)

type ClientChannel[T core.ChannelAssistant[U], U any] struct {
	*impl.ChannelBase

	real             core.Channel          //真实通道
	connector        Connector             //连接器
	heartbeatHandler core.HeartbeatHandler //心跳处理
	heartbeatCancel  context.CancelFunc
}

func NewClientChannel[T core.ChannelAssistant[U], U any](real core.Channel, connector Connector) core.Channel {
	var cb = &ClientChannel[T, U]{}
	cb.ChannelBase = impl.NewChannelBase(real.GetConfig())
	cb.connector = connector
	cb.real = real
	cb.heartbeatHandler = connector.GetHeartbeatHandler()
	if cb.heartbeatHandler == nil {
		cb.heartbeatHandler = impl.HeartbeatHandlerDefault
	}
	cb.InitHeartbeat()
	return cb
}

func (cc *ClientChannel[T, U]) InitHeartbeat() {
	if cc.heartbeatCancel != nil {
		cc.heartbeatCancel()
	}

	var ctx context.Context
	ctx, cc.heartbeatCancel = context.WithCancel(context.Background())
	timer := time.NewTicker(cc.connector.GetHeartbeatInterval())

	if cc.connector.AutoReconnect() {
		go func() {
			for {
				select {
				case <-ctx.Done():
					return
				case <-timer.C:
					if err := cc.HeartbeatHandle(); err != nil {
						slog.Warn(fmt.Sprintf("client channel heartbeat error:%s", err))
					}
				}
			}
		}()
	}
}

func (cc *ClientChannel[T, U]) IsValid() bool {
	if cc.real == nil {
		return false
	}
	return cc.real.IsValid()
}

func (cc *ClientChannel[T, U]) IsClosed() int {
	if cc.real == nil {
		return 0
	}
	return cc.real.IsClosed()
}

func (cc *ClientChannel[T, U]) GetLocalAddress() net.Addr {
	return cc.real.GetLocalAddress()
}

func (cc *ClientChannel[T, U]) GetRemoteAddress() net.Addr {
	return cc.real.GetRemoteAddress()
}

func (cc *ClientChannel[T, U]) HeartbeatHandle() (err error) {
	if cc.real != nil {
		//说明握手未成功
		if cc.real.GetHandshake() == nil {
			return
		}
		if cc.real.IsClosed() == constant.CLOSE4_USER {
			slog.Debug(fmt.Sprintf("Client channel is closed (pause heartbeat), sessionId = %s", cc.GetSession().SessionId()))
		}
		return
	}

	if cc.PrepareCheck() {
		return
	}
	if err = cc.heartbeatHandler(cc.GetSession()); err != nil {
		if cc.connector.AutoReconnect() {
			cc.real.Close(constant.CLOSE3_ERROR)
			cc.real = nil
		}
		return fmt.Errorf("client channel heartbeat failed %v", err)
	}
	return nil
}

func (cc *ClientChannel[T, U]) Send(frame *message.Frame, stream stream.StreamInternal) (err error) {

	if cc.PrepareCheck() {
		return
	}
	if err = cc.real.Send(frame, stream); err != nil {
		if cc.connector.AutoReconnect() {
			cc.real.Close(constant.CLOSE3_ERROR)
			cc.real = nil
		}
		return fmt.Errorf("Client channel send failed %v", err)
	}
	return
}

func (cc *ClientChannel[T, U]) Retrieve(frame *message.Frame, stream stream.StreamInternal) {
	cc.real.Retrieve(frame, stream)
}

func (cc *ClientChannel[T, U]) GetSession() core.Session {
	return cc.real.GetSession()
}

func (cc *ClientChannel[T, U]) Reconnect() (err error) {
	//if err = cc.InitHeartbeat; err != nil {
	//	return
	//}
	//err = cc.PrepareCheck()
	return
}

func (cc *ClientChannel[T, U]) Close(code int) {
	go func() {
		cc.real.Close(code)
		cc.connector.Close()
	}()
}

func (cc *ClientChannel[T, U]) PrepareCheck() bool {
	if cc.real == nil || !cc.IsValid() {
		cc.real, _ = cc.connector.Connect()
		return true
	}
	return false
}
