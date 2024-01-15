package constant

// @Description: 一些常量定义

// META KEY
const (
	META_SOCKETD_VERSION           = "Socket.D"    // 框架版本号
	META_DATA_LENGTH               = "Data-Length" // 数据长度
	META_DATA_TYPE                 = "Data-Type"   // 数据类型
	META_DATA_FRAGMENT_IDX         = "Data-Fragment-Idx"
	META_DATA_FRAGMENT_TOTAL       = "Data-Fragment-Total"
	META_DATA_DISPOSITION_FILENAME = "Data-Disposition-Filename"
	META_RANGE_START               = "Data-Range-Start"
	META_RANGE_SIZE                = "Data-Range-Size"
)

// 帧消息标志
const (
	FrameConnect = 10 // 握手：连 接(c->s)，提交客户端握手信息，请求服务端握手信息
	FrameConnack = 11 // 握手：确认(c<-s)，响应服务端握手信息

	FramePing = 20 //心跳:ping(c-->s)
	FramePong = 21 //心跳:pong(c<--s)

	FrameClose = 30 // 客户端关闭连接
	FrameAlarm = 31 // 客户端异常

	FrameMessage   = 40 // 消息(c<->s)
	FrameRequest   = 41 // 请求(c<->s)
	FrameSubscribe = 42 // 订阅
	FrameReply     = 48 // 回复
	FrameReplyEnd  = 49 // 结束回复
)

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
