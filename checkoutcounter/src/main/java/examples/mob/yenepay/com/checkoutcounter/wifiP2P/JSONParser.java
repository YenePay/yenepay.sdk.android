package examples.mob.yenepay.com.checkoutcounter.wifiP2P;

import android.text.TextUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sisay Getnet on 1/4/2018.
 */

public class JSONParser {
    private String json;
    private ObjectMapper objectMapper;
    private JsonNode rootNode;
    public JSONParser(String jsonString) throws Exception {
        json = jsonString;
        objectMapper = new ObjectMapper();
        rootNode = objectMapper.readTree(json);
    }
    public <T> T parseJson() throws Exception {
        return parseJson(null);
    }
    public <T> T parseJson(String treePath) throws Exception {
        if(TextUtils.isEmpty(treePath)) {
            return objectMapper.readValue(rootNode, new TypeReference<T>() {
            });
        } else{
            return objectMapper.readValue(rootNode.findValue(treePath), new TypeReference<T>() {
            });
        }
    }
    public static String convertToJSON(Object obj) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

//    public static class JsonParser implements ISelfJSONParser {
//
//        public Object parseJson(String str) throws IOException {
//            ObjectMapper objectMapper = new ObjectMapper();
//            return objectMapper.readValue(objectMapper.readTree(str), new TypeReference<ArrayList<AirTimeCard>>(){});
//        }
//    }
}
