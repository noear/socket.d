package fragment

import (
	"cmp"
	"fmt"
	"slices"
	"socketd/transport/core"
	"strconv"
)

// AggregatorDefault
/**
 * @Description: 分片聚合器
 * @Date 2024-01-10 22:37:31
 */
type AggregatorDefault struct {
	main            *core.Message //主导消息
	fragmentHolders []Holder      //分片列表
	dataStreamSize  int           //数据流大小
	dataLength      int           //数据总长度
}

func NewAggregatorDefault(main *core.Message) (ad *AggregatorDefault, err error) {
	ad = new(AggregatorDefault)
	ad.main = main

	var dataLenStr = main.Meta.Get(core.META_DATA_LENGTH)
	if dataLenStr == "" {
		err = fmt.Errorf("missing %s meta, event=%s", core.META_DATA_LENGTH, main.Event)
	}
	ad.dataLength, err = strconv.Atoi(dataLenStr)
	return
}

func (a AggregatorDefault) GetSid() string {
	return a.main.Sid
}

func (a AggregatorDefault) GetDataStreamSize() int {
	return a.dataStreamSize
}

func (a AggregatorDefault) GetDataLength() int {
	return a.dataLength
}

// Add
/**
 * @Description 添加帧
 * @Date 2024-01-10 23:28:31
 */
func (a AggregatorDefault) Add(index int, message *core.Message) {
	a.fragmentHolders = append(a.fragmentHolders, Holder{index, message})
	a.dataStreamSize += message.DataSize()
	return
}

// Get
/**
 * @Description 获取聚合后的帧
 * @Date 2024-01-10 23:28:41
 */
func (a AggregatorDefault) Get() (msg *core.Message, err error) {
	slices.SortFunc(a.fragmentHolders, func(a, b Holder) int {
		return cmp.Compare(a.GetIndex(), b.GetIndex())
	})

	var data = make([]byte, 0, a.dataStreamSize)

	for _, v := range a.fragmentHolders {
		data = append(data, v.message.Data...)
	}

	msg = core.NewMessage()
	msg.CopyMessage(a.main)
	msg.Data = data
	return
}
