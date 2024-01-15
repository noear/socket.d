package socketd

import (
	"net/url"
	"strconv"
	"strings"

	"socketd/transport/server"
)

type ConfigOption func(config *server.Config)

func WithPort(port int) ConfigOption {
	return func(config *server.Config) {
		config.Port = port
	}
}

func WithHost(host string) ConfigOption {
	return func(config *server.Config) {
		config.Host = host
	}
}

func WithDebug() ConfigOption {
	return func(config *server.Config) {
		config.Debug = true
	}
}

func WithProtocal(protocol string) ConfigOption {
	return func(config *server.Config) {
		config.Protocol = protocol
	}
}

func WithLink(link string) ConfigOption {
	link, _ = strings.CutPrefix(link, "sd:")
	u, _ := url.Parse(link)
	split := strings.Split(u.Host, ":")
	return func(config *server.Config) {
		config.Protocol = u.Scheme
		config.Host = u.Host
		if len(split) == 2 {
			config.Host = split[0]
			config.Port, _ = strconv.Atoi(split[1])
		}
	}
}
