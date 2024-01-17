package core

import (
	"net"
	"net/url"

	"socketd/transport/core/message"
	"socketd/transport/stream"
)

type ChannelInternal interface {
	Channel

	SetSession(session Session)
	GetStream(sid string) stream.StreamInternal
	OnOpenFuture(future func(bool, error))
	DoOpenFuture(isOk bool, err error)
}

type Channel interface {
	GetAttachment(name string) any      //获取附件
	PutAttachment(name string, val any) //放置附件
	Close(int)                          //1协议关 2用户关
	IsClosed() int                      //是否已经关闭
	IsValid() bool                      //是否有效
	GetConfig() Config                  //获取配置
	GetLocalAddress() net.Addr          //获取本地地址
	GetRemoteAddress() net.Addr         //获取远程地址

	GetSession() Session               //获取session
	SetHandshake(handshake *Handshake) //设置握手信息
	GetHandshake() *Handshake          //获取握手信息

	Reconnect() (err error)                                              //手动重新连接
	SendConnect(url string, metas url.Values) (err error)                //发送握手
	SendConnectAck(connectMsg *message.Message) (err error)              //发送握手应答
	SendPing() (err error)                                               //发送ping（心跳）
	SendPong() (err error)                                               //发送pong（心跳）
	SendClose() (err error)                                              //发送关闭请求
	SendAlarm(from *message.Message, alarm string) (err error)           //发送告警
	Send(frame *message.Frame, stream stream.StreamInternal) (err error) //发送帧、流

	Retrieve(frame *message.Frame, stream stream.StreamInternal) //接收答复
	OnError(err error)                                           //出错时错误处理
}

type ChannelAssistant[T any] interface {
	IsValid(target T) bool                      // 判断是否有效
	Write(target T, frame *message.Frame) error // 写数据
	Close(target T) error                       // 关闭连接
	GetLocalAddress(target T) net.Addr          // 获取本地地址
	GetRemoteAddress(target T) net.Addr         // 获取远程地址
}

type ChannelSupporter[T ChannelAssistant[U], U any, V Config] interface {
	GetProcessor() Processor // 获取处理器
	GetConfig() V            // 获取配置
	GetAssistant() T         // 获取通道辅助器
}
