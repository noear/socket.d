package impl

import (
	"bytes"
	"encoding/binary"
	"net/url"

	"socketd/transport/core/constant"
	"socketd/transport/core/message"
)

type CodecDefault struct{}

func (cd *CodecDefault) ReadLine(buf []byte) (n int, line []byte) {
	for i := range buf {
		if buf[i] == '\n' {
			return n, buf[:i]
		}
		n += 1
	}
	return n, buf
}

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

	lines := bytes.Split(buf[8:], []byte{'\n'})
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

func (cd *CodecDefault) Encode(f *message.Frame) []byte {
	var buf = new(bytes.Buffer)
	binary.Write(buf, binary.BigEndian, uint32(0))
	binary.Write(buf, binary.BigEndian, f.Flag)
	if f.Message != nil {
		buf.WriteString(f.Message.Sid)
		buf.WriteByte('\n')
		buf.WriteString(f.Message.Event)
		buf.WriteByte('\n')
		if f.Message.Entity != nil {
			buf.WriteString(f.Message.Meta.Encode())
			buf.WriteByte('\n')
			buf.Write(f.Message.Data)
		}
	}

	var bs = buf.Bytes()
	binary.BigEndian.PutUint32(bs[:4], uint32(len(bs)))
	return bs
}
