package dev.gtcl.eulerityproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.models.ImageURL;
import dev.gtcl.eulerityproject.service.EulerityAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    public static final String IMAGES_KEY = "images";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView textView = findViewById(R.id.splash_text);

        EulerityAPI.create().getImages().enqueue(new Callback<List<ImageURL>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<ImageURL>> call, Response<List<ImageURL>> response) {
                if(response.isSuccessful()){
                    Intent intent = new Intent(SplashActivity.this, ImageListActivity.class);
                    intent.putExtra(IMAGES_KEY, (Serializable) response.body());
                    startActivity(intent);
                } else {
                    textView.setText("Code: " + response.code() + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<ImageURL>> call, Throwable t) {
                textView.setText(t.getLocalizedMessage());
            }
        });
    }
}
