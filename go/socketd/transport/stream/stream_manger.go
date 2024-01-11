package stream

type StreamManager interface {
	Add(sid string, stream StreamInternal)
	Get(sid string) StreamInternal
	Remove(sid string)
}
