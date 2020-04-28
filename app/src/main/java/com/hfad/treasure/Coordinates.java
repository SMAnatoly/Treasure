package com.hfad.treasure;

import android.util.Log;
import cz.msebera.android.httpclient.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Coordinates {
    private final static String TAG = "Coordinates";
    private ArrayList<Integer> id = new ArrayList<>();
    private ArrayList<String> accauntName = new ArrayList<>();
    private ArrayList<String> userName = new ArrayList<>();
    private ArrayList<String> latitude = new ArrayList<>();
    private ArrayList<String> longitude = new ArrayList<>();
    private ArrayList<String> isSent = new ArrayList<>();

    public ArrayList<String> getAccauntName() {
        return accauntName;
    }

    public void setAccauntName(String accauntName) {
        this.accauntName.add(accauntName);
    }

    public ArrayList<String> getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.add(userName);
    }

    public ArrayList<String> getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude.add(latitude);
    }

    public ArrayList<String> getLongitude() { return longitude; }

    public void setLongitude(String longitude) {
        this.longitude.add(longitude);
    }

    public ArrayList<String> getIsSent() { return isSent; }

    public void setIsSent(String longitude) {
        this.isSent.add(longitude);
    }

    public ArrayList<Integer> getId() { return id; }

    public void setId(Integer id) {  this.id.add(id); }

    public static String ToJSONString(Coordinates coordinates) throws Exception {
        Log.d(TAG + ".PrepareContentEntity","coordinates.getId().size(): " + coordinates.getId().size() + " - "+ coordinates.getId().toString());
        JSONObject jsonParams = new JSONObject();
        if(coordinates.getId().size() > 0){
            if(coordinates.getLatitude().size() > 0) {
                try {
                    jsonParams.put("accauntName", coordinates.getAccauntName().get(0));
                    jsonParams.put("userName", coordinates.getUserName().get(0));
                    jsonParams.put("lat", coordinates.getLatitude().get(0));
                    jsonParams.put("lon", coordinates.getLongitude().get(0));
                    //this.coordEntity = new StringEntity(jsonParams.toString());
                    Log.d("PrepareCoordinatesEntity", "content: " + coordinates.getLatitude().get(0));
                } catch (JSONException e ) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new Exception();
        }
        return jsonParams.toString();
    }
}
