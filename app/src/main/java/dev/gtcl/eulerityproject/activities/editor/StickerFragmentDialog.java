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
import dev.gtcl.eulerityproject.activities.editor.adapters.StickerAdapter;
import dev.gtcl.eulerityproject.activities.editor.listeners.StickerListener;

public class StickerFragmentDialog extends BottomSheetDialogFragment {

    private StickerListener stickerListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.sticker_rv);
        recyclerView.setAdapter(new StickerAdapter(stickerListener));
    }

    public void setStickerListener(StickerListener stickerListener){
        this.stickerListener = stickerListener;
    }
}
