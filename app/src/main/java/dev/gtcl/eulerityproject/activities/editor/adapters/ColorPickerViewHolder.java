package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.ColorListener;

public class ColorPickerViewHolder extends RecyclerView.ViewHolder {
    private final View view;

    private ColorPickerViewHolder(View view){
        super(view);
        this.view = view.findViewById(R.id.color_picker_view);
    }

    public void bind(@ColorInt int color, ColorListener colorListener){
        view.setBackgroundColor(color);
        view.setOnClickListener((v -> colorListener.onColorSelected(color)));
    }

    public static ColorPickerViewHolder create(ViewGroup viewGroup){
        return new ColorPickerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color_picker, viewGroup, false));
    }
}
