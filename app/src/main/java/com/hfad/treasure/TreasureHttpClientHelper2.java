package com.hfad.treasure;

import android.os.Environment;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


import java.io.*;

public class TreasureHttpClientHelper2 {
    private final String TAG = "TreasureHttpClientHelper2";

    private StringEntity coordEntity;
    private StringEntity contEntity;
    public void setContEntity(String string) throws UnsupportedEncodingException {
        this.contEntity = new StringEntity(string);
    }
    public void setCoordEntity(String string) throws UnsupportedEncodingException {
        this.coordEntity = new StringEntity(string);
    }

    private int lastContentId;
    private String lastFileName;
    public void setLastContentId(int lastContentId) {
        this.lastContentId = lastContentId;
    }
    public void setLastFileName(String lastFileName) {
        this.lastFileName = lastFileName;
    }

    public void resetJSONResponse() {
        this.JSONResponse = null;
    }

    private String JSONResponse = null;
    public String getJSONResponse() {
        return JSONResponse;
    }

    public String getIsDownloaded() {
        return isDownloaded;
    }

    private String isDownloaded = "0";


    public void SendCoordinates(String coordinateurl){
        new AsyncHttpClient().setBasicAuth("user", "password");
        new AsyncHttpClient().post(null, coordinateurl, coordEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                JSONResponse = new String(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("WSSend", "Hi! Error colling API: " + statusCode);
            }
        });
    }

    public void SendListOfContent(String contenturl){


        new AsyncHttpClient().post(null, contenturl, contEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Log.d("UPLOADING file", responseBody.toString());

                try {
                    FileOutputStream f = new FileOutputStream((new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)   + File.separator + lastFileName)));
                    f.write(responseBody); //your bytes
                    f.close();
                    isDownloaded = "1";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("WSSend", "Hi! Error colling API: " + statusCode);
            }
        });


    }
}