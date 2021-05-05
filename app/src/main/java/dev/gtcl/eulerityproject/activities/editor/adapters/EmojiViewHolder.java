package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.EmojiListener;

public class EmojiViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public EmojiViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.emoji_text);
    }

    public void bind(String emoji, EmojiListener emojiListener){
        textView.setText(emoji);
        textView.setOnClickListener(v -> emojiListener.onEmojiSelected(emoji));
    }

    public static EmojiViewHolder create(ViewGroup viewGroup){
        return new EmojiViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_emoji, viewGroup, false));
    }
}
