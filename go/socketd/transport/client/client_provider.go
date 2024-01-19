package client

type Provider interface {
	Schemas() []string
	CreateClient(config *Config)
}
