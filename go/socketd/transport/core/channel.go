package core

import (
	"bufio"
	"io"
	"net/netip"
)

type ChannelInternal interface {
	SetSession(session Session)
	GetStream() bufio.Reader
	OnOpenFuture(func(bool, error))
	DoOpenFuture(bool, error)
}

type Channel interface {
	ChannelInternal

	Close(int)     // 1协议关 2用户关
	IsClosed() int // 是否已经关闭
	IsValid() bool // 是否有效

	GetLocalAddress() netip.Addr  //获取本地地址
	GetRemoteAddress() netip.Addr //获取远程地址

	GetSession() Session               //获取session
	SetHandshake(handshake *Handshake) //设置握手信息
	GetHandshake() *Handshake          //获取握手信息

	Reconnect() (err error)                           //手动重新连接
	SendConnect(url string) (err error)               //发送握手
	SendConnectAck(connectMsg Message) (err error)    //发送握手应答
	SendPing() (err error)                            //发送ping（心跳）
	SendPong() (err error)                            //发送pong（心跳）
	SendClose() (err error)                           //发送关闭请求
	SendAlarm(from Message, alarm string) (err error) //发送告警
	Send(frame Frame, stream io.Writer) (err error)   //发送帧、流

	Retrieve(frame Frame, stream io.Reader) //接收答复
	OnError(func(err error))                //出错时错误处理
}
