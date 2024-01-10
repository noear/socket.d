package fragment

import (
	"fmt"
	"socketd/transport/core"
	"strconv"
)

// AggregatorTempfile
/**
 * @Description: 分片聚合器
 * @Date 2024-01-10 22:37:31
 */
type AggregatorTempfile struct {
	main            *core.Message //主导消息
	fragmentHolders []Holder      //分片列表
	dataStreamSize  int           //数据流大小
	dataLength      int           //数据总长度
}

func NewAggregatorTempfile(main *core.Message) (ad *AggregatorTempfile, err error) {
	ad = new(AggregatorTempfile)
	ad.main = main

	var dataLenStr = main.Meta.Get(core.META_DATA_LENGTH)
	if dataLenStr == "" {
		err = fmt.Errorf("missing %s meta, event=%s", core.META_DATA_LENGTH, main.Event)
	}
	ad.dataLength, err = strconv.Atoi(dataLenStr)
	return
}

func (a AggregatorTempfile) GetSid() string {
	return a.main.Sid
}

func (a AggregatorTempfile) GetDataStreamSize() int {
	return a.dataStreamSize
}

func (a AggregatorTempfile) GetDataLength() int {
	return a.dataLength
}

// Add
/**
 * @Description 添加帧
 * @Date 2024-01-10 23:28:31
 */
func (a AggregatorTempfile) Add(index int, message *core.Message) {
	a.fragmentHolders = append(a.fragmentHolders, Holder{index, message})
	a.dataStreamSize += message.DataSize()
	return
}

// Get
/**
 * @Description 获取聚合帧
 * @Date 2024-01-10 23:28:41
 */
func (a AggregatorTempfile) Get() (frame *core.Message, err error) {

	return
}
