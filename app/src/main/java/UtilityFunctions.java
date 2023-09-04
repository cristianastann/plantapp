package com.example.plantapp;

import android.content.Context;
import android.widget.ImageView;
import android.content.res.Configuration;

public class UtilityFunctions {

    public static void setSunMoonImage(Context context, ImageView imageView) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            imageView.setImageResource(R.drawable.moon);
        } else {
            imageView.setImageResource(R.drawable.sunnewpng);
        }
    }
}
