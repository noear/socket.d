package impl

import (
	"time"

	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/fragment"
)

var _ core.Config = new(ConfigBase)

type ConfigBase struct {
	clientMode bool
	// streamManager StreamManager
	codec           core.Codec
	fragmentHandler core.FragmentHandler
	fragmentSize    int
	charset         string

	readBufferSize  int
	writeBufferSize int
	idleTimeout     time.Duration
	requestTimeout  time.Duration
	streamTimeout   time.Duration
	maxUdpSize      int
}

func DefualtConfig(clientMode bool) *ConfigBase {
	var c = &ConfigBase{}

	c.clientMode = clientMode
	c.codec = &CodecDefault{}
	c.charset = "utf-8"
	c.fragmentHandler = fragment.NewFragmentHandlerDefault()
	c.fragmentSize = constant.MAX_SIZE_DATA

	c.readBufferSize = 512
	c.writeBufferSize = 512

	c.idleTimeout = time.Minute
	c.requestTimeout = time.Second * 10
	c.streamTimeout = time.Hour * 2
	c.maxUdpSize = 2048
	return c
}

func (c *ConfigBase) ClientMode() bool {
	return c.clientMode
}
func (c *ConfigBase) GetRoleName() string {
	if c.clientMode {
		return "client"
	}
	return "server"
}
func (c *ConfigBase) GetCharset() string {
	return c.charset
}
func (c *ConfigBase) GetCodec() core.Codec {
	return c.codec
}
func (c *ConfigBase) GetFragmentHandler() core.FragmentHandler {
	return c.fragmentHandler
}
func (c *ConfigBase) GetFragmentSize() int {
	return c.fragmentSize
}
func (c *ConfigBase) GetReadBufferSize() int {
	return c.readBufferSize
}
func (c *ConfigBase) GetWriteBufferSize() int {
	return c.writeBufferSize
}
func (c *ConfigBase) GetIdleTimeout() time.Duration {
	return c.idleTimeout
}
func (c *ConfigBase) GetRequestTimeout() time.Duration {
	return c.requestTimeout
}
func (c *ConfigBase) GetStreamTimeout() time.Duration {
	//TODO implement me
	panic("implement me")
}
func (c *ConfigBase) GetMaxUdpSize() int {
	return c.maxUdpSize
}
