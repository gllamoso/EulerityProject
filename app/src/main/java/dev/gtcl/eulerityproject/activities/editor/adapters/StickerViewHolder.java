package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;


import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.StickerListener;

public class StickerViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;

    private StickerViewHolder(View view) {
        super(view);
        imageView = view.findViewById(R.id.sticker_image);
    }

    public void bind(@DrawableRes int imgRes, StickerListener stickerListener){
        imageView.setImageResource(imgRes);
        imageView.setOnClickListener(v -> stickerListener.onStickerSelected(imgRes));
    }

    public static StickerViewHolder create(ViewGroup viewGroup){
        return new StickerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sticker, viewGroup, false));
    }
}
