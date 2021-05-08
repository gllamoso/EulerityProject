package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.EditorToolListener;

public class FiltersViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView textView;
    private final ViewGroup background;

    private FiltersViewHolder(View view) {
        super(view);
        imageView = view.findViewById(R.id.tool_image);
        textView = view.findViewById(R.id.tool_text);
        background = view.findViewById(R.id.tool_background);
    }

    public void bind(FiltersAdapter.FilterItem filterItem, EditorToolListener editorToolListener){
        imageView.setImageResource(filterItem.imgRes);
        textView.setText(filterItem.textRes);
        background.setOnClickListener(view -> editorToolListener.onItemSelected(filterItem.filterType));
    }

    public static FiltersViewHolder create(ViewGroup viewGroup){
        return new FiltersViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filter, viewGroup, false));
    }
}
