package tcp

import (
	"context"
	"errors"
	"log/slog"
	"net"
	"time"

	"socketd/transport/client"
	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/impl"
)

type ClientConnector struct {
	*client.ConnectBase[*Client]

	cfg          *client.Config
	real         *net.TCPConn
	clientCancel context.CancelFunc
	connackChan  chan struct{}
}

func NewClientConnector(clt *Client) *ClientConnector {
	var cc = new(ClientConnector)
	cc.cfg = clt.cfg
	cc.ConnectBase = client.NewConnectorBase[*Client](clt)
	cc.connackChan = make(chan struct{})
	return cc
}

func (cc *ClientConnector) Connect() (core.Channel, error) {
	tcpAddr, err := net.ResolveTCPAddr(cc.cfg.GetSchema(), cc.cfg.GetAddress())
	if err != nil {
		return nil, err
	}

	cc.real, err = net.DialTCP(cc.cfg.GetSchema(), nil, tcpAddr)
	if err != nil {
		return nil, err
	}

	if cc.cfg.GetReadBufferSize() > 0 {
		err := cc.real.SetReadBuffer(cc.cfg.GetReadBufferSize())
		if err != nil {
			return nil, err
		}
	}
	if cc.cfg.GetWriteBufferSize() > 0 {
		err := cc.real.SetWriteBuffer(cc.cfg.GetWriteBufferSize())
		if err != nil {
			return nil, err
		}
	}

	var channel core.ChannelInternal = impl.NewChannelDefault[*ChannelAssistant, *net.TCPConn, *client.Config](cc.real, cc.ConnectBase.GetClient())

	ctx, cancel := context.WithCancel(context.Background())
	cc.clientCancel = cancel
	go cc.Receive(ctx, channel, cc.real)

	err = channel.SendConnect(cc.cfg.GetLinkUrl(), cc.cfg.GetMetaMap())

	timer := time.NewTimer(cc.GetClient().GetConfig().GetConnectTimeout())
	select {
	case <-timer.C:
		return nil, errors.New("connect timeout")
	case <-cc.connackChan:
	}
	return channel, nil
}

func (cc *ClientConnector) Receive(ctx context.Context, channel core.ChannelInternal, conn *net.TCPConn) {
	_client := cc.ConnectBase.GetClient()
	for {
		select {
		case <-ctx.Done():
			return
		default:
			if channel.IsClosed() > 0 {
				_client.GetProcessor().OnClose(channel)
				return
			}
			frame, err := _client.GetAssistant().Read(conn)
			if err != nil {
				slog.Debug("conn interrupt", "err", err)
				_client.GetProcessor().OnError(channel, err)
			}
			if frame != nil {
				if frame.Flag == constant.FrameConnack {
					cc.connackChan <- struct{}{}
					continue
				}
				err := _client.GetProcessor().OnReceive(channel, frame)
				if err != nil {
					slog.Debug("conn interrupt", "err", err)
					_client.GetProcessor().OnError(channel, err)
					return
				}
			}
		}
	}
}

func (cc *ClientConnector) Close() {
	if cc.real != nil {
		if err := cc.real.Close(); err != nil {
			slog.Debug("conn close", "err", err)
		}
		if cc.clientCancel != nil {
			cc.clientCancel()
		}
	}
}
