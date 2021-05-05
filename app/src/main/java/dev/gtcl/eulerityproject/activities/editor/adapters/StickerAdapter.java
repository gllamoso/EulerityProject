package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.Arrays;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.StickerListener;

public class StickerAdapter extends ListAdapter<Integer, StickerViewHolder> {

    private final StickerListener stickerListener;

    public StickerAdapter(StickerListener stickerListener){
        super(StickerAdapter.DIFF_UTIL);
        this.stickerListener = stickerListener;
        submitList(Arrays.asList(R.drawable.hat_black, R.drawable.hat_pink));
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return StickerViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        holder.bind(getItem(position), stickerListener);
    }

    public static final DiffUtil.ItemCallback<Integer> DIFF_UTIL = new DiffUtil.ItemCallback<Integer>() {
        @Override
        public boolean areItemsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
            return oldItem.equals(newItem);
        }
    };
}
