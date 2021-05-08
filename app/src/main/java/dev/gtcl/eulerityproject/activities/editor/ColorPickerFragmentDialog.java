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

        setColor();
        setLabel(sbRed, 0);
        setLabel(sbGreen, 0);
        setLabel(sbBlue, 0);
        sbRed.setOnSeekBarChangeListener(this);
        sbGreen.setOnSeekBarChangeListener(this);
        sbBlue.setOnSeekBarChangeListener(this);

        cancel.setOnClickListener((v) -> dismiss());
        done.setOnClickListener((v) -> {
            if(colorPickListener != null){
                colorPickListener.onColorPicked(sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress());
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
        setColor();
    }

    private void setColor(){
        colorView.setBackgroundColor(Color.rgb(sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress()));
    }

    public interface ColorPickListener{
        void onColorPicked(int r, int g, int b);
    }
}
