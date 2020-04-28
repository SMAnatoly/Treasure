package com.hfad.treasure;

import android.content.Context;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.HttpEntity;
public class TreasureHttpClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
        client.post(context, url, entity, contentType, responseHandler);
    }

}
