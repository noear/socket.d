package core

import "time"

type Config interface {
	ClientMode() bool // 是否为客户端模式
	// GetStreamManager()
	GetRoleName() string                 // 获取角色名
	GetCharset() string                  // 获取字符集
	GetCodec() Codec                     // 获取编码器
	GetFragmentHandler() FragmentHandler // 获取分片处理器
	GetFragmentSize() int                // 获取分片大小
	// GetChannelExecutor() ChannelExecutor // 获取通道执行器
	GetReadBufferSize() int           // 获取读取缓冲区大小
	GetWriteBufferSize() int          // 获取写入缓冲区大小
	GetIdleTimeout() time.Duration    // 获取空闲超时时间
	GetRequestTimeout() time.Duration // 获取请求超时时间
	GetStreamTimeout() time.Duration  // 获取流超时时间
	GetMaxUdpSize() int               // 获取最大udp包大小
}

const (
	DEF_SID         = "" //默认流id（占位）
	DEF_EVENT       = "" //默认事件（占位）
	DEF_META_STRING = "" //默认元信息字符串（占位）
	//DEF_DATA =[]byte //默认数据（占位）
	CLOSE1_PROTOCOL         = 1            //因协议指令关闭
	CLOSE2_PROTOCOL_ILLEGAL = 2            //因协议非法关闭
	CLOSE3_ERROR            = 3            //因异常关闭
	CLOSE4_USER             = 4            //因用户主动关闭
	MAX_SIZE_SID            = 64           //流ID长度最大限制
	MAX_SIZE_EVENT          = 512          //事件长度最大限制
	MAX_SIZE_META_STRING    = 4096         //元信息串长度最大限制
	MAX_SIZE_DATA           = 1048576 * 16 //16m 数据长度最大限制（也是分片长度最大限制）
	MAX_SIZE_FRAME          = 1048576 * 17 //17m 帧长度最大限制
	MIN_FRAGMENT_SIZE       = 1024         //1k 分片长度最小限制
	DEMANDS_ZERO            = 0            //零需求
	DEMANDS_SIGNLE          = 1            //单需求
	DEMANDS_MULTIPLE        = 2            //多需要
)
