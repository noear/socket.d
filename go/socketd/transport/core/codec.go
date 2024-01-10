package core

type Codec interface {
	Decode(f *Frame, body []byte)
	Encode(f *Frame) []byte
}
