package examples.mob.yenepay.com.checkoutcounter.wifiP2P;

import android.support.annotation.NonNull;

import java.util.Map;

import examples.mob.yenepay.com.checkoutcounter.store.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;
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
    private String mHostName = null;

    public StoreServer(int port) {
        super(port);
    }

    public StoreServer(String host, int port) {
        super(port);
        mHostName = host;
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> params = session.getParms();
        if(params.isEmpty() || !params.containsKey(PARAM_NAME_SERVICE_TYPE)){
            return badRequest();
        }
        String serviceType = params.get(PARAM_NAME_SERVICE_TYPE);
        RequestHandler handler = getHandler(serviceType);
        if(handler == null){
            return notFound();
        }
        return handler.getResponse(session);
    }
    @NonNull
    private Response notFound() {
        return statusResponse(Response.Status.NOT_FOUND,null);
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
    private Response badRequest(String msg) {
        return statusResponse(Response.Status.BAD_REQUEST, msg);
    }

    public RequestHandler getHandler(String serviceType){
        switch (serviceType) {
            case SERVICE_TYPE_NEW_PAYMENT:
                return new NewPaymentRequestHandler();
            case SERVICE_TYPE_PAYMENT_RESULT:
                return new PaymentResultRequestHandler();
        }
        return null;
    }

    interface RequestHandler{
        Response getResponse(IHTTPSession session);
    }

    class NewPaymentRequestHandler implements RequestHandler{

        @Override
        public Response getResponse(IHTTPSession session) {
            Map<String, String> parms = session.getParms();
            if(!parms.containsKey(PARAM_STORE_ORDER_ID) || !parms.containsKey(PARAM_NOUNCE)){
                return badRequest();
            }
            CustomerOrder order = StoreManager.getNewOrder(parms.get(PARAM_STORE_ORDER_ID), parms.get(PARAM_NOUNCE));
            if(order == null){
                return notFound();
            } else if(!order.isPending()){
                return badRequest("Invalid Order");
            }
            return newFixedLengthResponse(Response.Status.OK, PLAIN_TEXT, order.getPaymentUri(mHostName, getListeningPort()));
        }
    }

    class PaymentResultRequestHandler implements RequestHandler{

        @Override
        public Response getResponse(IHTTPSession session) {
            return null;
        }
    }
}
