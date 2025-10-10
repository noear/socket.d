#!/bin/bash

echo Generate JKS:

echo 'keyPass 123456'
echo 'storepass 123456'

# gen KeyPair
keytool -genkeypair -alias certificateKey -keypass 123456 -storepass 123456 -keyalg RSA -validity 36500 -keystore keystore.jks

# export public CER
keytool -export -alias certificateKey -storepass 123456 -keystore keystore.jks -rfc -file public.crt

# gen Truststore, and import public CRT
Keytool -import -alias certificateKey -storepass 123456 -noprompt -file public.crt -keystore trustKeystore.jks

# list trustKeystore
keytool -list -v -storepass 123456 -keystore trustKeystore.jks