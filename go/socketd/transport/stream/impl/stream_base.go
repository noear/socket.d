package impl

import (
	"context"
	"fmt"
	"time"

	"socketd/transport/stream"
)

type StreamBase struct {
	sid             string
	demands         int
	timeout         time.Duration
	doOnError       func(err error)
	doOnProgress    func(isSend bool, val int, max int)
	insuranceCancel context.CancelFunc
}

func NewStreamBase(sid string, demands int, timeout time.Duration) *StreamBase {
	return &StreamBase{
		sid:     sid,
		demands: demands,
		timeout: timeout,
	}
}

func (b *StreamBase) Sid() string {
	return b.sid
}

func (b *StreamBase) Demands() int {
	return b.demands
}

func (b *StreamBase) Timeout() time.Duration {
	return b.timeout
}

func (b *StreamBase) SetTimeout() time.Duration {
	return b.timeout
}

func (b *StreamBase) InsuranceStart(streamManger stream.StreamManager, streamTimeout time.Duration) {
	if b.insuranceCancel != nil {
		return
	}

	var ctx, cancel = context.WithCancel(context.Background())
	b.insuranceCancel = cancel

	var timer = time.NewTimer(streamTimeout)
	go func(ctx context.Context) {
		select {
		case <-ctx.Done():
		case <-timer.C:
			streamManger.Remove(b.sid)
			b.OnError(fmt.Errorf("the stream response timeout,sid = %s", b.sid))
		}
	}(ctx)
}

func (b *StreamBase) InsuranceCancel() {
	if b.insuranceCancel != nil {
		b.insuranceCancel()
	}
}

func (b *StreamBase) OnError(err error) {
	if b.doOnError != nil {
		b.doOnError(err)
	}
}

func (b *StreamBase) OnProgress(isSend bool, val int, max int) {
	if b.doOnProgress != nil {
		b.doOnProgress(isSend, val, max)
	}
}

func (b *StreamBase) ThenError(onError func(err error)) {
	b.doOnError = onError
}

func (b *StreamBase) ThenProgress(onProgress func(isSend bool, val int, max int)) {
	b.doOnProgress = onProgress
}
