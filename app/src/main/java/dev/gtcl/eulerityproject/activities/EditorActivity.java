package dev.gtcl.eulerityproject.activities;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.EditTextFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.EmojiFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.StickerFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.adapters.EditorToolsAdapter;
import dev.gtcl.eulerityproject.activities.editor.adapters.FilterViewAdapter;
import dev.gtcl.eulerityproject.activities.editor.listeners.EditorToolListener;
import dev.gtcl.eulerityproject.activities.editor.listeners.EmojiListener;
import dev.gtcl.eulerityproject.activities.editor.listeners.FilterListener;
import dev.gtcl.eulerityproject.activities.editor.ToolType;
import dev.gtcl.eulerityproject.activities.editor.PropertiesFragmentDialog;
import dev.gtcl.eulerityproject.activities.editor.listeners.StickerListener;
import dev.gtcl.eulerityproject.models.Status;
import dev.gtcl.eulerityproject.models.UploadURL;
import dev.gtcl.eulerityproject.service.EulerityAPI;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class EditorActivity extends AppCompatActivity implements EditorToolListener, FilterListener, OnPhotoEditorListener, PropertiesFragmentDialog.PropertiesChangeListener, EmojiListener, StickerListener {

    private enum State{
        DRAWING,
        ERASING,
        NONE
    }

    private static final String TAG = EditorActivity.class.getSimpleName();
    private static final String APP_ID = "gerardllamoso@gmail.com";
    private static final int WRITE_STORAGE = 52;

    private String originalUrl;
    private final EulerityAPI api = EulerityAPI.create();
    private boolean areFiltersVisible;
    private final ConstraintSet constraintSet = new ConstraintSet();
    private File imageFile;

    // Views
    private ConstraintLayout rootView;
    private PhotoEditor photoEditor;
    private PhotoEditorView photoEditorView;
    private RecyclerView filtersRecyclerView;
    private RecyclerView editingToolsRecyclerVIew;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView textView;

    // Fragments
    private PropertiesFragmentDialog propertiesFragment;
    private EmojiFragmentDialog emojiFragmentDialog;
    private StickerFragmentDialog stickerFragmentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Set Fragments
        propertiesFragment = new PropertiesFragmentDialog();
        propertiesFragment.setPropertiesChangeListener(this);
        emojiFragmentDialog = new EmojiFragmentDialog();
        emojiFragmentDialog.setEmojiListener(this);
        stickerFragmentDialog = new StickerFragmentDialog();
        stickerFragmentDialog.setStickerListener(this);

        rootView = findViewById(R.id.editor_root_view);
        photoEditorView = findViewById(R.id.photo_editor_view);
        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .build();
        photoEditor.setOnPhotoEditorListener(this);

        originalUrl = getIntent().getStringExtra(ImageListActivity.URL_KEY);
        Glide.with(this)
                .asBitmap()
                .load(originalUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        photoEditorView.getSource().setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        editingToolsRecyclerVIew = findViewById(R.id.editor_tools_rv);
        editingToolsRecyclerVIew.setAdapter(new EditorToolsAdapter(this));

        filtersRecyclerView = findViewById(R.id.filters_rv);
        filtersRecyclerView.setAdapter(new FilterViewAdapter(this));

        toolbar = findViewById(R.id.editor_toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            upload();
            return true;
        });
        toolbar.setNavigationOnClickListener((v) -> super.onBackPressed());

        progressBar = findViewById(R.id.editor_progress_bar);
        textView = findViewById(R.id.editor_text);
    }

    private void showText(String text){
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
        photoEditorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar(){
        textView.setVisibility(View.GONE);
        photoEditorView.setVisibility(View.GONE);
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
    public void onBackPressed() {
        if (areFiltersVisible) {
            showFilters(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemSelected(ToolType toolType) {
        if(photoEditorView.getVisibility() == View.GONE){
            return;
        }

        switch (toolType) {
            case UNDO:
                photoEditor.undo();
                break;
            case REDO:
                photoEditor.redo();
                break;
            case BRUSH:
                photoEditor.setBrushDrawingMode(true);
                setState(State.DRAWING);
                showBottomSheetDialogFragment(propertiesFragment);
                break;
            case TEXT:
                EditTextFragmentDialog.show(this, (inputText, colorCode) -> {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);
                    photoEditor.addText(inputText, styleBuilder);
                    setState(State.NONE);
                });
                break;
            case ERASER:
                setState(State.ERASING);
                photoEditor.brushEraser();
                break;
            case FILTER:
                showFilters(true);
                break;
            case EMOJI:
                showBottomSheetDialogFragment(emojiFragmentDialog);
                break;
            case STICKER:
                showBottomSheetDialogFragment(stickerFragmentDialog);
                break;
            case CLEAR:
                photoEditor.clearAllViews();
                photoEditor.setFilterEffect(PhotoFilter.NONE);
                break;
        }
    }

    private void showFilters(boolean show) {
        areFiltersVisible = show;
        constraintSet.clone(rootView);

        if (show) {
            constraintSet.clear(filtersRecyclerView.getId(), ConstraintSet.START);
            constraintSet.connect(filtersRecyclerView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(filtersRecyclerView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            constraintSet.connect(filtersRecyclerView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.clear(filtersRecyclerView.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(300);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0F));
        TransitionManager.beginDelayedTransition(rootView, changeBounds);
        constraintSet.applyTo(rootView);
    }

    @Override
    public void onFilterSelected(PhotoFilter filter) {
        photoEditor.setFilterEffect(filter);
        showFilters(false);
    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {
        EditTextFragmentDialog.show(this, text, colorCode, (str, col) -> {
            final TextStyleBuilder styleBuilder = new TextStyleBuilder();
            styleBuilder.withTextColor(col);
            photoEditor.editText(rootView, str, styleBuilder);
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int i) { }

    @Override
    public void onRemoveViewListener(ViewType viewType, int i) { }

    @Override
    public void onStartViewChangeListener(ViewType viewType) { }

    @Override
    public void onStopViewChangeListener(ViewType viewType) { }

    @Override
    public void onColorChanged(int colorCode) {
        photoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        photoEditor.setBrushSize(brushSize);
    }

    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
        emojiFragmentDialog.dismiss();
        setState(State.NONE);
    }

    @Override
    public void onStickerSelected(@DrawableRes int drawRes) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawRes);
        photoEditor.addImage(bitmap);
        stickerFragmentDialog.dismiss();
        setState(State.NONE);
    }

    private void setState(State state){
        switch(state){
            case NONE:
                toolbar.setTitle(R.string.no_tool_selected);
                break;
            case DRAWING:
                toolbar.setTitle(R.string.drawing);
                break;
            case ERASING:
                toolbar.setTitle(R.string.erasing);
                break;
        }
    }

    private void upload() {
        setState(State.NONE);
        if(photoEditorView.getVisibility() == View.GONE){
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
                photoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        File directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                        try {
                            imageFile = File.createTempFile("temp", ".jpeg", directory);
                            FileOutputStream fOut = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
                            bitmap.recycle();

                            Map<String, RequestBody> map = new HashMap<>();
                            map.put("appid", toRequestBody(APP_ID));
                            map.put("original", toRequestBody(originalUrl));
                            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
                            map.put("file\"; filename=\"" + imageFile.getName(), fileBody);
                            api.uploadImage(uploadUrl, map).enqueue(uploadImageCallback);
                        } catch (Exception e) {
                            showText(e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        showText(e.toString());
                    }
                });
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