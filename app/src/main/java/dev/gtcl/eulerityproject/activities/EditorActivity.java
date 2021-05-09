package dev.gtcl.eulerityproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import dev.gtcl.eulerityproject.BitmapFilter;
import dev.gtcl.eulerityproject.DrawableImageView;
import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.Util;
import dev.gtcl.eulerityproject.activities.editor.SetTextFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.ColorPickerFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.PaintOptionsFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.ValuePickerFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.adapters.FiltersAdapter;
import dev.gtcl.eulerityproject.activities.editor.listeners.EditorToolListener;
import dev.gtcl.eulerityproject.activities.editor.listeners.FilterListener;
import dev.gtcl.eulerityproject.activities.editor.FilterType;
import dev.gtcl.eulerityproject.activities.editor.listeners.PaintOptionsListener;
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

public class EditorActivity extends AppCompatActivity implements EditorToolListener, FilterListener, SetTextFragmentDialog.TextEditor, ColorPickerFragmentDialog.ColorPickListener, PaintOptionsListener {

    private static final String TAG = EditorActivity.class.getSimpleName();
    private static final String APP_ID = "gerardllamoso@gmail.com";
    private static final int WRITE_STORAGE = 52;

    private String originalUrl;
    private final EulerityAPI api = EulerityAPI.create();
    private File imageFile;
    private Bitmap originalBitmap;
    private Bitmap currentBitmap;

    // Views
    private DrawableImageView drawableImageView;
    private ProgressBar progressBar;
    private TextView textView;
    private Toolbar toolbar;

    // Fragments
    private SetTextFragmentDialog setTextFragmentDialog;
    private ColorPickerFragmentDialog colorPickerFragmentDialog;
    private ValuePickerFragmentDialog valuePickerFragmentDialog;
    private PaintOptionsFragmentDialog paintOptionsFragmentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        drawableImageView = findViewById(R.id.editor_image);

        originalUrl = getIntent().getStringExtra(ImageListActivity.URL_KEY);
        Glide.with(this)
                .asBitmap()
                .load(originalUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        originalBitmap = resource;
                        currentBitmap = resource.copy(resource.getConfig(), true);
                        drawableImageView.setImageBitmap(originalBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });

        RecyclerView editingToolsRecyclerVIew = findViewById(R.id.editor_tools_rv);
        editingToolsRecyclerVIew.setAdapter(new FiltersAdapter(this));

        toolbar = findViewById(R.id.editor_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.menu_item_upload && drawableImageView.getVisibility() == View.VISIBLE){
                toolbar.setTitle(R.string.apply_filters);
                upload();
            }
            return true;
        });
        toolbar.setNavigationOnClickListener((v) -> super.onBackPressed());

        progressBar = findViewById(R.id.editor_progress_bar);
        textView = findViewById(R.id.editor_text);

        // Initialize Fragments
        setTextFragmentDialog = new SetTextFragmentDialog();
        setTextFragmentDialog.setOnTextEditorListener(this);
        colorPickerFragmentDialog = new ColorPickerFragmentDialog();
        colorPickerFragmentDialog.setColorPickListener(this);
        valuePickerFragmentDialog = new ValuePickerFragmentDialog();
        paintOptionsFragmentDialog = new PaintOptionsFragmentDialog();
        paintOptionsFragmentDialog.setPaintOptionsListener(this);
    }

    private void showText(String text){
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        drawableImageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar(){
        textView.setVisibility(View.GONE);
        drawableImageView.setVisibility(View.GONE);
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
        if(drawableImageView.getVisibility() == View.GONE){
            return;
        }

        drawableImageView.stopPainting();
        toolbar.setTitle(R.string.apply_filters);
        switch (filterType) {
            case TEXT:
                Util.showDialogFragment(setTextFragmentDialog, getSupportFragmentManager());
                break;
            case PAINT:
                Util.showDialogFragment(paintOptionsFragmentDialog, getSupportFragmentManager());
                break;
            case ERASE:
                drawableImageView.eraseLastDrawing();
                break;
            case GRAYSCALE:
                (new AsyncFilterCreator(BitmapFilter::applyGrayscale)).execute();
                break;
            case VIGNETTE:
                (new AsyncFilterCreator(BitmapFilter::applyVignette)).execute();
                break;
            case INVERT:
                (new AsyncFilterCreator(BitmapFilter::applyColorInversion)).execute();
                break;
            case SATURATION:
                valuePickerFragmentDialog.setMinMax(getString(R.string.saturation_value_label),  0, 200, (val) -> {
                    (new AsyncFilterCreator((b) -> BitmapFilter.applySaturation(b, val))).execute();
                });
                Util.showDialogFragment(valuePickerFragmentDialog, getSupportFragmentManager());
                break;
            case TINT:
                Util.showDialogFragment(colorPickerFragmentDialog, getSupportFragmentManager());
                break;
            case ROTATE_LEFT:
                (new AsyncFilterCreator((b) -> BitmapFilter.applyRotation(b, -90))).execute();
                break;
            case ROTATE_RIGHT:
                (new AsyncFilterCreator((b) -> BitmapFilter.applyRotation(b, 90))).execute();
                break;
            case FLIP_HORIZONTAL:
                (new AsyncFilterCreator((b) -> BitmapFilter.applyFlip(b, false, true))).execute();
                break;
            case FLIP_VERTICAL:
                (new AsyncFilterCreator((b) -> BitmapFilter.applyFlip(b, true, false))).execute();
                break;
            case CLEAR_FILTERS:
                if(currentBitmap != null){
                    currentBitmap.recycle();
                }
                currentBitmap = Bitmap.createBitmap(originalBitmap);
                drawableImageView.setImageBitmap(currentBitmap);
                break;
            case CLEAR_ALL:
                if(currentBitmap != null){
                    currentBitmap.recycle();
                }
                currentBitmap = Bitmap.createBitmap(originalBitmap);
                drawableImageView.eraseAllDrawings();
                drawableImageView.clearText();
                drawableImageView.setImageBitmap(currentBitmap);
                break;

        }
    }

    @Override
    public void onTextSelected(String inputText, int colorCode, int textSize) {
        drawableImageView.setText(inputText, colorCode, textSize);
    }

    @Override
    public void onColorSelected(int r, int g, int b) {
        (new AsyncFilterCreator((bitmap) -> BitmapFilter.applyTint(bitmap, Color.rgb(r,g,b)))).execute();
    }

    @Override
    public void onPaintOptionsSelected(int color, int brushSize) {
        drawableImageView.startPainting(color, brushSize);
        toolbar.setTitle(R.string.drawing);
    }

    private class AsyncFilterCreator extends AsyncTask<Void, Void, Bitmap>{

        private final Function<Bitmap, Bitmap> filterFunction;

        private AsyncFilterCreator(Function<Bitmap, Bitmap> filterFunction){
            this.filterFunction = filterFunction;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            drawableImageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return filterFunction.apply(currentBitmap);
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            super.onPostExecute(bm);
            if(currentBitmap != null){
                currentBitmap.recycle();
            }
            currentBitmap = bm;
            drawableImageView.setVisibility(View.VISIBLE);
            drawableImageView.setImageBitmap(currentBitmap);
            progressBar.setVisibility(View.GONE);
        }

    }

    private void upload() {
        if(drawableImageView.getVisibility() == View.GONE){
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
                    drawableImageView.createBitmap().compress(Bitmap.CompressFormat.JPEG, 75, fOut);

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

}