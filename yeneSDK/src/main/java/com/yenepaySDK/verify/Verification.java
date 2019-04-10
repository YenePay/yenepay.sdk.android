package com.yenepaySDK.verify;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.mobsdk.BuildConfig;
import com.yenepaySDK.mobsdk.R;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import static android.content.ContentValues.TAG;

public class Verification {

    public static final String SERVER_KEY_STORE_NAME = "server";
    public static final String YP_PUBLIC_KEY_ALIAS = "yp_public";
    public final String HOST_NAME;
//    public static final String HOST_NAME = "192.168.137.1";
    private Context mContext;
    private PasswordProtectionHandler mProtectionHandler;
    private char[] password = "y3n3p6y".toCharArray();


    public Verification(Context context) {
        this(context, null);
    }

    public Verification(Context context, PasswordProtectionHandler handler) {
        this.mContext = context;
        HOST_NAME = mContext.getString(R.string.certificate_host_name);
        this.mProtectionHandler = handler != null? handler: new PasswordProtectionHandler();
    }

    public boolean verify(PaymentResponse response) throws Exception {
        try {
            String publicKeyString = getString(publicKeyReader());

            String dataString = response.getVerificationString();
            Log.d(TAG, "verify: data - " + dataString);
            Log.d(TAG, "____________________________________________________________________");
            Log.d(TAG, "verify: Signature - " + response.getSignature());

            byte[] dataSignature = Base64.decode(response.getSignature(), Base64.DEFAULT);
            boolean result = verifyDataSignature(dataString, dataSignature);
            if(result)
                Log.i(TAG, "verify: Signature Verification Successful");
            else
                Log.e(TAG, "verify: Signature Verification Failed");
            return result;
        } catch (Exception ex){
            Log.e(TAG, "main: ", ex);
            throw ex;
        }
    }

    public boolean verifyDataSignature(String dataString, byte[] dataSignature) throws Exception {
        Exception unhandled = null;
        try {
            InputStream signatureData = new ByteArrayInputStream(dataSignature);
            InputStream data = new ByteArrayInputStream(dataString.getBytes(Charset.forName("UTF8")));
            final RSAPublicKey publicKey = getPublicKey();

            final Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);

            final byte[] buffy = new byte[16 * 1024];
            int read = -1;
            while ((read = data.read(buffy)) != -1) {
                signature.update(buffy, 0, read);
            }
//
            final byte[] signatureBytes = new byte[publicKey.getModulus().bitLength() / 8];
            signatureData.read(signatureBytes);
//
            return signature.verify(signatureBytes);
//            return true;
        } catch (IOException e) {
            e.printStackTrace();
            unhandled = e;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            unhandled = e;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            unhandled = e;
        } catch (SignatureException e) {
            e.printStackTrace();
            unhandled = e;
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//            unhandled = e;
        } catch (Exception e){
            e.printStackTrace();
            unhandled = e;
        }
        if(unhandled != null){
            throw new Exception("Exception occurred while verifying - " + dataString, unhandled);
        }
//        return false;
        return true;
    }

    //    private static PemReader publicKeyReader() throws FileNotFoundException {
//        return new PemReader(new InputStreamReader(new FileInputStream("publicKey.pem")));
//    }
    private InputStream publicKeyReader() throws FileNotFoundException {
        return mContext.getResources().openRawResource(R.raw.yenepay);
    }

    private String getString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null){
            builder.append(line).append("\n");
        }
        reader.close();
        return builder.toString();
    }

    public RSAPublicKey downloadServerCertificate(){
        RSAPublicKey publicKey = null;
        TrustManager[] trustLocaleCert = new TrustManager[]{ new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        try {
            if(BuildConfig.DEBUG) {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustLocaleCert, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            }

            String hostName = HOST_NAME;
            SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(hostName,443);
            socket.startHandshake();
            Certificate[] certs = socket.getSession().getPeerCertificates();
            Certificate cert = certs[0];
            publicKey = (RSAPublicKey) cert.getPublicKey();

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            File file = new File(mContext.getFilesDir(), SERVER_KEY_STORE_NAME) ;
            keyStore.load(null, password);


//            if(file.exists()){
//
//            }
//            KeyStore.ProtectionParameter parameter = new KeyStore.CallbackHandlerProtection(mProtectionHandler);
            KeyStore.ProtectionParameter parameter = new KeyStore.PasswordProtection(password);
            KeyStore.TrustedCertificateEntry entry = new KeyStore.TrustedCertificateEntry(cert);
            keyStore.setEntry(YP_PUBLIC_KEY_ALIAS, entry, null);
            FileOutputStream fs = new FileOutputStream(new File(mContext.getFilesDir(), SERVER_KEY_STORE_NAME));
            keyStore.store(fs, password);
            fs.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public RSAPublicKey getPublicKeyFromStore(){
        RSAPublicKey publicKey = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream fs = new FileInputStream(new File(mContext.getFilesDir(), SERVER_KEY_STORE_NAME));
            keyStore.load(fs, password);
            fs.close();

//            if(file.exists()){
//
//            }
//            KeyStore.ProtectionParameter parameter = new KeyStore.CallbackHandlerProtection(mProtectionHandler);
            KeyStore.ProtectionParameter parameter = new KeyStore.PasswordProtection(password);
            if(keyStore.size() > 0 && keyStore.containsAlias(YP_PUBLIC_KEY_ALIAS)) {
                KeyStore.TrustedCertificateEntry entry = (KeyStore.TrustedCertificateEntry) keyStore.getEntry(YP_PUBLIC_KEY_ALIAS, null);
                Certificate certificate = entry.getTrustedCertificate();
                publicKey = (RSAPublicKey)certificate.getPublicKey();
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        }
//            File file = new File(mContext.getFilesDir(), SERVER_KEY_STORE_NAME) ;


        return publicKey;
    }


    public RSAPublicKey getPublicKey(){
        RSAPublicKey publicKey = getPublicKeyFromStore();
        if(publicKey == null){
            publicKey = downloadServerCertificate();
        }
        return publicKey;
    }

    public class PasswordProtectionHandler implements CallbackHandler {

        private char[] lastPassword;

        public PasswordProtectionHandler(){}

        // implement this method to handle the callback
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback cb : callbacks) {
                if (cb instanceof javax.security.auth.callback.PasswordCallback) {
                    javax.security.auth.callback.PasswordCallback pcb = (javax.security.auth.callback.PasswordCallback) cb;
                    try {
                        this.lastPassword = onPassword();// HERE YOUR SWING OR AWT OR ANOTHER WAY TO GET THE PASSWORD FROM THE CLIENT
                    } catch (Exception e) {}
                    pcb.setPassword(this.lastPassword);
                }
            }
        }

        protected char[] onPassword(){
            return "yenepaypassword".toCharArray();
        }
    }
}
