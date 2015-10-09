
/*
 * MainActivity.java
 * Copyright (C) 2015 sean <sean@wireless-10-147-155-193.public.utexas.edu>
 *
 * Distributed under terms of the MIT license.
 */

package io.kirmani.daydream;

import io.kirmani.daydream.cardboard.CardboardCamera;
import io.kirmani.daydream.cardboard.CardboardCube;
import io.kirmani.daydream.cardboard.CardboardFloor;
import io.kirmani.daydream.cardboard.CardboardScene;
import io.kirmani.daydream.http.HttpUtil;

import android.os.Bundle;
import android.util.Log;

import com.google.api.client.json.JsonParser;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * A Cardboard template application.
 */
public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {
    private static final String TAG = "MainActivity";

    private CardboardCube mCube;
    private CardboardFloor mFloor;
    private CardboardCamera mCamera;
    private CardboardScene mScene;
    private HttpUtil mHttpUtil;

    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRestoreGLStateEnabled(false);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        mScene = new CardboardScene();

        mCube = new CardboardCube(this, mScene);
        mFloor = new CardboardFloor(this, mScene);
        mCamera = new CardboardCamera(this, mScene);

        mHttpUtil = new HttpUtil(this, mScene);
    }

    @Override
    public void onRendererShutdown() {}

    @Override
    public void onSurfaceChanged(int width, int height) {}

    /**
     * Creates the buffers we use to store information about the 3D world.
     *
     * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
     * Hence we use ByteBuffers.
     *
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        mCube.onSurfaceCreated(config);
        mFloor.onSurfaceCreated(config);
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        mHttpUtil.update();
        mCube.onNewFrame(headTransform);
        mCamera.onNewFrame(headTransform);
    }

    /**
     * Draws a frame for an eye.
     *
     * @param eye The eye to render. Includes all required transformations.
     */
    @Override
    public void onDrawEye(Eye eye) {
        mCamera.onDrawEye(eye);
        mCube.onDrawEye(eye);
        mFloor.onDrawEye(eye);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {}

    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        mHttpUtil.post();
        mCube.onCardboardTrigger();
    }
}
