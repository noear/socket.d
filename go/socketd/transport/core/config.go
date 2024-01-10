package core

import "time"

type Config interface {
	ClientMode() bool // 是否为客户端模式
	// GetStreamManager()
	GetRoleName() string // 获取角色名
	GetCharset() string  // 获取字符集
	GetCodec() Codec     // 获取编码器
	// GetFragmentHandler() FragmentHandler // 获取分片处理器
	GetFragmentSize() int // 获取分片大小
	// GetChannelExecutor() ChannelExecutor // 获取通道执行器
	GetReadBufferSize() int           // 获取读取缓冲区大小
	GetWriteBufferSize() int          // 获取写入缓冲区大小
	GetIdleTimeout() time.Duration    // 获取空闲超时时间
	GetRequestTimeout() time.Duration // 获取请求超时时间
	GetStreamTimeout() time.Duration  // 获取流超时时间
	GetMaxUdpSize() int               // 获取最大udp包大小
}
