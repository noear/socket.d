package server

type Config struct {
	Protocol string
	Host     string
	Port     int
	Debug    bool
}

func (c *Config) WithSchema(schema string) *Config {
	// if strings.HasPrefix(schema, "sd:") {
	// 	schema = schema[3:]
	// }

	// 临时使用
	c.Protocol = "tcp"
	c.Host = "0.0.0.0"
	c.Port = 8602
	return c
}

func (c *Config) DebugMode() {
	c.Debug = true
}
