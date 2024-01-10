package socketd

type SocketD struct {
}

func (sd *SocketD) Version() string {
	return "0.1.0"
}

func (sd *SocketD) ProtocolVersion() string {
	return "1.0"
}
