
package com.example.classplanner.util;

import android.graphics.Color;

public class ColorPalette {

    public static final int[] COLORS = {

            Color.rgb(66, 133, 244),
            Color.rgb(52, 168, 83),
            Color.rgb(251, 188, 5),
            Color.rgb(234, 67, 53),
            Color.rgb(171, 71, 188),
            Color.rgb(0, 172, 193),
            Color.rgb(255, 112, 67),
            Color.rgb(121, 85, 72),
            Color.rgb(236, 64, 122),
            Color.rgb(96, 125, 139)
    };

    public static int getColor(int index) {

        return COLORS[index % COLORS.length];
    }
}
