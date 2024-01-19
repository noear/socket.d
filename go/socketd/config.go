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
	metaMap url.Values
}

func (c *Config) GetSchema() string {
	return c.schema
}

func (c *Config) GetHost() string {
	if c.host == "" {
		c.host = "0.0.0.0"
	}
	return c.host
}

func (c *Config) GetPort() int {
	if c.port == 0 {
		c.port = 8602
	}
	return c.port
}

func (c *Config) GetAddress() string {
	return fmt.Sprintf("%s:%d", c.GetHost(), c.GetPort())
}

func (c *Config) GetMetaMap() url.Values {
	return c.metaMap
}

func (c *Config) MetaPut(name, val string) {
	c.metaMap.Set(name, val)
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
	schema, _ = strings.CutPrefix(schema, "sd:")
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
			index := strings.Index(link, "?")
			if index != -1 {
				config.metaMap, _ = url.ParseQuery(link[index+1:])
			}
		}
	}
}
