package stream

import (
	"time"

	"socketd/transport/core/message"
)

type StreamInternal interface {
	Stream

	Demands() int                                                           //获取需求量（0，1，2）
	Timeout() time.Duration                                                 //设置超时时间
	InsuranceStart(streamManger StreamManager, streamTimeout time.Duration) //保险开始（避免永久没有回调，造成内存不能释放）
	InsuranceCancel()                                                       //保险取消息
	OnReply(message *message.Frame)                                         //答复时
	OnError(err error)                                                      //异常时
	OnProgress(isSend bool, val int, max int)                               //进度时
}
