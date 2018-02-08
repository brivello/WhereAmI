package tech.brivello.whereami;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Adress {
    private int lod = -1;
    private long osm_id = -1;
    private String osm_type = "";
    private String country_code = "";
    private String country = "";
    private String postcode = "";
    private String state = "";
    private String county = "";
    private String city = "";
    private String suburb = "";
    private String road = "";
    private String display_name = "";

    public Adress(String json, int lod){
        Log.d("address", "start");
        try {
            JSONObject jObject = new JSONObject(json);

            if(jObject.has("error")){
                System.err.println(jObject.get("error"));
                return;
            }

            osm_id = jObject.getLong("osm_id");
            osm_type = jObject.getString("osm_type");
            display_name = jObject.getString("display_name");

            JSONObject addressObject = jObject.getJSONObject("address");
            if(addressObject.has("country_code")){
                country_code = addressObject.getString("country_code");
            }
            if(addressObject.has("country")){
                country = addressObject.getString("country");
            }
            if(addressObject.has("postcode")){
                postcode = addressObject.getString("postcode");
            }
            if(addressObject.has("state")){
                state = addressObject.getString("state");
            }
            if(addressObject.has("county")){
                county = addressObject.getString("county");
            }

            if(addressObject.has("city")){
                city = addressObject.getString("city");
                Log.d("address", "have city");
            }
            if(addressObject.has("suburb")){
                suburb = addressObject.getString("suburb");
            }
            if(addressObject.has("road")){
                road = addressObject.getString("road");
            }

            this.lod = lod;
        } catch (JSONException e) {
            Log.d("address", "Can't parse JSON string");
            System.err.println("Can't parse JSON string");
            e.printStackTrace();
        }
    }

    public long getOsmId(){
        return osm_id;
    }

    public String getOsmType(){
        return osm_type;
    }

    public int getLod(){
        return lod;
    }

    public String getCountryCode(){
        return country_code;
    }

    public String getCountry(){
        return country;
    }

    public String getPostcode(){
        return postcode;
    }

    public String getState(){
        return state;
    }

    public String getCounty(){
        return county;
    }

    public String getCity(){
        return city;
    }

    public String getSuburb(){
        return suburb;
    }

    public String getRoad(){
        return road;
    }

    public String getDisplayName(){
        return display_name;
    }

    public String toString(){
        return display_name;
    }
}