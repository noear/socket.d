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

    /**
     * 安全机制
     */
    public SslContextBuilder secureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    public SslContextBuilder keyManager(String keyStoreFile, String keyStorePassword) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        return keyManager(keyStoreFile, null, keyStorePassword);
    }

    public SslContextBuilder keyManager(String keyStoreFile, String type, String keyStorePassword) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        try (InputStream inputStream = new FileInputStream(keyStoreFile)) {
            return keyManager(inputStream, type, keyStorePassword);
        }
    }

    public SslContextBuilder keyManager(InputStream keyStore, String keyStorePassword) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        return keyManager(keyStore, null, keyStorePassword);
    }

    public SslContextBuilder keyManager(InputStream keyStore, String type, String keyStorePassword) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        if (type == null) {
            type = "PKCS12";
        }

        KeyStore ks = loadKeyStore(keyStore, type, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keyStorePassword.toCharArray());
        keyManagers = kmf.getKeyManagers(); // X509KeyManager
        return this;
    }

    public SslContextBuilder trustManager(String trustStoreFile, String trustStorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        return trustManager(trustStoreFile, null, trustStorePassword);
    }

    public SslContextBuilder trustManager(String trustStoreFile, String type, String trustStorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try (InputStream inputStream = new FileInputStream(trustStoreFile)) {
            return trustManager(inputStream, type, trustStorePassword);
        }
    }

    public SslContextBuilder trustManager(InputStream trustStore, String trustStorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        return trustManager(trustStore, null, trustStorePassword);
    }

    public SslContextBuilder trustManager(InputStream trustStore, String type, String trustStorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        if (type == null) {
            type = "PKCS12";
        }

        KeyStore ts = loadKeyStore(trustStore, type, trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);
        trustManagers = tmf.getTrustManagers();
        return this;
    }

    /**
     * 构建
     */
    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, secureRandom);

        return sslContext;
    }

    private KeyStore loadKeyStore(InputStream inputStream, String type, char[] passphrase) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
        KeyStore ks = KeyStore.getInstance(type);
        ks.load(inputStream, passphrase);
        return ks;
    }
}