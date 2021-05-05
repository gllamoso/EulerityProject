package dev.gtcl.eulerityproject.activities.editor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.EditorToolListener;

public class EditorToolsViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView textView;
    private final ViewGroup background;

    private EditorToolsViewHolder(View view) {
        super(view);
        imageView = view.findViewById(R.id.tool_image);
        textView = view.findViewById(R.id.tool_text);
        background = view.findViewById(R.id.tool_background);
    }

    public void bind(EditorToolsAdapter.ToolModel toolModel, EditorToolListener editorToolListener){
        imageView.setImageResource(toolModel.imgRes);
        textView.setText(toolModel.textRes);
        background.setOnClickListener(view -> editorToolListener.onItemSelected(toolModel.toolType));
    }

    public static EditorToolsViewHolder create(ViewGroup viewGroup){
        return new EditorToolsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_editing_tool, viewGroup, false));
    }
}
