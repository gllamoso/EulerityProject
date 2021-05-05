package dev.gtcl.eulerityproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.main.ImageAdapter;
import dev.gtcl.eulerityproject.activities.main.ItemClickListener;
import dev.gtcl.eulerityproject.models.ImageURL;

public class ImageListActivity extends AppCompatActivity implements ItemClickListener {

    public static final String URL_KEY = "URL";
    private static final String TAG = ImageListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        RecyclerView recyclerView = findViewById(R.id.thumbnail_rv);
        ImageAdapter adapter = new ImageAdapter(this);
        List<ImageURL> list = (List<ImageURL>) getIntent().getSerializableExtra(SplashActivity.IMAGES_KEY);
        adapter.submitList(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(String url) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(URL_KEY, url);
        startActivity(intent);
    }
}