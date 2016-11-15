package info.futureme.abs.rest.client;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import info.futureme.abs.R;
import info.futureme.abs.biz.ContextManager;
import info.futureme.abs.biz.IAccountManager;
import okhttp3.OkHttpClient;


/**
 * httpclient suitable for https connection and client is also certificated using specified ssl certificate
 * as well as the server
 * @author Jeffrey
 * @version 1.0
 * @updated 16-2月-2016 18:02:50
 */
public class TwoWayAuthHttpClient {
    public static OkHttpClient getClient(String password, IAccountManager accountService) throws IOException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException, CertificateException {
        OkHttpClient httpClient = OneWayAuthHttpClient.getClient(accountService);
        KeyStore keyStore = readKeyStore(password); //your method to obtain KeyStore
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        httpClient.newBuilder().sslSocketFactory(sslContext.getSocketFactory());
        return httpClient;
    }


    public static KeyStore readKeyStore(String password) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        KeyStore ks = null;
        ks = KeyStore.getInstance(KeyStore.getDefaultType());

        // get user password and file input stream

        InputStream fis = null;
        try {
            fis = ContextManager.context().getResources().openRawResource(R.raw.ssl);
            ks.load(fis, password.toCharArray());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return ks;
    }

}
