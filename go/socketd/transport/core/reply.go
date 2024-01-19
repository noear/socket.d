package core

type Reply interface {
	Sid() string
	IsEnd() bool
}
