package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import dev.gtcl.eulerityproject.activities.editor.listeners.FilterListener;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterViewAdapter extends ListAdapter<Pair<String, PhotoFilter>, FilterViewHolder> {
    private final FilterListener filterListener;

    public FilterViewAdapter(FilterListener filterListener){
        super(FilterViewAdapter.DIFF_CALLBACK);
        this.filterListener = filterListener;
        createList();
    }

    private void createList(){
        List<Pair<String, PhotoFilter>> list = new ArrayList<>();
        list.add(new Pair<>("filters/original.jpg", PhotoFilter.NONE));
        list.add(new Pair<>("filters/auto_fix.png", PhotoFilter.AUTO_FIX));
        list.add(new Pair<>("filters/brightness.png", PhotoFilter.BRIGHTNESS));
        list.add(new Pair<>("filters/contrast.png", PhotoFilter.CONTRAST));
        list.add(new Pair<>("filters/documentary.png", PhotoFilter.DOCUMENTARY));
        list.add(new Pair<>("filters/dual_tone.png", PhotoFilter.DUE_TONE));
        list.add(new Pair<>("filters/fill_light.png", PhotoFilter.FILL_LIGHT));
        list.add(new Pair<>("filters/fish_eye.png", PhotoFilter.FISH_EYE));
        list.add(new Pair<>("filters/grain.png", PhotoFilter.GRAIN));
        list.add(new Pair<>("filters/gray_scale.png", PhotoFilter.GRAY_SCALE));
        list.add(new Pair<>("filters/lomish.png", PhotoFilter.LOMISH));
        list.add(new Pair<>("filters/negative.png", PhotoFilter.NEGATIVE));
        list.add(new Pair<>("filters/posterize.png", PhotoFilter.POSTERIZE));
        list.add(new Pair<>("filters/saturate.png", PhotoFilter.SATURATE));
        list.add(new Pair<>("filters/sepia.png", PhotoFilter.SEPIA));
        list.add(new Pair<>("filters/sharpen.png", PhotoFilter.SHARPEN));
        list.add(new Pair<>("filters/temprature.png", PhotoFilter.TEMPERATURE));
        list.add(new Pair<>("filters/tint.png", PhotoFilter.TINT));
        list.add(new Pair<>("filters/vignette.png", PhotoFilter.VIGNETTE));
        list.add(new Pair<>("filters/cross_process.png", PhotoFilter.CROSS_PROCESS));
        list.add(new Pair<>("filters/b_n_w.png", PhotoFilter.BLACK_WHITE));
        list.add(new Pair<>("filters/flip_horizental.png", PhotoFilter.FLIP_HORIZONTAL));
        list.add(new Pair<>("filters/flip_vertical.png", PhotoFilter.FLIP_VERTICAL));
        list.add(new Pair<>("filters/rotate.png", PhotoFilter.ROTATE));
        submitList(list);
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return FilterViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        Pair<String, PhotoFilter> item = getItem(position);
        holder.bind(item.first, item.second, filterListener);
    }

    private static final DiffUtil.ItemCallback<Pair<String, PhotoFilter>> DIFF_CALLBACK = new DiffUtil.ItemCallback<Pair<String, PhotoFilter>>() {
        @Override
        public boolean areItemsTheSame(@NonNull Pair<String, PhotoFilter> oldItem, @NonNull Pair<String, PhotoFilter> newItem) {
            return oldItem.second == newItem.second;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Pair<String, PhotoFilter> oldItem, @NonNull Pair<String, PhotoFilter> newItem) {
            return oldItem.second == newItem.second;
        }
    };
}
