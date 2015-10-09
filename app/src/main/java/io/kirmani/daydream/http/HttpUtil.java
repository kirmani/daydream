/*
 * HttpUtil.java
 * Copyright (C) 2015 sean <sean@Seans-MBP.lan>
 *
 * Distributed under terms of the MIT license.
 */

package io.kirmani.daydream.http;

import io.kirmani.daydream.R;
import io.kirmani.daydream.cardboard.CardboardObject;
import io.kirmani.daydream.cardboard.CardboardOverlayView;
import io.kirmani.daydream.cardboard.CardboardScene;

import android.app.Activity;
import android.util.Log;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonParser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;


public class HttpUtil extends CardboardObject {
    private static final String TAG = "HttpUtil";

    private static final String HTTP_REQUEST_URL = "http://daydream.kirmani.io";
    private static final long SECOND = 500000000;

    private static JsonParser mUpdateJsonParser;

    private BigDecimal mTriggers;
    private long mLastUpdate;

    private Activity mActivity;

    public HttpUtil(Activity activity, CardboardScene scene) {
        super(activity, scene);
        mActivity = activity;
        mTriggers = BigDecimal.ZERO;
        mLastUpdate = 0;
    }

    public void post() {
        new CardboardTriggerTask().execute();
    }

    public void update() {
        long now = System.nanoTime();
        if (now - mLastUpdate > SECOND) {
            new DaydreamUpdateTask().execute();
            mLastUpdate = now;
        }
    }

    private class CardboardTriggerTask extends AsyncTask<Void, Void, HttpResponse> {
        protected HttpResponse doInBackground(Void... values) {
            try {
                byte[] content = new byte[300];
                Arrays.fill(content, (byte) ' ');
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                HttpRequest request = httpTransport.createRequestFactory().buildPostRequest(
                        new GenericUrl(HTTP_REQUEST_URL), new ByteArrayContent(null, content));
                request.setRequestMethod(HttpMethods.POST);
                HttpResponse resp = request.execute();
                return resp;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return null;
        }

        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "Fetching...");
        }

        protected void onPostExecute(Void... values) {
            Log.i(TAG, "Executed");
        }
    }

    private class DaydreamUpdateTask extends AsyncTask<Void, Void, HttpResponse> {
        private AndroidJsonFactory mJsonFactory;
        private CardboardOverlayView mOverlayView;

        protected HttpResponse doInBackground(Void... values) {
            try {
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                HttpRequest request = httpTransport.createRequestFactory().buildGetRequest(
                        new GenericUrl(HTTP_REQUEST_URL));
                request.setRequestMethod(HttpMethods.GET);
                HttpResponse resp = request.execute();
                return resp;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return null;
        }

        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "Fetching...");
        }

        protected void onPostExecute(HttpResponse response) {
            Log.i(TAG, "Executed");
            try {
                JsonParser jsonParser =
                    (new AndroidJsonFactory()).createJsonParser(response.getContent());
                if (jsonParser == null) { return; }

                Map<String, Object> json = jsonParser.parseAndClose(Map.class);
                Map<String, Object> result = (Map<String, Object>) json.get("result");
                BigDecimal triggers = (BigDecimal) result.get("triggers");
                if (triggers.compareTo(mTriggers) > 0) {
                    if (!mTriggers.equals(BigDecimal.ZERO)) {
                        mOverlayView = (CardboardOverlayView) mActivity.findViewById(R.id.overlay);
                        mOverlayView.show3DToast("Someone has clicked the screen!");
                    }
                    mTriggers = triggers;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}

