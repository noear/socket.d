package message

import (
	"fmt"
	"net/url"
	"strconv"

	"socketd/transport/core/constant"
)

type Entity struct {
	Meta url.Values
	Data []byte
}

func NewEntity(meta url.Values, data []byte) *Entity {
	return &Entity{meta, data}
}

func (e *Entity) String() string {
	return fmt.Sprintf("{Meta:%s, Data:%s}", e.Meta, e.Data)
}

func (e *Entity) At(name string) *Entity {
	e.Meta.Set("@", name)
	return e
}

func (e *Entity) Range(start int, size int) *Entity {
	e.Meta.Set(constant.META_RANGE_START, strconv.Itoa(start))
	e.Meta.Set(constant.META_RANGE_SIZE, strconv.Itoa(size))
	return e
}

func (e *Entity) MetaPut(name, val string) *Entity {
	e.Meta.Set(name, val)
	return e
}

func (e *Entity) MetaMapPut(m url.Values) *Entity {
	for k, v := range m {
		e.Meta[k] = v
	}
	return e
}

func (e *Entity) MetaString() string {
	return e.Meta.Encode()
}

func (e *Entity) MetaStringSet(meta string) *Entity {
	e.Meta, _ = url.ParseQuery(meta)
	return e
}

func (e *Entity) DataSet(data []byte) *Entity {
	e.Data = data
	return e
}

func (e *Entity) DataSize() int {
	return len(e.Data)
}
