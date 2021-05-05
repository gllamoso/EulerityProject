package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.FilterListener;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView textView;

    private FilterViewHolder(View view) {
        super(view);
        imageView = view.findViewById(R.id.filter_image);
        textView = view.findViewById(R.id.filter_text);
    }

    public void bind(String fileName, PhotoFilter filter, FilterListener filterListener){
        loadImage(fileName);
        textView.setText(filter.name().replace("_", " "));
        imageView.setOnClickListener(v -> filterListener.onFilterSelected(filter));
    }

    private void loadImage(String fileName){
        AssetManager assetManager = imageView.getContext().getAssets();
        try {
            InputStream istr = assetManager.open(fileName);
            imageView.setImageBitmap(BitmapFactory.decodeStream(istr));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FilterViewHolder create(ViewGroup viewGroup){
        return new FilterViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filter, viewGroup, false));
    }
}
