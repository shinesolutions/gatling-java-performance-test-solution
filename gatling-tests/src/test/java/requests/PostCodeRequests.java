package requests;

import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.util.HashMap;
import java.util.Map;

public class PostCodeRequests {

    static Map<String,String> headers = new HashMap<>() {{
        put("Content-Type", "application/json");
    }};

    public static ChainBuilder GET_Postcode_Random(String host) {
        return exec(http("GET_Postcode_Random")
                .get(host + "/random/postcodes")
                .headers(headers));
    }

    public static ChainBuilder GET_Postcode(String host, String postcode) {
        return exec(http("GET_Postcode")
                .get(host + "/postcodes/" + postcode)
                .headers(headers));
    }

}
