package dev.gtcl.eulerityproject.activities.editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.adapters.EmojiAdapter;
import dev.gtcl.eulerityproject.activities.editor.listeners.EmojiListener;

public class EmojiFragmentDialog extends BottomSheetDialogFragment {

    private EmojiListener emojiListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_emojis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.emoji_rv);
        recyclerView.setAdapter(new EmojiAdapter(getContext(), emojiListener));
    }

    public void setEmojiListener(EmojiListener emojiListener){
        this.emojiListener = emojiListener;
    }
}
