package dev.gtcl.eulerityproject.activities.editor;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.DialogFragment;

import dev.gtcl.eulerityproject.R;

public class ColorPickerFragmentDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener{

    private ColorPickListener colorPickListener;

    public void setColorPickListener(ColorPickListener colorPickListener){
        this.colorPickListener = colorPickListener;
    }

    private View colorView;
    private TextView redLabel;
    private TextView greenLabel;
    private TextView blueLabel;
    private SeekBar sbRed;
    private SeekBar sbGreen;
    private SeekBar sbBlue;

    private int red;
    private int green;
    private int blue;

    public void setRGB(int red, int green, int blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_color_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        colorView = view.findViewById(R.id.color_picker_color);
        redLabel = view.findViewById(R.id.color_picker_red_label);
        greenLabel = view.findViewById(R.id.color_picker_green_label);
        blueLabel = view.findViewById(R.id.color_picker_blue_label);
        sbRed = view.findViewById(R.id.color_picker_seekbar_red);
        sbGreen = view.findViewById(R.id.color_picker_seekbar_green);
        sbBlue = view.findViewById(R.id.color_picker_seekbar_blue);
        final Button cancel = view.findViewById(R.id.color_picker_cancel);
        final Button done = view.findViewById(R.id.color_picker_done);

        setColorIndicator(red, green, blue);
        sbRed.setProgress(red);
        sbGreen.setProgress(green);
        sbBlue.setProgress(blue);
        setLabel(sbRed, red);
        setLabel(sbGreen, green);
        setLabel(sbBlue, blue);
        sbRed.setOnSeekBarChangeListener(this);
        sbGreen.setOnSeekBarChangeListener(this);
        sbBlue.setOnSeekBarChangeListener(this);

        cancel.setOnClickListener((v) -> dismiss());
        done.setOnClickListener((v) -> {
            if(colorPickListener != null){
                red = sbRed.getProgress();
                green = sbGreen.getProgress();
                blue = sbBlue.getProgress();
                colorPickListener.onColorSelected(red, green, blue);
            }
            dismiss();
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        setLabel(seekBar, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }

    @SuppressLint("NonConstantResourceId")
    private void setLabel(SeekBar seekBar, int value){
        switch(seekBar.getId()){
            case R.id.color_picker_seekbar_red:
                String redLabelText = String.format(getString(R.string.red_label), value);
                redLabel.setText(redLabelText);
                break;
            case R.id.color_picker_seekbar_green:
                String greenLabelText = String.format(getString(R.string.green_label), value);
                greenLabel.setText(greenLabelText);
                break;
            case R.id.color_picker_seekbar_blue:
                String blueLabelText = String.format(getString(R.string.blue_label), value);
                blueLabel.setText(blueLabelText);
                break;
        }
        setColorIndicator(sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress());
    }

    private void setColorIndicator(int red, int green, int blue){
        colorView.setBackgroundColor(Color.rgb(red, green, blue));
    }

    public interface ColorPickListener{
        void onColorSelected(int r, int g, int b);
    }
}
