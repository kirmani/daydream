/*
 * HttpUtil.java
 * Copyright (C) 2015 sean <sean@Seans-MBP.lan>
 *
 * Distributed under terms of the MIT license.
 */

package io.kirmani.daydream.cardboard;

import android.content.Context;
import android.util.Log;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.vrtoolkit.cardboard.HeadTransform;

import java.util.Arrays;

public class CardboardHttpObject extends CardboardObject {
    private static final String TAG = "HttpUtil";

    private static final String HTTP_REQUEST_URL = "http://daydream.kirmani.io";
    private static final long SECOND = 500000000;

    private long mLastUpdate;

    public CardboardHttpObject(Context context, CardboardScene scene) {
        super(context, scene);
        mLastUpdate = 0;
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        super.onNewFrame(headTransform);
        update();
    }

    protected void onUpdate(HttpResponse response) throws Exception {}
    protected HttpContent sendContentOnTrigger() throws Exception {
        byte[] content = new byte[300];
        Arrays.fill(content, (byte) ' ');
        return new ByteArrayContent(null, content);
    }

    private void update() {
        long now = System.nanoTime();
        if (now - mLastUpdate > SECOND) {
            new DaydreamUpdateTask().execute();
            mLastUpdate = now;
        }
    }

    private class DaydreamUpdateTask extends AsyncTask<Void, Void, HttpResponse> {
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
                onUpdate(response);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private class CardboardTriggerTask extends AsyncTask<Void, Void, HttpResponse> {
        protected HttpResponse doInBackground(Void... values) {
            try {
                HttpContent content = sendContentOnTrigger();
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                HttpRequest request = httpTransport.createRequestFactory().buildPostRequest(
                        new GenericUrl(HTTP_REQUEST_URL), content);
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

        protected void onPostExecute(HttpResponse response) {
            Log.i(TAG, "Executed");
        }
    }

    @Override
    public void onCardboardTrigger() {
        super.onCardboardTrigger();
        post();
    }

    private void post() {
        new CardboardTriggerTask().execute();
    }

}

