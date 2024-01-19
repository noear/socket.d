package client

import (
	"socketd/transport/core/message"
	"socketd/transport/stream"
	"time"
)

// Session
/**
 * @Description:客户会话
 * @Date 2024-01-15 21:15:06
 */
type Session interface {
	IsValid() bool                                                                                                //是否有效
	SessionId() string                                                                                            //获取会话Id
	Reconnect() error                                                                                             //手动重连（一般是自动）
	Send(event string, entity *message.Entity) (stream.SendStream, error)                                         //发送
	SendAndRequest(event string, entity *message.Entity, timeout time.Duration) (stream.RequestStream, error)     //发送并请求
	SendAndSubscribe(event string, entity *message.Entity, timeout time.Duration) (stream.SubscribeStream, error) //发送并订阅（答复结束之前，不限答复次数）
}
