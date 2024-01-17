package socketd

import (
	"fmt"
	"net/url"
	"strconv"
	"strings"
)

type Config struct {
	schema  string
	host    string
	port    int
	metaMap map[string]string
}

func (c *Config) GetSchema() string {
	return c.schema
}

func (c *Config) GetHost() string {
	return c.host
}

func (c *Config) GetPort() int {
	return c.port
}

func (c *Config) GetAddress() string {
	return fmt.Sprintf("%s:%d", c.host, c.port)
}

func (c *Config) GetMetaMap() map[string]string {
	return c.metaMap
}

func (c *Config) MetaPut(name, val string) {
	c.metaMap[name] = val
}

type ConfigOption func(config *Config)

func WithPort(port int) ConfigOption {
	return func(config *Config) {
		config.port = port
	}
}

func WithHost(host string) ConfigOption {
	return func(config *Config) {
		config.host = host
	}
}

func WithSchema(schema string) ConfigOption {
	return func(config *Config) {
		config.schema = schema
	}
}

func WithLink(link string) ConfigOption {
	link, _ = strings.CutPrefix(link, "sd:")
	u, _ := url.Parse(link)
	split := strings.Split(u.Host, ":")
	return func(config *Config) {
		config.schema = u.Scheme
		config.host = u.Host
		if len(split) == 2 {
			config.host = split[0]
			config.port, _ = strconv.Atoi(split[1])
		}
	}
}
