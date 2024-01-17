package client

import (
	"context"
	"fmt"
	"log/slog"
	"net"
	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/impl"
	"socketd/transport/core/message"
	"socketd/transport/stream"
)

type Channel struct {
	*impl.ChannelBase //继承

	connector        Connector             //连接器
	real             core.Channel          //真实通道
	heartbeatHandler core.HeartbeatHandler //心跳处理
	heartbeatCancel  context.CancelFunc
}

func NewClientChannel(real core.Channel, connector Connector) *Channel {
	var cb = &Channel{}
	cb.ChannelBase = impl.NewChannelBase(real.GetConfig())
	cb.connector = connector
	cb.real = real
	cb.heartbeatHandler = connector.GetHeartbeatHandler()
	if cb.heartbeatHandler == nil {
		cb.heartbeatHandler = impl.HeartbeatHandlerDefault
	}
	return cb
}

func (cc *Channel) InitHeartbeat() {

}

func (cc *Channel) IsValid() bool {
	if cc.real == nil {
		return false
	}
	return cc.real.IsValid()
}

func (cc *Channel) IsClosed() int {
	if cc.real == nil {
		return 0
	}
	return cc.real.IsClosed()
}

func (cc *Channel) GetLocalAddress() net.Addr {
	return cc.real.GetLocalAddress()
}

func (cc *Channel) GetRemoteAddress() net.Addr {
	return cc.real.GetRemoteAddress()
}

func (cc *Channel) HeartbeatHandle() (err error) {
	if cc.real != nil {
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
		return fmt.Errorf("Client channel heartbeat failed %v", err)
	}
	return nil
}

func (cc *Channel) Send(frame *message.Frame, stream stream.StreamInternal) (err error) {

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

func (cc *Channel) Retrieve(frame *message.Frame, stream stream.StreamInternal) {
	cc.real.Retrieve(frame, stream)
}

func (cc *Channel) GetSession() core.Session {
	return cc.real.GetSession()
}

func (cc *Channel) Reconnect() (err error) {
	//if err = cc.InitHeartbeat; err != nil {
	//	return
	//}
	//err = cc.PrepareCheck()
	return
}

func (cc *Channel) Close(code int) {
	go func() {
		cc.real.Close(code)
		cc.connector.Close()
	}()
}

func (cc *Channel) PrepareCheck() bool {
	if cc.real == nil || !cc.IsValid() {
		cc.real, _ = cc.connector.Connect()
		return true
	}
	return false
}
