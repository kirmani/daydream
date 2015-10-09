/*
 * CardboardLight.java
 * Copyright (C) 2015 sean <sean@Seans-MBP.lan>
 *
 * Distributed under terms of the MIT license.
 */

package io.kirmani.daydream.cardboard;

import android.content.Context;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;

public class CardboardLight extends CardboardObject {
    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] { 0.0f, 2.0f, 0.0f, 1.0f };

    public CardboardLight(Context context, CardboardScene scene) {
        super(context, scene);
    }

    public void onDrawEye(Eye eye) {
        // Set the position of the light
        Matrix.multiplyMV(getLightPosInEyeSpace(), 0, getView(), 0, LIGHT_POS_IN_WORLD_SPACE, 0);
    }
}

