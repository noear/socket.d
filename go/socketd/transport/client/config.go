package client

import (
	"fmt"
	"time"

	"socketd/transport/core"
)

type Config struct {
	core.Config
	core.PreConfig

	heartbeatInterval time.Duration // 心跳间隔
	connectTimeout    time.Duration // 连接超时
	autoReconnect     bool          // 是否自动重连
}

func NewConfig(coreCfg core.Config, preCfg core.PreConfig) *Config {
	var cfg = new(Config)
	cfg.Config = coreCfg
	cfg.PreConfig = preCfg
	cfg.connectTimeout = time.Second * 10
	cfg.heartbeatInterval = time.Second * 20
	cfg.autoReconnect = true
	return cfg
}

func (c *Config) GetLinkUrl() string {
	return fmt.Sprintf("sd:%s://%s:%d", c.GetSchema(), c.GetHost(), c.GetPort())
}

func (c *Config) GetUrl() string {
	return fmt.Sprintf("%s://%s:%d", c.GetSchema(), c.GetHost(), c.GetPort())
}

//func (c *Config) GetSchema() string {
//	return c.schema
//}
//
//func (c *Config) GetHost() string {
//	return c.host
//}
//
//func (c *Config) GetPort() int {
//	return c.port
//}

func (c *Config) GetHeartbeatInterval() time.Duration {
	return c.heartbeatInterval
}

func (c *Config) HeartbeatInterval(heartbeatInterval time.Duration) *Config {
	c.heartbeatInterval = heartbeatInterval
	return c
}

func (c *Config) GetConnectTimeout() time.Duration {
	return c.connectTimeout
}

func (c *Config) ConnectTimeout(connectTimeout time.Duration) *Config {
	c.connectTimeout = connectTimeout
	return c
}

func (c *Config) IsAutoReconnect() bool {
	return c.autoReconnect
}

func (c *Config) AutoReconnect(autoReconnect bool) *Config {
	c.autoReconnect = autoReconnect
	return c
}

func (c *Config) IdleTimeout(idleTimeout int) *Config {

	return c
}
