package de.travlyn.prototyping;

import java.io.*;

public class Test {
    public static void main(String[] args) {
        new Test();
    }

    public Test(){
        try {
           callPOISOpenRouteService();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void callDirectionsOpenRouteService() throws Exception{
        String key = this.readKey("OpenRouteService");
        APIRequest request = new APIRequest("https://api.openrouteservice.org/v2/directions/driving-car?start=8.369973,49.021083&end=8.350135,49.001056", key);
        System.out.println(request.performAPIGetCall());
    }

    private void callPOISOpenRouteService() throws Exception {
        APIRequest request = new APIRequest("https://api.openrouteservice.org/pois");
        System.out.println(request.performAPIPostCall("{\"request\":\"pois\",\"geometry\":{\"bbox\":[[8.389778,49.021435],[8.419476,49.00511]],\"geojson\":{\"type\":\"Point\",\"coordinates\":[8.404713,49.013217]},\"buffer\":200},\"filters\":{\"category_group_ids\":[620]}}"));
    }

    private void callPOISFoursquare() throws Exception{
        APIRequest request = new APIRequest("https://api.foursquare.com/v2/venues/explore?ll=40.7243,-74.0018&query=coffee&v=20180323&limit=1&client_id=J2Z1OVD4XKVCKFMFOLPF2INOYVOOIT0NVV5COTNLNQFDWPYD&client_secret="+readKey("Foursquare"));
        System.out.println(request.performAPIGetCall());
    }
    private String readKey(String service) {
        if(service=="OpenRouteService"){
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(
                    Test.class.getResourceAsStream("OpenRouteService.txt")))) {
                return fileReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }else if(service=="Foursquare"){
            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(
                    Test.class.getResourceAsStream("Foursquare.txt")))) {
                return fileReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
