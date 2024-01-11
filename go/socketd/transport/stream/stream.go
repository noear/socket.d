package stream

type Stream interface {
	Sid() string
	IsDone() bool
	ThenError(err error)
	ThenProgress(onProgress func(bool, int, int))
}
