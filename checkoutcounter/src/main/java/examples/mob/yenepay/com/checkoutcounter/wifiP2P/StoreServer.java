package examples.mob.yenepay.com.checkoutcounter.wifiP2P;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yenepaySDK.verify.Verification;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Map;

import examples.mob.yenepay.com.checkoutcounter.StoreApp;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.PaymentResponseEntity;
import examples.mob.yenepay.com.checkoutcounter.db.model.Order;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;
import fi.iki.elonen.NanoHTTPD;

public class StoreServer extends NanoHTTPD {

    public static final String BAD_REQUEST_MSG = "Bad Request";
    public static final String PARAM_NAME_SERVICE_TYPE = "q";
    public static final String SERVICE_TYPE_NEW_PAYMENT = "n";
    public static final String SERVICE_TYPE_PAYMENT_RESULT = "r";
    public static final String PARAM_STORE_ORDER_ID = "oid";
    public static final String PARAM_NOUNCE = "nce";
    public static final String MIME_TYPE_JSON = "application/json";
    public static final String PLAIN_TEXT = "plain/text";
    public static final String SUCCESSFUL_MSG = "Successful";
    public static final String SERVER_ERROR_MSG = "Server Error";
    private String mHostName = null;
    private StoreRepository mRepository;


    public StoreServer(StoreRepository repository, String host, int port) {
        super(port);
        mHostName = host;
        mRepository = repository;
    }


    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> params = session.getParms();
        if (params.isEmpty() || !params.containsKey(PARAM_NAME_SERVICE_TYPE)) {
            return badRequest();
        }
        String serviceType = params.get(PARAM_NAME_SERVICE_TYPE);
        RequestHandler handler = getHandler(serviceType);
        if (handler == null) {
            return notFound();
        }

        return handler.getResponse(session);
    }

    @NonNull
    private Response notFound() {
        return statusResponse(Response.Status.NOT_FOUND, null);
    }

    @NonNull
    private Response statusResponse(Response.Status status, String msg) {
        return newFixedLengthResponse(status, MIME_TYPE_JSON, msg);
    }

    @NonNull
    private Response badRequest() {
        return statusResponse(Response.Status.BAD_REQUEST, BAD_REQUEST_MSG);
    }

    @NonNull
    private Response interalServerError() {
        return statusResponse(Response.Status.BAD_REQUEST, SERVER_ERROR_MSG);
    }

    @NonNull
    private Response badRequest(String msg) {
        return statusResponse(Response.Status.BAD_REQUEST, msg);
    }

    public RequestHandler getHandler(String serviceType) {
        switch (serviceType) {
            case SERVICE_TYPE_NEW_PAYMENT:
                return new NewPaymentRequestHandler();
            case SERVICE_TYPE_PAYMENT_RESULT:
                return new PaymentResultRequestHandler();
        }
        return null;
    }

    interface RequestHandler {
        Response getResponse(IHTTPSession session);
    }

    class NewPaymentRequestHandler implements RequestHandler {
        private static final String TAG = "NewPaymentRequestHandle";

        @Override
        public Response getResponse(IHTTPSession session) {
            Map<String, String> parms = session.getParms();
            if (!parms.containsKey(PARAM_STORE_ORDER_ID) || !parms.containsKey(PARAM_NOUNCE)) {
                return badRequest();
            }
            Log.d(TAG, "getResponse: New Payer arrived");
            Log.d(TAG, "getResponse: For order - "  + parms.get(PARAM_STORE_ORDER_ID));
//            CustomerOrder order = StoreManager.getNewOrder(parms.get(PARAM_STORE_ORDER_ID), parms.get(PARAM_NOUNCE));
            Order order = mRepository.getOrderPOJO(parms.get(PARAM_STORE_ORDER_ID));
            if (order == null) {
                return notFound();
            } else if (!order.isPending()) {
                return badRequest("Invalid Order");
            }
            order.setStatus(CustomerOrder.STATUS_PROCESSING);
            mRepository.updateOrder(order);
//            mRepository.insertNewOrder(order);
            order.initTotals();
            String paymentUri = order.getPaymentUri(mHostName, getListeningPort());
            Log.d(TAG, "getResponse: " + Uri.decode(paymentUri));
            return newFixedLengthResponse(Response.Status.OK, PLAIN_TEXT, paymentUri);
        }
    }

    class PaymentResultRequestHandler implements RequestHandler {
        private static final String TAG = "PaymentResultRequestHan";
        @Override
        public Response getResponse(IHTTPSession session) {
            ObjectMapper disputesObjectMapper = new ObjectMapper();
            try {
                JsonNode node = disputesObjectMapper.readTree(session.getInputStream());
                PaymentResponseEntity response = disputesObjectMapper.readValue(node, new TypeReference<PaymentResponseEntity>(){});
                if(response != null){
                    Log.d(TAG, "getResponse: " + response);
                    //TODO: Validate payment response
                    boolean validatedLocally = false;
                    try {
                        Map<String, String> parms = session.getParms();
                        if(parms.containsKey("BuyerId")) {
                            response.setBuyerId(parms.get("BuyerId"));
                        }
                        if(parms.containsKey("MerchantId")) {
                            response.setMerchantId(parms.get("MerchantId"));
                        }
                        if(parms.containsKey("TransactionFee")) {
                            response.setTransactionFee(Double.parseDouble(parms.get("TransactionFee")));
                        }
                        //TODO: remove this after SDK Push
                        response.setSignature(response.getStatusDescription());

                        validatedLocally = new Verification(StoreApp.getContext()).verify(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(validatedLocally) {
                        CustomerOrder order = mRepository.getOrderPOJO(response.getMerchantOrderId());
                        if(order != null && order.setAppropriateStatus(response)){
                            response.setOrderId(order.getId());
                            mRepository.updateOrder(order);
                            mRepository.insertPaymentResponse(response);
                            return newFixedLengthResponse(Response.Status.OK, PLAIN_TEXT, SUCCESSFUL_MSG);
                        }
                    } else {
                        return badRequest("Bad Data");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                return interalServerError();
            }
            return badRequest();
        }
    }

}