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
        validData[1] = "TotalAmount=2.00&BuyerId=37b20de1-5e1f-443f-b575-3ea79dc1b9f2&MerchantOrderId=9e2bc816-0287-4301-9ebc-2f74dca78d8e&MerchantId=02e1aaa5-ed29-408e-a24b-eddd665c3e8f&MerchantCode=0325&TransactionCode=14493&TransactionId=c2dede26-ad53-43ee-9b27-a51b84c6b87a&Status=9&Currency=ETB";
//        validData[1] = "TotalAmount=2";
//        validData[1] = "TotalAmount=2.00&BuyerId=37b20de1-5e1f-443f-b575-3ea79dc1b9f2&MerchantOrderId=9e2bc816-0287-4301-9ebc-2f74dca78d8e&MerchantId=02e1aaa5-ed29-408e-a24b-eddd665c3e8f&MerchantCode=0325&TransactionCode=14493&TransactionId=c2dede26-ad53-43ee-9b27-a51b84c6b87a&Status=9&Currency=ETB";
        validData[2] = "LPq0LhZo4JrFudjVXuYbZAq/znTQbJl/sylDDRdLBPASgJVd4dDd7LbpWPEKorBhqdGOB2xwNW0ED7tbvAjSWtlb2DCTuBts0omD/KRy/UySR36AOLewwzwlIcBoQpJl/VKWxuFpq87FK/SATeHGeBmfCg5FxePa/smJymUfvoexvrI09XBMb3f3BmZr/xyi31AUgkzDIzP+p3utqvks4VyWypzELLEl/NkN3IZ6INpluIrKaHBSI6xGO4afCl6Fce2HyegFUOTrK02AcnL+vZnmSCHosEu3j2TsWnXHjAlMCQWziVj6FYVSuFEqfs7FDqrtKmKddqkOS9OJnet/Yw==";
//        validData[2] = "m/5yILXByw88mYsbxv+lJzRVebfUdFcade1WdrseVRTcumLz2UQvCLSROQmazrA5RdTKlbKa3/B9viO+yw9E6p62lcHZCeFgaKTswzyi1+5DJr6bXRKwolXDvI4qNhaDGhdDLzoj7iujtYdEqSgaeUppcmq8SgfuMh4BytoLr6Yj1DOZgqvI+rkX61WiU/VAm24vpL2JsWO3w3pvuWyC19SQngq7jC9w4ex+HGGVwuIDnTNyb7y8c5ThyG5a9W3Dxa7lTS1S7LNepJDUPwlDxONMKHFQCA8a6+gsN+2gFrNzuQELW8LoO3+2FfOAIwJC3QBUy5Mmt+4ZbDMk1B/NOg==";
//        validData[2] = "dOtwIqN4HRpOUUczo5hHVf4wvnXh353Awr2GIaSDmvwyfyCnrhsPhhHnU3azoV0fQxA+2T1IJYwVhawLOm+xIgAKf/X8M9hDPppgwOZnYx1MIOLC7/qqRyadts/RGdQ0iT+EQmBm2wCel+kUOsVsYUMBdM4+XI4glzk9zIEsq6RGuKbJ5TZPNxxSVOSZ85yWFHrlRt7f5rtjMePP9eLGLfYg/g0lwFiQgOczyXXIQ2dr+bB8ropLVT5dR0h6AnxgxpEXF1IqwUGF7ifSq5jCYQnvcKbBZsL+Q4tqtKq9VQtZX4GY8xml4lAp1GM+gNdcSJPWQqbkHsm22AKAipztUA==";
        context = InstrumentationRegistry.getInstrumentation().getContext();
        testPublicKeyBytes = getTestPublicKey();
        testSignature = getSignature();
        response = new PaymentResponse();
        response.setGrandTotal(2);
        response.setBuyerId("37b20de1-5e1f-443f-b575-3ea79dc1b9f2");
        response.setMerchantOrderId("9e2bc816-0287-4301-9ebc-2f74dca78d8e");
        response.setMerchantId("02e1aaa5-ed29-408e-a24b-eddd665c3e8f");
        response.setMerchantCode("0325");
        response.setOrderCode("14493");
        response.setPaymentOrderId("c2dede26-ad53-43ee-9b27-a51b84c6b87a");
        response.setStatus(9);
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