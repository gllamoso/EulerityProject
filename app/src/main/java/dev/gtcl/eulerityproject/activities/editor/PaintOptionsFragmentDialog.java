package dev.gtcl.eulerityproject.activities.editor;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.listeners.PaintOptionsListener;

public class PaintOptionsFragmentDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener, ColorPickerFragmentDialog.ColorPickListener {

    private static final int MIN_BRUSH_SIZE = 12;

    private TextView textView;
    private View colorIndicator;

    private ColorPickerFragmentDialog colorPickerFragmentDialog;

    private PaintOptionsListener paintOptionsListener;

    private int color = Color.BLACK;

    public void setPaintOptionsListener(PaintOptionsListener paintOptionsListener){
        this.paintOptionsListener = paintOptionsListener;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_paint_options, container);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        colorPickerFragmentDialog = new ColorPickerFragmentDialog();
        colorPickerFragmentDialog.setColorPickListener(this);
        colorPickerFragmentDialog.setRGB(Color.red(color), Color.green(color), Color.blue(color));

        textView = view.findViewById(R.id.paint_options_size_label);
        final SeekBar sb = view.findViewById(R.id.paint_options_seekbar);
        final Button done = view.findViewById(R.id.paint_options_done);
        final Button cancel = view.findViewById(R.id.paint_options_cancel);
        colorIndicator = view.findViewById(R.id.paint_options_color_indicator);

        sb.setOnSeekBarChangeListener(this);
        setTextSizeLabel(sb.getProgress() + MIN_BRUSH_SIZE);
        colorIndicator.setBackgroundColor(color);

        done.setOnClickListener((v) -> {
            dismiss();
            paintOptionsListener.onPaintOptionsSelected(color, sb.getProgress() + MIN_BRUSH_SIZE);
        });

        cancel.setOnClickListener((v) -> dismiss());

        colorIndicator.setOnClickListener((v) -> colorPickerFragmentDialog.show(getChildFragmentManager(), null));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        setTextSizeLabel(progress + MIN_BRUSH_SIZE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }

    private void setTextSizeLabel(int textSize){
        String text = String.format(getString(R.string.size), textSize);
        textView.setText(text);
    }

    @Override
    public void onColorSelected(int r, int g, int b) {
        color = Color.rgb(r,g,b);
        colorIndicator.setBackgroundColor(color);
    }
}
