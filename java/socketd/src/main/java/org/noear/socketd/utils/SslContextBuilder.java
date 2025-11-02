package org.noear.socketd.utils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Ssl 上下文构建器
 *
 * @author noear
 * @since 2.5
 */
public class SslContextBuilder {
    private SecureRandom secureRandom = null;
    private KeyManager[] keyManagers = null;
    private TrustManager[] trustManagers = null;
    private String protocol = "TLS";
    private String algorithm = "SunX509";
    private String keyStoreType = "PKCS12";

    public SslContextBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public SslContextBuilder algorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public SslContextBuilder keyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }

    public SslContextBuilder secureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    public SslContextBuilder keyManager(String keyStoreFile, String keyStorePassword, String keyPassword) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        try (InputStream inputStream = new FileInputStream(keyStoreFile)) {
            return keyManager(inputStream, keyStorePassword, keyPassword);
        }
    }

    public SslContextBuilder keyManager(InputStream keyStoreInputStream, String keyStorePassword, String keyPassword) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        KeyStore ks = loadKeyStore(keyStoreInputStream, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        kmf.init(ks, keyPassword.toCharArray());
        keyManagers = kmf.getKeyManagers();
        return this;
    }

    public SslContextBuilder trustManager(String trustStoreFile, String trustStorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try (InputStream inputStream = new FileInputStream(trustStoreFile)) {
            return trustManager(inputStream, trustStorePassword);
        }
    }

    public SslContextBuilder trustManager(InputStream trustStoreInputStream, String trustStorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore ts = loadKeyStore(trustStoreInputStream, trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
        tmf.init(ts);
        trustManagers = tmf.getTrustManagers();
        return this;
    }

    /**
     * 构建
     */
    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(keyManagers, trustManagers, secureRandom);

        return sslContext;
    }

    private KeyStore loadKeyStore(InputStream keyStoreInputStream, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(keyStoreInputStream, password);
        return ks;
    }
}