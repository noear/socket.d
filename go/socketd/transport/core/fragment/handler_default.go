package fragment

import (
	"socketd/transport/core"
	"socketd/transport/core/message"
)

type HandlerDefault struct {
	*HandlerBase
}

func NewFragmentHandlerDefault() *HandlerDefault {
	return &HandlerDefault{&HandlerBase{}}
}

func (t *HandlerDefault) CreateFragmentAggregator(message *message.Message) (core.FragmentAggregator, error) {
	return NewAggregatorDefault(message)
}

func (t *HandlerDefault) AggrEnable() bool {
	return true
}
