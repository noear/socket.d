package core

type Processor interface {
	SetListener(listener Listener)           // 设置监听器
	OnOpen(conn Channel)                 // 打开处理
	OnReceive(conn Channel, frame Frame) // 接收处理
	OnMessage(conn Channel, msg Message) // 收消息时处理
	OnClose(conn Channel)                // 关闭处理
	OnError(conn Channel, err error)     // 错误处理
}
