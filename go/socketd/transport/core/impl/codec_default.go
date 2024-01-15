package impl

import (
	"bytes"
	"encoding/binary"
	"net/url"

	"socketd/transport/core/constant"
	"socketd/transport/core/message"
)

type CodecDefault struct{}

var LF = []byte{0x00, 0x0a} //两字节换行符

func (cd *CodecDefault) Decode(buf []byte) (f *message.Frame) {
	if len(buf) < 8 {
		return
	}

	f = &message.Frame{}
	f.Len = binary.BigEndian.Uint32(buf[:4])
	f.Flag = binary.BigEndian.Uint32(buf[4:8])
	if f.Len == 0 || f.Len == 8 || f.Len > constant.MAX_SIZE_FRAME {
		return
	}

	lines := bytes.Split(buf[8:], LF)
	if len(lines) >= 1 {
		f.Message = &message.Message{}
		f.Message.Sid = string(lines[0])
	}
	if len(lines) >= 2 {
		f.Message.Event = string(lines[1])
	}
	if len(lines) >= 3 {
		f.Message.Entity = &message.Entity{}
		f.Message.Meta, _ = url.ParseQuery(string(lines[2]))
	}
	if len(lines) >= 4 {
		f.Message.Data = lines[3]
	}
	return f
}

func (cd *CodecDefault) Encode(f *message.Frame) (buf []byte) {
	buf = make([]byte, 8)
	binary.BigEndian.PutUint32(buf[4:8], f.Flag)
	if f.Message != nil {
		buf = append(buf, []byte(f.Message.Sid)...)
		buf = append(buf, LF...)
		buf = append(buf, []byte(f.Message.Event)...)
		buf = append(buf, LF...)
		if f.Message.Entity != nil {
			buf = append(buf, f.Message.Meta.Encode()...)
			buf = append(buf, LF...)
			buf = append(buf, f.Message.Data...)
		}
	}
	binary.BigEndian.PutUint32(buf[:4], uint32(len(buf)))
	return
}
