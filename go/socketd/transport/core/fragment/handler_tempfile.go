package fragment

import (
	"socketd/transport/core"
	"socketd/transport/core/message"
)

type HandlerTempfile struct {
	*HandlerBase
}

func (t *HandlerTempfile) CreateFragmentAggregator(message *message.Message) (core.FragmentAggregator, error) {
	return NewAggregatorTempfile(message)
}
func (t *HandlerTempfile) AggrEnable() bool {
	return true
}
