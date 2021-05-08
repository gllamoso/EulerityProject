package dev.gtcl.eulerityproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import dev.gtcl.eulerityproject.BitmapFilter;
import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.AddTextFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.ColorPickerFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.ValuePickerFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.adapters.FiltersAdapter;
import dev.gtcl.eulerityproject.activities.editor.listeners.EditorToolListener;
import dev.gtcl.eulerityproject.activities.editor.listeners.FilterListener;
import dev.gtcl.eulerityproject.activities.editor.FilterType;
import dev.gtcl.eulerityproject.models.Status;
import dev.gtcl.eulerityproject.models.UploadURL;
import dev.gtcl.eulerityproject.service.EulerityAPI;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class EditorActivity extends AppCompatActivity implements EditorToolListener, FilterListener, AddTextFragmentDialog.TextEditor, ColorPickerFragmentDialog.ColorPickListener {

    private static final String TAG = EditorActivity.class.getSimpleName();
    private static final String APP_ID = "gerardllamoso@gmail.com";
    private static final int WRITE_STORAGE = 52;

    private String originalUrl;
    private final EulerityAPI api = EulerityAPI.create();
    private File imageFile;
    private Bitmap originalBitmap;
    private Bitmap currentBitmap;

    // Views
    private ConstraintLayout rootView;
    private ImageView imageView;
    private RecyclerView editingToolsRecyclerVIew;
    private ProgressBar progressBar;
    private TextView textView;

    // Fragments
    private AddTextFragmentDialog addTextFragmentDialog;
    private ColorPickerFragmentDialog colorPickerFragmentDialog;
    private ValuePickerFragmentDialog valuePickerFragmentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        rootView = findViewById(R.id.editor_root_view);
        imageView = findViewById(R.id.editor_image);

        originalUrl = getIntent().getStringExtra(ImageListActivity.URL_KEY);
        Glide.with(this)
                .asBitmap()
                .load(originalUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        originalBitmap = resource;
                        currentBitmap = Bitmap.createBitmap(resource);
                        imageView.setImageBitmap(originalBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });

        editingToolsRecyclerVIew = findViewById(R.id.editor_tools_rv);
        editingToolsRecyclerVIew.setAdapter(new FiltersAdapter(this));

        Toolbar toolbar = findViewById(R.id.editor_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.menu_item_upload){
                upload();
            } else {
                currentBitmap = Bitmap.createBitmap(originalBitmap);
                imageView.setImageBitmap(currentBitmap);
            }
            return true;
        });
        toolbar.setNavigationOnClickListener((v) -> super.onBackPressed());

        progressBar = findViewById(R.id.editor_progress_bar);
        textView = findViewById(R.id.editor_text);

        // Initialize Fragments
        addTextFragmentDialog = new AddTextFragmentDialog();
        addTextFragmentDialog.setOnTextEditorListener(this);
        colorPickerFragmentDialog = new ColorPickerFragmentDialog();
        colorPickerFragmentDialog.setColorPickListener(this);
        valuePickerFragmentDialog = new ValuePickerFragmentDialog();
    }

    private void showText(String text){
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar(){
        textView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteImageFile();
    }

    private void deleteImageFile(){
        if(imageFile != null && imageFile.exists()){
            imageFile.delete();
            imageFile = null;
        }
    }

    @Override
    public void onItemSelected(FilterType filterType) {
        if(imageView.getVisibility() == View.GONE){
            return;
        }

        switch (filterType) {
            case TEXT:
                showDialogFragment(addTextFragmentDialog);
                break;
            case GRAYSCALE:
                (new AsyncFilterApplication(BitmapFilter::applyGrayscale)).execute();
                break;
            case VIGNETTE:
                (new AsyncFilterApplication(BitmapFilter::applyVignette)).execute();
                break;
            case SEPIA:
                (new AsyncFilterApplication(BitmapFilter::applySepia)).execute();
                break;
            case INVERT:
                (new AsyncFilterApplication(BitmapFilter::applyColorInversion)).execute();
                break;
            case BRIGHTNESS:
                valuePickerFragmentDialog.setMinMax(getString(R.string.brightness_value_label), -255, 255, (val) -> {
                    (new AsyncFilterApplication((b) -> BitmapFilter.applyBrightness(b, val))).execute();
                });
                showDialogFragment(valuePickerFragmentDialog);
                break;
            case CONTRAST:
                valuePickerFragmentDialog.setMinMax(getString(R.string.contrast_value_label), -100, 100, (val) -> {
                    (new AsyncFilterApplication((b) -> BitmapFilter.applyContrast(b, val))).execute();
                });
                showDialogFragment(valuePickerFragmentDialog);
                break;
            case SATURATION:
                valuePickerFragmentDialog.setMinMax(getString(R.string.saturation_value_label),  0, 200, (val) -> {
                    (new AsyncFilterApplication((b) -> BitmapFilter.applySaturation(b, val))).execute();
                });
                showDialogFragment(valuePickerFragmentDialog);
                break;
            case HUE:
                valuePickerFragmentDialog.setMinMax(getString(R.string.hue_value_label),  0, 360, (val) -> {
                    (new AsyncFilterApplication((b) -> BitmapFilter.applyHue(b, val))).execute();
                });
                showDialogFragment(valuePickerFragmentDialog);
                break;
            case TINT:
                showDialogFragment(colorPickerFragmentDialog);
                break;
            case ROTATE_LEFT:
                (new AsyncFilterApplication((b) -> BitmapFilter.applyRotation(b, -90))).execute();
                break;
            case ROTATE_RIGHT:
                (new AsyncFilterApplication((b) -> BitmapFilter.applyRotation(b, 90))).execute();
                break;
            case FLIP_HORIZONTAL:
                (new AsyncFilterApplication((b) -> BitmapFilter.applyFlip(b, true, false))).execute();
                break;
            case FLIP_VERTICAL:
                (new AsyncFilterApplication((b) -> BitmapFilter.applyFlip(b, false, true))).execute();
                break;
        }
    }

    private void upload() {
        if(imageView.getVisibility() == View.GONE){
            return;
        }

        if (hasPermissionToWrite()) {
            showProgressBar();
            api.getUploadUrl().enqueue(getUploadURLCallback);
        } else {
            String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            ActivityCompat.requestPermissions(this, new String[]{permission}, WRITE_STORAGE);
        }
    }

    private boolean hasPermissionToWrite(){
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED)
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q);
    }

    private final Callback<UploadURL> getUploadURLCallback = new Callback<UploadURL>() {
        @SuppressLint("MissingPermission")
        @Override
        public void onResponse(Call<UploadURL> call, Response<UploadURL> response) {
            if (response.isSuccessful()) {
                String uploadUrl = response.body().getUrl();
                File directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                try {
                    imageFile = File.createTempFile("temp", ".jpeg", directory);
                    FileOutputStream fOut = new FileOutputStream(imageFile);
                    currentBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);

                    Map<String, RequestBody> map = new HashMap<>();
                    map.put("appid", toRequestBody(APP_ID));
                    map.put("original", toRequestBody(originalUrl));
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
                    map.put("file\"; filename=\"" + imageFile.getName(), fileBody);
                    api.uploadImage(uploadUrl, map).enqueue(uploadImageCallback);
                } catch (Exception e) {
                    showText(e.toString());
                }
            } else {
                showText("Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<UploadURL> call, Throwable t) {
            showText(t.toString());
        }
    };

    private final Callback<Status> uploadImageCallback = new Callback<Status>() {
        @Override
        public void onResponse(Call<Status> call, Response<Status> response) {
            if(response.isSuccessful()){
                showText(getString(R.string.upload_successful));
                textView.append("\n");
                textView.append("APP ID: " + APP_ID);
            } else {
                showText("Unsuccessful upload response: " + response.message());
            }
            deleteImageFile();
        }

        @Override
        public void onFailure(Call<Status> call, Throwable t) {
            showText(t.getLocalizedMessage());
            deleteImageFile();
        }
    };

    private static RequestBody toRequestBody(String value){
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private void showDialogFragment(DialogFragment fragment){
        if(fragment == null || fragment.isAdded()){
            return;
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onDone(String inputText, int colorCode, int textSize) {
        (new AsyncFilterApplication((b) -> BitmapFilter.addText(EditorActivity.this, b, inputText, colorCode, textSize))).execute();
    }

    @Override
    public void onColorPicked(int r, int g, int b) {
        (new AsyncFilterApplication((bitmap) -> BitmapFilter.applyTint(bitmap, Color.rgb(r,g,b)))).execute();
    }

    private class AsyncFilterApplication extends AsyncTask<Void, Void, Void>{

        private final Function<Bitmap, Bitmap> filterFunction;

        private AsyncFilterApplication(Function<Bitmap, Bitmap> filterFunction){
            this.filterFunction = filterFunction;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            currentBitmap = filterFunction.apply(currentBitmap);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(currentBitmap);
            progressBar.setVisibility(View.GONE);
        }

    }

}