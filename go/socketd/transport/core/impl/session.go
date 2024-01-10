package impl

import (
	"net/netip"
)

type Session struct {
	sessionId     string
	localAddress  netip.Addr
	remoteAddress netip.Addr
}

func (s *Session) SessionId() string {
	return s.sessionId
}

func (s *Session) LocalAddress() string {
	return s.localAddress.String()
}

func (s *Session) RemoteAddress() string {
	return s.remoteAddress.String()
}
