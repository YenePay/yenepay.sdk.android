package com.yenepaySDK.verify;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.io.ByteStreams;
import android.util.Base64;

import com.yenepaySDK.PaymentResponse;

import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;

import static org.junit.Assert.*;

public class VerificationTest {

    String[] validData = new String[3];
    String testPublicKey = "MIIC/jCCAeqgAwIBAgIQLFk7exPNg41NRNaeNu0I9jAJBgUrDgMCHQUAMBIxEDAO" +
            "BgNVBAMTB0RldlJvb3QwHhcNMTAwMTIwMjIwMDAwWhcNMzAwMTIwMjIwMDAwWjAS" +
            "MRAwDgYDVQQDEwdEZXZSb290MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC" +
            "AQEAqQ2VFwmki/y8U/2JHgj+wzgpk+WTOTCrFuHso0abFwcFU7WHl1NS4HSn5OmA" +
            "u/Yrsih5SRAw0gcynchhFQtXw6Pg99MVMfd/Im/pWyqrOII4VNdGSkVokKFehAgh" +
            "OW+zCtikRTOGDeU7WayB9BHbpIyuIiCrtOqdBuBgngaKb8+Y0xaoSxGEHHWhocZE" +
            "SuQlnkE8Bv48ro+rXaYKfnOrEr34v4jQORvgfdI3i2AmLlVuK2YiyFq6OmrmVHx/" +
            "pgnDFxZLrebU8rQhyFLooz9UCpKtkZAywfqtx8IfTVJ/WB7BVD+MQIvZ5qpx8uw9" +
            "y3eFyHRfZqNW9dtqEDuGvUiOWwIDAQABo1gwVjAPBgNVHRMBAf8EBTADAQH/MEMG" +
            "A1UdAQQ8MDqAENIWANpX5DZ3bX3WvoDfy0GhFDASMRAwDgYDVQQDEwdEZXZSb290" +
            "ghAsWTt7E82DjU1E1p427Qj2MAkGBSsOAwIdBQADggEBAFGus/YUn+HykxuDrNL8" +
            "kPqTbOlNiOCSVRPdex9UQ6H7zUprpE878ZK1aSgmI9OJmCYJoVVo8mYtcj7nJddw" +
            "LRSm1iN08+sd1KUMSrrC8TPmQeB5iIuyjmucA66s037YoSdXNxicptgE37iwlCtD" +
            "q7Wx0+XjnnlAk3vUSRdDMn3OmN8dmhaSp25Rt2g5xwaJ7uaPRh2Unt9/OpjbuXg5" +
            "JO2qAynCMn3UkvKEsWqDj9NjVPRcuNdLUt0PEG1bHvSaEqtCBe1wyu+isjhczMOq" +
            "j47eBGMgS9fNSLI7euhdW68+BOvwMKgjdnf5KMb7Yi6W7y2leuw5rf9oCmrHdiL8" +
            "ctQ=";
    private byte[] testPublicKeyBytes = null;
    private byte[] testSignature = null;
    private Verification verification;
    private Context context;
    private PaymentResponse response;

    @Before
    public void setUp() throws Exception {
        validData[0] = testPublicKey;
//        validData[1] = "TotalAmount=2.00&BuyerId=0095&MerchantOrderId=3740417f-f2cf-4b47-bff9-70e76dbb1b61&MerchantCode=1913&TransactionCode=1318&TransactionId=29b31d09-e82c-4ca9-aaa4-e10e9853a23e&Status=9&Currency=ETB";
//        validData[1] = "TotalAmount=2.00"+
//                "&BuyerId=37ef11b0-1e8a-47a0-818c-196a81b74f22"+
//                "&MerchantOrderId=0f8da454-5621-457c-854b-6547e39b81a9"+
//                "&MerchantCode=0003"+
//                "&MerchantId=f51f94fc-dd77-4366-90b8-8e0f88374573"+
//                "&TransactionCode=1131"+
//                "&TransactionId=a3bd3902-c7fd-4b5d-a30f-f896062c85d0"+
//                "&Status=1&"+
//                "Currency=ETB";
        //        validData[2] = "pgehxWM+IaDMmG9y16IPjT1uedXRfufacxI9+AL5G4oUd4f+sOCdjbxFHZifptgIWhzF7CHFMcpiUCHG3Hmc3kmr2AuNKiJrLllB77ogVd/7toBavqOy5lTQ54hEcMZn+pWA8wRMdq0DwHcJ4VZJWGr5jTB2ee1Ovgo2+AhgBMbiOUrYOJKMAebLY0Bnjdzit384bujYc0wdi6VQy6PhJc+apXNLGD86uM5PUtluAmcVwHDJ2ZB94gGIzX86jAgPdn+lXdYqUEA8gsUmSYRjffy8jBS+jvx1g12HUp7lwJYhuvD/H5QoCYrHD/Oy0E+s7LKXVvnsl6RdoVKDBT1SPQ==";

        validData[1] = "TotalAmount=2.00&BuyerId=93d72add-5c86-4779-a00a-9fa74a4d6b68&MerchantOrderId=f48c5a20-db87-4adf-b683-b931e11d4fe1&MerchantCode=1913&MerchantId=b14d41f4-d04e-4879-857a-058addbc680d&TransactionCode=1342&TransactionId=71e1e404-108a-4fbb-b06a-17f382804791&Status=9&Currency=ETB";
        validData[2] = "aHz7QYabxRa1hodLv9yMKHsK2chTqyD7x0MaEpTImxw22eN90TpaDVDhkuL2FCbax5ahffLKAQCMH6HjwoERhTQeRiZaYU1gl3jUmLuvc6WpomRCQ8YFdTUFysHqdK8NW1NyJ8EQkPtHcWmP9LtoQ8E2Gw6A2emhv3nk/f0yp1WYyliN03IOiKAi3/Uq3AMnXS8wLhrwzMUf45zyj4Ze6kzI81y4pGAzIQo7vXdXSdMlLU4LH+s11Z7r8/R+rPfqRTHVTnuVYicGKneRoGwVFEyEbBLBhSsdojxH1g6EOTf2wlWjNSxW/rua6jCCLSNkv1TXDFNBgkES6DlSc1rCGg==";
//
// validData[2] = "dOtwIqN4HRpOUUczo5hHVf4wvnXh353Awr2GIaSDmvwyfyCnrhsPhhHnU3azoV0fQxA+2T1IJYwVhawLOm+xIgAKf/X8M9hDPppgwOZnYx1MIOLC7/qqRyadts/RGdQ0iT+EQmBm2wCel+kUOsVsYUMBdM4+XI4glzk9zIEsq6RGuKbJ5TZPNxxSVOSZ85yWFHrlRt7f5rtjMePP9eLGLfYg/g0lwFiQgOczyXXIQ2dr+bB8ropLVT5dR0h6AnxgxpEXF1IqwUGF7ifSq5jCYQnvcKbBZsL+Q4tqtKq9VQtZX4GY8xml4lAp1GM+gNdcSJPWQqbkHsm22AKAipztUA==";
        context = InstrumentationRegistry.getInstrumentation().getContext();
        testPublicKeyBytes = getTestPublicKey();
        testSignature = getSignature();
        response = new PaymentResponse();
        response.setGrandTotal(2);
        response.setBuyerId("37ef11b0-1e8a-47a0-818c-196a81b74f22");
        response.setMerchantOrderId("0f8da454-5621-457c-854b-6547e39b81a9");
        response.setMerchantId("f51f94fc-dd77-4366-90b8-8e0f88374573");
        response.setMerchantCode("0003");
        response.setOrderCode("1131");
        response.setPaymentOrderId("a3bd3902-c7fd-4b5d-a30f-f896062c85d0");
        response.setStatus(1);
        response.setSignature(validData[2]);
        verification = new Verification(context);
    }

    @Test
    public void verifyDataSignature() throws Exception {
        boolean verified = verification.verifyDataSignature(validData[1], Base64.decode(validData[2], Base64.DEFAULT));
        assertTrue(verified);
    }

    private byte[] getTestPublicKey(){
        String fileName = "idsrv.der";
        return getFileBytes(fileName);
    }

    private byte[] getSignature(){
        String fileName = "signature2.bin";
        return getFileBytes(fileName);
    }

    private byte[] getFileBytes(String fileName) {

        AssetManager assets = context.getAssets();
        try {
            InputStream open = assets.open(fileName);
            DataInputStream data = new DataInputStream(open);
            return ByteStreams.toByteArray(open);
//            FileInputStream byteArrayInputStream = new FileInputStream(open)
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void downloadServerCertificate() {
        PublicKey publicKey = verification.downloadServerCertificate();
        assertNotNull(publicKey);
    }

    @Test
    public void testPaymentResponseParameters() {
        String v1 = validData[1];
        String v2 = response.getVerificationString();
        String exp = v1;
        String res = v2;
        assertEquals(exp.length(), res.length());
        assertEquals(exp, res);
    }
    @Test
    public void testVerify() throws Exception {
        boolean verified = verification.verify(response);
        assertTrue(verified);
    }
}