/*
 * CardboardCamera.java
 * Copyright (C) 2015 sean <sean@wireless-10-147-155-193.public.utexas.edu>
 *
 * Distributed under terms of the MIT license.
 */

package io.kirmani.daydream.cardboard;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import javax.microedition.khronos.egl.EGLConfig;

public class CardboardCamera extends CardboardObject {
    private static final float SPEED = 0.1f;
    private float mCameraX;
    private float mCameraY;
    private float mCameraZ;

    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] { 0.0f, 2.0f, 0.0f, 1.0f };

    public CardboardCamera(Context context, CardboardScene scene) {
        super(context, scene);
        setModel(new float[16]);
        mCameraX = 0.0f;
        mCameraY = 0.0f;
        mCameraZ = 0.01f;
    }

    @Override
    public void onSurfaceCreated(EGLConfig config) {
        super.onSurfaceCreated(config);
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        super.onNewFrame(headTransform);

        // Build the camera matrix and apply it to the ModelView.
        float[] forwardVector = new float[3];
        headTransform.getForwardVector(forwardVector, 0);
        Matrix.setLookAtM(getModel(), 0, mCameraX, mCameraY, mCameraZ, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f);
    }

    public void onDrawEye(Eye eye) {
        super.onDrawEye(eye);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(getView(), 0, eye.getEyeView(), 0, getModel(), 0);

        // Set the position of the light
        Matrix.multiplyMV(getLightPosInEyeSpace(), 0, getView(), 0, LIGHT_POS_IN_WORLD_SPACE, 0);
    }
}

