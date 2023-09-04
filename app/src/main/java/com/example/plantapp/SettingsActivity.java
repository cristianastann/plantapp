package com.example.plantapp;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    String[] fontSizeOptions = {"Small", "Medium", "Large"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // For Dark Mode
        SwitchCompat darkModeSwitch = findViewById(R.id.dark_mode_switch);
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
            } else if (!isChecked && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            }

        });

        // For Adjustable Font Sizes
        Spinner fontSizeSpinner = findViewById(R.id.font_size_spinner);

        ArrayAdapter<String> fontSizeAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, android.R.id.text1, fontSizeOptions);
        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSizeSpinner.setAdapter(fontSizeAdapter);

        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFontSize = fontSizeOptions[position];
                applyFontSizeToUI(selectedFontSize);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void applyFontSizeToUI(String fontSize) {
        Log.d("Font Change", "Changing to " + fontSize);
        TextView smallTextView = findViewById(R.id.small_text_view);
        TextView mediumTextView = findViewById(R.id.medium_text_view);
        TextView largeTextView = findViewById(R.id.large_text_view);

        float textSize = convertFontSizeToSP(fontSize);
        smallTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        mediumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
        largeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);

    }

    private float convertFontSizeToSP(String fontSize) {
        if (fontSize.equals("Small")) {
            return 12f;
        } else if (fontSize.equals("Medium")) {
            return 16f;
        } else if (fontSize.equals("Large")) {
            return 20f;
        }
        return 16f; // Default font size
    }
}
