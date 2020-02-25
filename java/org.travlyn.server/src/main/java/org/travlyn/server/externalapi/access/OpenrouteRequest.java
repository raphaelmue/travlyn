package org.travlyn.server.externalapi.access;

import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.Stop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class OpenrouteRequest {
    private static final String BASE_URL = "https://api.openrouteservice.org/pois";

    public String getPOIS(double lon, double lat){
        HashSet<Pair<String,String>> header = new HashSet<>();
        header.add(new Pair<>("Authorization","5b3ce3597851110001cf62487839b1884ada4627bbe7c52c372087fd"));
        APIRequest request = new APIRequest(BASE_URL,new HashSet<>(),this.genPostBody(),header);
        String result;
        try {
            result = request.performAPICallPOST();
        }catch (IOException e){
            return null;
        }
        return result;
    }

    private String genPostBody(){
        return "{\"request\":\"pois\",\"geometry\":{\"bbox\":[[" + 8.40 +","+ 49.01 + "],["+8.45+","+49.06+"]],\"geojson\":{\"type\":\"Point\",\"coordinates\":["+8.404435 +","+ 49.013513+"]},\"buffer\":200},\"filters\":{\"category_group_ids\":[620]}}";
    }
}
