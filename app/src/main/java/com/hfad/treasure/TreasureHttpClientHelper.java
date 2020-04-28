package com.hfad.treasure;


import android.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class TreasureHttpClientHelper {
    private final String TAG = "TreasureHttpClientHelper";
    private StringEntity coordEntity;
    private JSONObject JSONresponse;
    public JSONObject getJSONresponse() {
        return JSONresponse;
    }

    public void setCoordEntity(String string) throws UnsupportedEncodingException {
        this.coordEntity = new StringEntity(string);
    }

    public void SendCoordinates(String url) throws JSONException {
        TreasureHttpClient.post(null, url, coordEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                JSONresponse = response;
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }
        });
    }

}
