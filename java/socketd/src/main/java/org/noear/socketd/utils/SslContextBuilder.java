package org.noear.socketd.utils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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

    public SslContextBuilder trustManagerAsEmpty() {
        trustManagers = new TrustManager[]{new EmptyX509TrustManager()};
        return this;
    }

    /**
     * 构建
     */
    public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
        // if client:
        //  keyManagers == null, 表示客户端不提供客户端证书,用于单向认证场景（只需要服务器证书）,系统会使用默认的 KeyManager
        //  trustManagers == null, 表示使用默认的信任库（通常是 JRE 的 cacerts）,信任所有标准的 CA 机构颁发的证书
        //  secureRandom == null, 表示使用系统默认的随机数生成器,在大多数情况下，使用默认的就已经足够安全
        //
        // if server:
        //  keyManagers != null, 服务端必须提供证书来向客户端证明身份
        //  trustManagers == null, 表示不需要验证客户端证书
        //  secureRandom == null, 与客户端意思相同

        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(keyManagers, trustManagers, secureRandom);

        return sslContext;
    }

    private KeyStore loadKeyStore(InputStream keyStoreInputStream, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(keyStoreInputStream, password);
        return ks;
    }

    private static final class EmptyX509TrustManager implements X509TrustManager {
        private final X509Certificate[] acceptedIssuers = new X509Certificate[0];

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return acceptedIssuers;
        }
    }
}