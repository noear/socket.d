package fragment

import "socketd/transport/core"

// Holder
/**
 * @Description:分片持有人
 * @Date 2024-01-10 21:14:02
 */
type Holder struct {
	index   int
	message *core.Message
}

func NewHolder(index int, message *core.Message) *Holder {
	return &Holder{index, message}
}

func (h *Holder) GetIndex() int {
	return h.index
}

func (h *Holder) GetMessage() *core.Message {
	return h.message
}
