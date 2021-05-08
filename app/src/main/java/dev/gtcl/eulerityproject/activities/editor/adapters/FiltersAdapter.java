package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.EditorToolListener;
import dev.gtcl.eulerityproject.activities.editor.FilterType;

public class FiltersAdapter extends ListAdapter<FiltersAdapter.FilterItem, FiltersViewHolder> {

    private final EditorToolListener editorToolListener;

    public FiltersAdapter(EditorToolListener editorToolListener) {
        super(FiltersAdapter.DIFF_CALLBACK);
        this.editorToolListener = editorToolListener;
        createList();
    }

    private void createList(){
        List<FilterItem> list = new ArrayList<>();
        list.add(new FilterItem(R.drawable.ic_text, R.string.filter_text, FilterType.TEXT));
        list.add(new FilterItem(R.drawable.ic_filter, R.string.filter_grayscale, FilterType.GRAYSCALE));
        list.add(new FilterItem(R.drawable.ic_filter, R.string.filter_vignette, FilterType.VIGNETTE));
        list.add(new FilterItem(R.drawable.ic_filter, R.string.filter_sepia, FilterType.SEPIA));
        list.add(new FilterItem(R.drawable.ic_filter, R.string.filter_invert, FilterType.INVERT));
        list.add(new FilterItem(R.drawable.ic_tune, R.string.filter_brightness, FilterType.BRIGHTNESS));
        list.add(new FilterItem(R.drawable.ic_tune, R.string.filter_contrast, FilterType.CONTRAST));
        list.add(new FilterItem(R.drawable.ic_tune, R.string.filter_saturation, FilterType.SATURATION));
        list.add(new FilterItem(R.drawable.ic_tune, R.string.filter_hue, FilterType.HUE));
        list.add(new FilterItem(R.drawable.ic_tune, R.string.filter_tint, FilterType.TINT));
        list.add(new FilterItem(R.drawable.ic_rotate_left, R.string.filter_rotate_left, FilterType.ROTATE_LEFT));
        list.add(new FilterItem(R.drawable.ic_rotate_right, R.string.filter_rotate_right, FilterType.ROTATE_RIGHT));
        list.add(new FilterItem(R.drawable.ic_filter, R.string.filter_flip_horizontal, FilterType.FLIP_HORIZONTAL));
        list.add(new FilterItem(R.drawable.ic_filter, R.string.filter_flip_vertical, FilterType.FLIP_VERTICAL));
        submitList(list);
    }

    @NonNull
    @Override
    public FiltersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return FiltersViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull FiltersViewHolder holder, int position) {
        holder.bind(getItem(position), editorToolListener);
    }

    private static final DiffUtil.ItemCallback<FilterItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<FilterItem>(){

        @Override
        public boolean areItemsTheSame(@NonNull FilterItem oldItem, @NonNull FilterItem newItem) {
            return oldItem.filterType == newItem.filterType;
        }

        @Override
        public boolean areContentsTheSame(@NonNull FilterItem oldItem, @NonNull FilterItem newItem) {
            return oldItem.imgRes == newItem.imgRes && oldItem.textRes == newItem.textRes;
        }
    };

    static class FilterItem {
        @DrawableRes int imgRes;
        @StringRes int textRes;
        FilterType filterType;
        FilterItem(@DrawableRes int imgRes, @StringRes int textRes, FilterType filterType){
            this.imgRes = imgRes;
            this.textRes = textRes;
            this.filterType = filterType;
        }
    }

}
