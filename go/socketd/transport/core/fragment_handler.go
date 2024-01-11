package core

import (
	"socketd/transport/core/message"
)

// FragmentHandler
/**
 * @Description: 数据分片处理（分片必须做，聚合可开关）
 * @Date 2024-01-10 20:59:58
 */
type FragmentHandler interface {
	SplitFragment(channel Channel, stream any, message *message.Message, accept func(entity *message.Entity) (err error)) (err error) //拆割分片
	AggrFragment(channel Channel, fragmentIndex int, message *message.Message) (frame *message.Message, err error)                    //聚合分片
	AggrEnable() bool
}
