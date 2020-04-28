package com.hfad.treasure;

import android.content.ContentValues;
import android.os.Environment;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import org.json.JSONException;

import java.io.*;

public class WSSender {
    private final String TAG = "WSSender";
    private DatabaseHelper databaseHelper;
    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    private StringEntity coordEntity;
    private StringEntity contEntity;

    private int lastContentId;
    private String lastFileName;

    public void setLastContentId(int lastContentId) {
        this.lastContentId = lastContentId;
    }
    public void setLastFileName(String lastFileName) {
        this.lastFileName = lastFileName;
    }

    public void setContEntity(String string) throws UnsupportedEncodingException {
        this.contEntity = new StringEntity(string);
    }

    public void setCoordEntity(String string) throws UnsupportedEncodingException {
        this.coordEntity = new StringEntity(string);
    }

    public void SendCoordinates(String coordinateurl){
        new AsyncHttpClient().post(null, coordinateurl, coordEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                //Нужно полностью переработать!!
                //2. Нужно разобраться с асинхронностью и вообще избавится от передачи в метод объекта databaseHelper
                AddCoordinatesToDB(str);
                Log.d("SendCoordinates", "onSuccess: " + str);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("WSSend", "Hi! Error calling API: " + statusCode);
            }
        });
    }

    public void SendListOfContent(String contenturl, final DatabaseHelper databaseHelper){
        new AsyncHttpClient().post(null, contenturl, contEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                SaveFile(responseBody);
                UpdateContentDB(new ContentValues());
                Log.d("UPLOADING file", responseBody.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("WSSend", "Hi! Error colling API: " + statusCode);
            }
        });
    }

    private void AddCoordinatesToDB(String str){
        try {
            this.databaseHelper.addMultipleData("available_content", Content.TransformJSONtoContentValueArrayList(str), "ac_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void UpdateContentDB(ContentValues contentValues){
        contentValues.put("isDownloaded", 1);
        this.databaseHelper.updData("available_content", contentValues, "ac_id", lastContentId);
    }

    private void SaveFile(byte[] byteData){
        try {
            FileOutputStream f = new FileOutputStream((new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + lastFileName)));
            f.write(byteData);
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}