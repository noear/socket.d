package impl

import (
	"bytes"
	"encoding/binary"
	"net/url"

	"socketd/transport/core"
)

type CodecDefault struct{}

func (cd *CodecDefault) Decode(f *core.Frame, body []byte) {
	f.Len = binary.BigEndian.Uint32(body[0:4])
	f.Flag = binary.BigEndian.Uint32(body[4:8])
	lines := bytes.Split(body[8:], []byte{'\n'})

	if len(lines) >= 1 {
		f.Sid = string(lines[0])
	}
	if len(lines) >= 2 {
		f.Event = string(lines[1])
	}
	if len(lines) >= 3 {
		f.Meta, _ = url.ParseQuery(string(lines[2]))
	}
	if len(lines) >= 4 {
		f.Data = lines[3]
	}
}

func (cd *CodecDefault) Encode(f *core.Frame) []byte {
	var buf = new(bytes.Buffer)
	binary.Write(buf, binary.BigEndian, uint32(0))
	binary.Write(buf, binary.BigEndian, f.Flag)
	buf.WriteString(f.Sid)
	buf.WriteByte('\n')
	buf.WriteString(f.Event)
	buf.WriteByte('\n')
	buf.WriteString(f.Meta.Encode())
	buf.WriteByte('\n')
	buf.Write(f.Data)

	var bs = buf.Bytes()
	binary.BigEndian.PutUint32(bs[:4], uint32(len(bs)))
	return bs
}
