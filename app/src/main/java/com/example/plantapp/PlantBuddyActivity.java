package com.example.plantapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;


public class PlantBuddyActivity extends AppCompatActivity {

    private View greenOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("ThemeSettings", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("isDarkMode", false);

        if (isDarkMode) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_buddy);
        greenOverlay = findViewById(R.id.greenOverlay);
    }

    public void showOverlay() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        greenOverlay.startAnimation(fadeInAnimation);
        greenOverlay.setVisibility(View.VISIBLE);
    }

    public void hideOverlay() {
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                greenOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        greenOverlay.startAnimation(fadeOutAnimation);
    }
}
