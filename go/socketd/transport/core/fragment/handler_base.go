package fragment

import (
	"bytes"
	"fmt"
	"strconv"

	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/message"
)

//var _ core.FragmentHandler = new(HandlerBase)

type HandlerBase struct {
}

// SplitFragment
/**
 * @Description 获取下个分片
 * @Date 2024-01-10 22:31:25
 */
func (h HandlerBase) SplitFragment(channel core.Channel, stream any, msg *message.Message, accept func(entity *message.Entity) (err error)) (err error) {
	if msg.DataSize() > channel.GetConfig().GetFragmentSize() {
		var fragmentTotal = msg.DataSize() / channel.GetConfig().GetFragmentSize()
		if msg.DataSize()%channel.GetConfig().GetFragmentSize() > 0 {
			fragmentTotal += 1
		}

		reader := bytes.NewReader(msg.Data)
		var fragmentIndex = 0

		for {
			fragmentIndex += 1

			var data = make([]byte, 0, channel.GetConfig().GetFragmentSize())
			n, err := reader.Read(data)
			if err == nil {
				var fragmentEntity = &message.Entity{Data: data[:n-1]}
				if fragmentIndex == 1 {
					fragmentEntity.MetaMapPut(msg.Meta)
				}
				fragmentEntity.MetaPut(constant.META_DATA_FRAGMENT_IDX, strconv.Itoa(fragmentIndex))
				fragmentEntity.MetaPut(constant.META_DATA_FRAGMENT_TOTAL, strconv.Itoa(fragmentTotal))
				if err := accept(fragmentEntity); err != nil {
					//TODO 日志记录
					fmt.Println(err)
				}
				if stream != nil {
					//stream.onProgress(true, fragmentIndex, fragmentTotal);
				}
			}

		}
		return
	}
	if err := accept(msg.Entity); err != nil {
		//TODO 日志记录
		fmt.Println(err)
	}
	//if stream != nil {
	//	stream.onProgress(true, fragmentIndex, fragmentTotal);
	//}
	return
}

// AggrFragment
/**
 * @Description 聚合所有分片
 * @Date 2024-01-10 22:31:38
 */
func (h HandlerBase) AggrFragment(channel core.Channel, fragmentIndex int, message *message.Message) (result *message.Message, err error) {
	aggregator, ok := channel.GetAttachment(message.Sid).(core.FragmentAggregator)
	if !ok {
		aggregator, err = NewAggregatorDefault(message)
		if err != nil {
			return
		}
		channel.PutAttachment(aggregator.GetSid(), aggregator)
	}

	aggregator.Add(fragmentIndex, message)

	if aggregator.GetDataLength() > aggregator.GetDataStreamSize() {
		//长度不够，等下一个分片包
		return
	}
	//重置为聚合帖
	channel.PutAttachment(message.Sid, nil)
	return aggregator.Get()
}
