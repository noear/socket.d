package core

import (
	"socketd/transport/core/message"
)

// FragmentAggregator
/**
 * @Description:分片聚合器
 * @Date 2024-01-10 20:57:43
 */
type FragmentAggregator interface {
	GetSid() string                           // 获取流Id
	GetDataStreamSize() int                   // 数据流大小
	GetDataLength() int                       // 数据总长度
	Add(index int, message *message.Message)  // 添加分片
	Get() (frame *message.Message, err error) // 获取聚合帧
}
