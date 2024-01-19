package ws

import (
	"context"
	"errors"
	"log/slog"
	"time"

	"socketd/transport/client"
	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/impl"

	"github.com/gorilla/websocket"
)

type ClientConnector struct {
	*client.ConnectBase[*Client]

	cfg          *client.Config
	real         *websocket.Conn
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

func (cc *ClientConnector) Connect() (channel core.ChannelInternal, err error) {
	cc.real, _, err = websocket.DefaultDialer.Dial(cc.cfg.GetUrl(), nil)
	if err != nil {
		return nil, err
	}

	channel = impl.NewChannelDefault[*ChannelAssistant, *websocket.Conn, *client.Config](cc.real, cc.ConnectBase.GetClient())

	ctx, cancel := context.WithCancel(context.Background())
	cc.clientCancel = cancel
	go cc.Receive(ctx, channel, cc.real)

	err = channel.SendConnect(cc.cfg.GetLinkUrl(), cc.cfg.GetMetaMap())
	if err != nil {
		return nil, err
	}
	timer := time.NewTimer(cc.GetClient().GetConfig().GetConnectTimeout())
	select {
	case <-timer.C:
		return nil, errors.New("connect timeout")
	case <-cc.connackChan:
		return channel, nil
	}
}

func (cc *ClientConnector) Receive(ctx context.Context, channel core.ChannelInternal, conn *websocket.Conn) {
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
					channel.OnOpenFuture(func(b bool, err error) {})
					cc.connackChan <- struct{}{}
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
