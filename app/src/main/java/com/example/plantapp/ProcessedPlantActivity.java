package com.example.plantapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProcessedPlantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processed_plant);

        String identifiedPlant = getIntent().getStringExtra("identifiedPlant");

        fetchPlantInfo(identifiedPlant);
    }
    private void fetchPlantInfo(String plantName) {
        OkHttpClient client = new OkHttpClient();

        // Build the request
        Request request = new Request.Builder()
                .url("https://your-api-endpoint.com/plants/" + plantName)
                .build();

        // Asynchronous call to get plant info
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Assuming you have a TextView with id 'plant_info' in your XML
                            TextView plantInfoTextView = findViewById(R.id.plant_info);
                            plantInfoTextView.setText(responseBody);
                        }
                    });
                }
            }
        });
}
}
