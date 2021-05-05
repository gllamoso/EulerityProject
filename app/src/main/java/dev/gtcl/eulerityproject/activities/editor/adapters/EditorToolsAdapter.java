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
import dev.gtcl.eulerityproject.activities.editor.ToolType;

public class EditorToolsAdapter extends ListAdapter<EditorToolsAdapter.ToolModel, EditorToolsViewHolder> {

    private final EditorToolListener editorToolListener;

    public EditorToolsAdapter(EditorToolListener editorToolListener) {
        super(EditorToolsAdapter.DIFF_CALLBACK);
        this.editorToolListener = editorToolListener;
        createList();
    }

    private void createList(){
        List<ToolModel> list = new ArrayList<>();
        list.add(new ToolModel(R.drawable.ic_undo, R.string.label_undo, ToolType.UNDO));
        list.add(new ToolModel(R.drawable.ic_redo, R.string.label_redo, ToolType.REDO));
        list.add(new ToolModel(R.drawable.ic_brush, R.string.label_brush, ToolType.BRUSH));
        list.add(new ToolModel(R.drawable.ic_text, R.string.label_text, ToolType.TEXT));
        list.add(new ToolModel(R.drawable.ic_eraser, R.string.label_eraser, ToolType.ERASER));
        list.add(new ToolModel(R.drawable.ic_filter, R.string.label_filter, ToolType.FILTER));
        list.add(new ToolModel(R.drawable.ic_emoji, R.string.label_emoji, ToolType.EMOJI));
        list.add(new ToolModel(R.drawable.ic_photo, R.string.label_sticker, ToolType.STICKER));
        list.add(new ToolModel(R.drawable.ic_clear, R.string.label_clear, ToolType.CLEAR));
        submitList(list);
    }

    @NonNull
    @Override
    public EditorToolsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return EditorToolsViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull EditorToolsViewHolder holder, int position) {
        holder.bind(getItem(position), editorToolListener);
    }

    private static final DiffUtil.ItemCallback<ToolModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ToolModel>(){

        @Override
        public boolean areItemsTheSame(@NonNull ToolModel oldItem, @NonNull ToolModel newItem) {
            return oldItem.toolType == newItem.toolType;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ToolModel oldItem, @NonNull ToolModel newItem) {
            return oldItem.imgRes == newItem.imgRes && oldItem.textRes == newItem.textRes;
        }
    };

    static class ToolModel{
        @DrawableRes int imgRes;
        @StringRes int textRes;
        ToolType toolType;
        ToolModel(@DrawableRes int imgRes, @StringRes int textRes, ToolType toolType){
            this.imgRes = imgRes;
            this.textRes = textRes;
            this.toolType = toolType;
        }
    }

}
