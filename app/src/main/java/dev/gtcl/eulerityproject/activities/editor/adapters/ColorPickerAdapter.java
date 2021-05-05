package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.ColorListener;

public class ColorPickerAdapter extends ListAdapter<Integer, ColorPickerViewHolder> {
    private final ColorListener colorListener;

    public ColorPickerAdapter(Context context, ColorListener colorListener){
        super(ColorPickerAdapter.DIFF_CALLBACK);
        this.colorListener = colorListener;
        createList(context);
    }

    private void createList(Context context){
        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(context, R.color.blue_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.brown_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.green_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.orange_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.red_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.black));
        colors.add(ContextCompat.getColor(context, R.color.red_orange_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.sky_blue_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.violet_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.white));
        colors.add(ContextCompat.getColor(context, R.color.yellow_color_picker));
        colors.add(ContextCompat.getColor(context, R.color.yellow_green_color_picker));
        submitList(colors);
    }

    @NonNull
    @Override
    public ColorPickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ColorPickerViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorPickerViewHolder holder, int position) {
        holder.bind(getItem(position), colorListener);
    }

    private static final DiffUtil.ItemCallback<Integer> DIFF_CALLBACK = new DiffUtil.ItemCallback<Integer>() {
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
