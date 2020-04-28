package com.hfad.treasure;

import android.content.ContentValues;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Content{
    private final static String TAG = "Content";
    private ArrayList<Integer> id = new ArrayList<>();
    private ArrayList<String> accauntName = new ArrayList<>();
    private ArrayList<String> userName = new ArrayList<>();
    private ArrayList<String> itemName = new ArrayList<>();
    private ArrayList<String> contentType = new ArrayList<>();
    private ArrayList<String> isDownloaded = new ArrayList<>();

    private ContentValues contentValues;
    //private static ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>();

    private static int lastContentId;
    private static String lastFileName;

    public static int getLastContentId() {
        return lastContentId;
    }

    public static String getLastFileName() {
        return lastFileName;
    }

    //public static ArrayList<ContentValues> getcontentValuesArrayList(){return  contentValuesArrayList; }

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

    public ArrayList<Integer> getId() { return id; }

    public void setId(Integer id) {  this.id.add(id); }

    public ArrayList<String> getItemName() { return itemName; }

    public void setItemName(String itemName) { this.itemName.add(itemName); }

    public ArrayList<String> getContentType() { return contentType; }

    public void setContentType(String contentType) {  this.contentType.add(contentType); }

    public ArrayList<String> getIsDownloaded() {  return isDownloaded;  }

    public void setIsDownloaded(String isDownloaded) { this.isDownloaded.add(isDownloaded); }


    public void TransformJSONtoContentValue(String string) throws JSONException {
        contentValues = new ContentValues();
        JSONObject jsonObject = new JSONObject(string);

        contentValues.put("accauntName", "Accaunt1");
        contentValues.put("userName", "");
        contentValues.put("itemName", jsonObject.getString("itemName"));
        contentValues.put("contentType", jsonObject.getString("contentType"));
        contentValues.put("dt", TimeStamp.currenttime().toString());
        contentValues.put("isDownloaded", 0);
    }

    public static ArrayList<ContentValues> TransformJSONtoContentValueArrayList(String string) throws JSONException {
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>();
        String[] lines = string.split("\\r?\\n");
        JSONObject[] jsonObject = new JSONObject[lines.length];
        ContentValues[] cv = new ContentValues[lines.length];

        for (int i = 0; i < lines.length; i++) {
            jsonObject[i] = new JSONObject(lines[i]);
            cv[i] = new ContentValues();
            cv[i].put("accauntName", "Accaunt1");
            cv[i].put("userName", "SAG");
            cv[i].put("itemName", jsonObject[i].getString("itemName"));
            cv[i].put("contentType", jsonObject[i].getString("contentType"));
            cv[i].put("dt", TimeStamp.currenttime().toString());
            cv[i].put("isDownloaded", 0);
            contentValuesArrayList.add(cv[i]);
        }
        Log.d(" PrepareJSONContentArr: ", "contentValuesArrayList: " + contentValuesArrayList);
        return contentValuesArrayList;
    }

    public static String ToJSONString(Content content) throws Exception {
        Log.d(TAG + ".ToJSONString","content.getId().toString()" + content.getId().toString());
        JSONObject jsonPContent = new JSONObject();
        if(content.getId().size() > 0){
            try {
                jsonPContent.put("itemName", content.getItemName().get(0));
                jsonPContent.put("contentType", content.getContentType().get(0));
                lastContentId = content.getId().get(0);
                lastFileName = content.getItemName().get(0);
                Log.d("PrepareContentEntity", "lastFileName: " + lastFileName + " lastContentId: "+ lastContentId);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception();
        }
        return jsonPContent.toString();
    }
}
