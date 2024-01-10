package server

type Provider interface {
	Schemas() []string                        //协议架构
	CreateServer(serverConfig *Config) Server //创建服务端
}
