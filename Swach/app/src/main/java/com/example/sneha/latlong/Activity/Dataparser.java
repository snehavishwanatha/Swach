package com.example.sneha.latlong.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dataparser {
    private HashMap<String,String> getsinglenearbyplace(JSONObject googleplacejson){
        HashMap<String,String> googleplacemap= new HashMap<>();
        String nameofplace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        try {
            if(!googleplacejson.isNull("name")){
                nameofplace = googleplacejson.getString("name");
            }
            if(!googleplacejson.isNull("vicinity")){
                vicinity = googleplacejson.getString("vicinity");
            }
            latitude=googleplacejson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude=googleplacejson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googleplacejson.getString("reference");

            googleplacemap.put("place_name",nameofplace);
            googleplacemap.put("vicinity",vicinity);
            googleplacemap.put("lat",latitude);
            googleplacemap.put("lng",longitude);
            googleplacemap.put("reference",reference);

        } catch (JSONException e) {

            e.printStackTrace();
        }
        return googleplacemap;
    }
    private List<HashMap<String,String>> getallnearbyplaces(JSONArray jsonArray){
        int counter = jsonArray.length();
        List<HashMap<String,String>> nearbyplaceslist = new ArrayList<>();
        HashMap<String,String> nearbyplacemap = null;
        for(int i=0;i<=counter;i++){
            try {
                nearbyplacemap = getsinglenearbyplace((JSONObject) jsonArray.get(i));
                nearbyplaceslist.add(nearbyplacemap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearbyplaceslist;
    }

    public List<HashMap<String,String>> parse (String jsondata){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsondata);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getallnearbyplaces(jsonArray);
    }
}
