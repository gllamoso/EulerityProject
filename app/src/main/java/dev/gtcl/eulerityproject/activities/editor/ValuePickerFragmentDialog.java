package dev.gtcl.eulerityproject.activities.editor;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import dev.gtcl.eulerityproject.R;

public class ValuePickerFragmentDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener{

    private TextView textView;
    private SeekBar seekBar;

    private String label;
    private int min = 0;
    private int max = 100;
    private ValuePickListener valuePickListener;

    public void setMinMax(String label, int min, int max, ValuePickListener valuePickListener){
        this.label = label;
        this.min = min;
        this.max = max;
        this.valuePickListener = valuePickListener;
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
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_value_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.value_picker_label);
        seekBar = view.findViewById(R.id.value_picker_seekbar);
        Button cancel = view.findViewById(R.id.value_picker_cancel);
        Button done = view.findViewById(R.id.value_picker_done);
        seekBar.setMax(max - min);
        seekBar.setOnSeekBarChangeListener(this);
        textView.setText(String.format(Locale.US, "%s: %d", label, min));

        cancel.setOnClickListener((v) -> dismiss());
        done.setOnClickListener((v) -> {
            if(valuePickListener != null){
                valuePickListener.onValuePicked(seekBar.getProgress() + min);
            }
            dismiss();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        seekBar.setProgress(0);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        textView.setText(String.format(Locale.US, "%s: %d", label, progress + min));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }

    public interface ValuePickListener{
        void onValuePicked(int value);
    }
}
