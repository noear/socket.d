package impl

import "socketd/transport/core"

func HeartbeatHandlerDefault(session core.Session) error {
	return session.SendPing()
}
