package dev.gtcl.eulerityproject.activities.main;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import dev.gtcl.eulerityproject.models.ImageURL;

public class ImageAdapter extends ListAdapter<ImageURL, ImageViewHolder> {

    private final ItemClickListener itemClickListener;

    public ImageAdapter(ItemClickListener itemClickListener) {
        super(ImageAdapter.DIFF_CALLBACK);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ImageViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(getItem(position), itemClickListener);
    }

    private static final DiffUtil.ItemCallback<ImageURL> DIFF_CALLBACK = new DiffUtil.ItemCallback<ImageURL>() {
        @Override
        public boolean areItemsTheSame(@NonNull ImageURL oldItem, @NonNull ImageURL newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ImageURL oldItem, @NonNull ImageURL newItem) {
            return oldItem.equals(newItem);
        }
    };
}
