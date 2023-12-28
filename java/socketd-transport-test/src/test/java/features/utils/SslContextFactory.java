package features.utils;

import org.noear.socketd.utils.StrUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

public class SslContextFactory {

    public static SSLContext create(String keyStoreName, String keyStorePassword) throws IOException {
        return create(keyStoreName, null, keyStorePassword);
    }

    public static SSLContext create(String keyStoreName, String keyStoreType, String keyStorePassword) throws IOException {
        if (StrUtils.isEmpty(keyStoreType)) {
            keyStoreType = "jks";
        }

        KeyStore keyStore = loadKeyStore(keyStoreName, keyStoreType, keyStorePassword);

        KeyManager[] keyManagers = buildKeyManagers(keyStore, keyStorePassword.toCharArray());

        SSLContext sslContext;

        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, null, null);
        } catch (NoSuchAlgorithmException | KeyManagementException exc) {
            throw new IOException("Unable to create and initialise the SSLContext", exc);
        }

        return sslContext;
    }

    private static KeyStore loadKeyStore(final String location, String type, String storePassword)
            throws IOException {

        URL KeyStoreUrl = StrUtils.class.getResource(location);

        if (KeyStoreUrl == null) {
            throw new IllegalStateException("The keyStore file does not exist: " + location);
        }

        try (InputStream stream = KeyStoreUrl.openStream()) {
            KeyStore loadedKeystore = KeyStore.getInstance(type);
            loadedKeystore.load(stream, storePassword.toCharArray());
            return loadedKeystore;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException exc) {
            throw new IOException(String.format("Unable to load KeyStore %s", location), exc);
        }
    }

    private static KeyManager[] buildKeyManagers(final KeyStore keyStore, char[] storePassword)
            throws IOException {
        KeyManager[] keyManagers;
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory
                    .getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, storePassword);
            keyManagers = keyManagerFactory.getKeyManagers();
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException exc) {
            throw new IOException("Unable to initialise KeyManager[]", exc);
        }
        return keyManagers;
    }
}
