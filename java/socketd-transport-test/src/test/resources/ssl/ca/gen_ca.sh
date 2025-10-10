#!/bin/bash

## reference https://stackoverflow.com/questions/37714558/how-to-enable-server-side-ssl-for-grpc
echo Generate CA key:
openssl genrsa -passout pass:123456 -des3 -out ca.key 4096

echo Generate CA certificate:
openssl req -passin pass:123456 -new -x509 -days 365000 -key ca.key -out ca.crt -subj "/CN=*.hasor.net"

echo Generate server key:
openssl genrsa -passout pass:123456 -des3 -out server.key 4096

echo Generate server signing request:
openssl req -passin pass:123456 -new -key server.key -out server.csr -subj "/CN=*.hasor.net"

echo Self-sign server certificate:
openssl x509 -req -passin pass:123456 -days 365000 -in server.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out server.crt

echo Remove passphrase from server key:
openssl rsa -passin pass:123456 -in server.key -out server.key

# 单向认证，client不需要生成秘钥和证书，只要提供的 CA 证书即可
echo Generate client key
openssl genrsa -passout pass:123456 -des3 -out client.key 4096

echo Generate client signing request:
openssl req -passin pass:123456 -new -key client.key -out client.csr -subj "/CN=localhost"

echo Self-sign client certificate:
openssl x509 -passin pass:123456 -req -days 365000 -in client.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out client.crt

echo Remove passphrase from client key:
openssl rsa -passin pass:123456 -in client.key -out client.key

echo Generate client pem file
openssl pkcs8 -topk8 -nocrypt -in client.key -out client.pem
echo Generate server pem file
openssl pkcs8 -topk8 -nocrypt -in server.key -out server.pem
