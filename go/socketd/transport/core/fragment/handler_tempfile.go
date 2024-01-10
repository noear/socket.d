package fragment

import "socketd/transport/core"

type HandlerTempfile struct {
	*HandlerBase
}

func (t *HandlerTempfile) CreateFragmentAggregator(message *core.Message) (core.FragmentAggregator, error) {
	return NewAggregatorTempfile(message)
}
func (t *HandlerTempfile) AggrEnable() bool {
	return true
}
