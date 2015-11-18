/*
 * CardboardMenu.java
 * Copyright (C) 2015 sean <sean@Seans-MBP.lan>
 *
 * Distributed under terms of the MIT license.
 */

package io.kirmani.daydream.cardboard;

import io.kirmani.daydream.R;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;

public class CardboardMenu extends CardboardObject {
    private static float mMenuDistance = 10f;

    CardboardScene mScene;

    public CardboardMenu(Context context, CardboardScene scene) {
        super(context, scene);
        mScene = scene;
    }

    @Override
    public void onSurfaceCreated(EGLConfig config) {
        super.onSurfaceCreated(config);
        checkGLError("onSurfaceCreated");

        float[] menuCoords = createMenuCoords(2);

        ByteBuffer bbVertices = ByteBuffer.allocateDirect(menuCoords.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        setVertices(bbVertices.asFloatBuffer());
        getVertices().put(menuCoords);
        getVertices().position(0);

        ByteBuffer bbColors = ByteBuffer.allocateDirect(MENU_COLORS.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        setColors(bbColors.asFloatBuffer());
        getColors().put(MENU_COLORS);
        getColors().position(0);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(MENU_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        setNormals(bbNormals.asFloatBuffer());
        getNormals().put(MENU_NORMALS);
        getNormals().position(0);

        int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);

        setProgram(GLES20.glCreateProgram());
        GLES20.glAttachShader(getProgram(), vertexShader);
        GLES20.glAttachShader(getProgram(), passthroughShader);
        GLES20.glLinkProgram(getProgram());
        GLES20.glUseProgram(getProgram());

        checkGLError("Menu program");

        setModelParam(GLES20.glGetUniformLocation(getProgram(), "u_Model"));
        setModelViewParam(GLES20.glGetUniformLocation(getProgram(), "u_MVMatrix"));
        setModelViewProjectionParam(GLES20.glGetUniformLocation(getProgram(), "u_MVP"));
        setLightPosParam(GLES20.glGetUniformLocation(getProgram(), "u_LightPos"));

        setPositionParam(GLES20.glGetAttribLocation(getProgram(), "a_Position"));
        setNormalParam(GLES20.glGetAttribLocation(getProgram(), "a_Normal"));
        setColorParam(GLES20.glGetAttribLocation(getProgram(), "a_Color"));

        GLES20.glEnableVertexAttribArray(getPositionParam());
        GLES20.glEnableVertexAttribArray(getNormalParam());
        GLES20.glEnableVertexAttribArray(getColorParam());

        checkGLError("Menu program params");

        Matrix.setIdentityM(getModel(), 0);
        Matrix.translateM(getModel(), 0, 0, 0, -mMenuDistance);
        checkGLError("onSurfaceCreated");
    }

    public void onNewFrame(HeadTransform headTransform) {
        // Make menu follow head.
        float[] forwardVector = new float[3];
        float[] position = mScene.getPosition();
        headTransform.getForwardVector(forwardVector, 0);
        Matrix.setIdentityM(getModel(), 0);
        Matrix.translateM(getModel(), 0,
                -forwardVector[0] * mMenuDistance,
                -forwardVector[1] * mMenuDistance,
                forwardVector[2] * mMenuDistance);
        // TODO(kirmani): Rotate Menu to Look at Camera
        // TODO(kirmani): Make Menu follow Camera when Flying Around
    }

    @Override
    public void onDrawEye(Eye eye) {
        super.onDrawEye(eye);
        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        Matrix.multiplyMM(getModelView(), 0, getView(), 0, getModel(), 0);
        Matrix.multiplyMM(getModelViewProjection(), 0, getPerspective(), 0, getModelView(), 0);
        draw();
    }

    public void draw() {
        GLES20.glUseProgram(getProgram());
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(getLightPosParam(), 1, getLightPosInEyeSpace(), 0);
        GLES20.glUniformMatrix4fv(getModelParam(), 1, false, getModel(), 0);
        GLES20.glUniformMatrix4fv(getModelViewParam(), 1, false, getModelView(), 0);
        GLES20.glUniformMatrix4fv(getModelViewProjectionParam(), 1, false,
                getModelViewProjection(), 0);
        GLES20.glVertexAttribPointer(getPositionParam(), COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, getVertices());
        GLES20.glVertexAttribPointer(getNormalParam(), 3, GLES20.GL_FLOAT, false, 0,
                getNormals());
        GLES20.glVertexAttribPointer(getColorParam(), 4, GLES20.GL_FLOAT, false, 0, getColors());

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        checkGLError("drawing menu");
    }

    private float[] createMenuCoords(int numItems) {
        return new float[] {-1.0f * numItems, 1.0f, 1.0f,
            -1.0f * numItems, -1.0f, 1.0f,
            1.0f * numItems, 1.0f, 1.0f,
            -1.0f * numItems, -1.0f, 1.0f,
            1.0f * numItems, -1.0f, 1.0f,
            1.0f * numItems, 1.0f, 1.0f,
        };
    }

    public static final float[] MENU_COLORS = new float[] {
        0f, 0.5273f, 0.2656f, 0.6f,
            0f, 0.5273f, 0.2656f, 0.6f,
            0f, 0.5273f, 0.2656f, 0.6f,
            0f, 0.5273f, 0.2656f, 0.6f,
            0f, 0.5273f, 0.2656f, 0.6f,
            0f, 0.5273f, 0.2656f, 0.6f,
    };

    public static final float[] MENU_NORMALS = new float[] {
        0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
    };
}

