/*
 * CardboardScene.java
 * Copyright (C) 2015 sean <sean@wireless-10-147-155-193.public.utexas.edu>
 *
 * Distributed under terms of the MIT license.
 */

package io.kirmani.daydream.cardboard;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import javax.microedition.khronos.egl.EGLConfig;

public class CardboardScene {
    private static final float SPEED = 0.4f;
    private static final float CAMERA_Z = 0.01f;

    private float mCameraX;
    private float mCameraY;
    private float mCameraZ;

    private float[] mCamera;
    private float[] mView;
    private float[] mModelView;
    private float[] mModelViewProjection;
    private float[] mPerspective;

    private boolean mIsFlying;

    public CardboardScene() {
        mView = new float[16];
        mModelView = new float[16];
        mModelViewProjection = new float[16];
        mPerspective = new float[16];
        mCamera = new float[16];

        mCameraX = 0.00f;
        mCameraY = 0.00f;
        mCameraZ = 0.01f;

        mIsFlying = false;
    }

    public void onSurfaceCreated(EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well.
    }

    public void onNewFrame(HeadTransform headTransform) {
        // Build the camera matrix and apply it to the ModelView.
        float[] forwardVector = new float[3];
        headTransform.getForwardVector(forwardVector, 0);
        if (mIsFlying) {
            mCameraX += forwardVector[0] * SPEED;
            mCameraY += forwardVector[1] * SPEED;
            mCameraZ -= forwardVector[2] * SPEED;
        }
        Matrix.setLookAtM(mCamera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }

    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(getView(), 0, eye.getEyeView(), 0, mCamera, 0);
        Matrix.translateM(getView(), 0, mCameraX, mCameraY, mCameraZ);
        setPerspective(eye.getPerspective(0.01f, 100f));
    }

    public void toggleFlight() {
        mIsFlying = !mIsFlying;
    }

    public float[] getView() {
        return mView;
    }

    public void setPerspective(float[] perspective) {
        mPerspective = perspective;
    }

    public float[] getPerspective() {
        return mPerspective;
    }
}
