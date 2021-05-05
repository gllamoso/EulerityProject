package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import dev.gtcl.eulerityproject.activities.editor.listeners.EmojiListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;

public class EmojiAdapter extends ListAdapter<String, EmojiViewHolder> {

    private final EmojiListener emojiListener;

    public EmojiAdapter(Context context, EmojiListener emojiListener) {
        super(EmojiAdapter.DIFF_UTIL);
        this.emojiListener = emojiListener;
        submitList(PhotoEditor.getEmojis(context));
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return EmojiViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position) {
        holder.bind(getItem(position), emojiListener);
    }

    private static final DiffUtil.ItemCallback<String> DIFF_UTIL = new DiffUtil.ItemCallback<String>(){

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };
}
