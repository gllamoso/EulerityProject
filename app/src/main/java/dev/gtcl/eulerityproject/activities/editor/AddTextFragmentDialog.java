package dev.gtcl.eulerityproject.activities.editor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import dev.gtcl.eulerityproject.R;

public class AddTextFragmentDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener, ColorPickerFragmentDialog.ColorPickListener {

    private static final String TAG = AddTextFragmentDialog.class.getSimpleName();

    private static final int MIN_TEXT_SIZE = 60;

    private View colorIndicator;
    private EditText editText;
    private TextView textView;
    private TextEditor textEditor;

    private ColorPickerFragmentDialog colorPickerFragmentDialog;

    private int color;

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
        return inflater.inflate(R.layout.fragment_dialog_add_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        colorPickerFragmentDialog = new ColorPickerFragmentDialog();
        colorPickerFragmentDialog.setColorPickListener(this);

        editText = view.findViewById(R.id.add_text_edit_text);
        textView = view.findViewById(R.id.add_text_size_label);
        final SeekBar sb = view.findViewById(R.id.add_text_seekbar);
        final Button done = view.findViewById(R.id.add_text_done);
        final Button cancel = view.findViewById(R.id.add_text_cancel);
        colorIndicator = view.findViewById(R.id.add_text_color_indicator);

        sb.setOnSeekBarChangeListener(this);
        setTextSizeLabel(sb.getProgress() + MIN_TEXT_SIZE);
        color = ContextCompat.getColor(getContext(), R.color.black);
        showKeyboard();

        done.setOnClickListener((v) -> {
            dismiss();
            String inputText = editText.getText().toString();
            editText.setText("");
            if(!TextUtils.isEmpty(inputText) && textEditor != null){
                textEditor.onDone(inputText, color, sb.getProgress() + MIN_TEXT_SIZE);
            }
        });

        cancel.setOnClickListener((v) -> {
            editText.setText("");
            dismiss();
        });

        colorIndicator.setOnClickListener((v) -> {
            hideKeyboard();
            colorPickerFragmentDialog.show(getChildFragmentManager(), null);
        });
    }

    private void showKeyboard(){
        if(editText.requestFocus()){
            editText.postDelayed(() -> {
                ((InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }, 300);
            editText.setSelection(editText.getText().length());
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setOnTextEditorListener(TextEditor textEditor) {
        this.textEditor = textEditor;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        setTextSizeLabel(progress + MIN_TEXT_SIZE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }

    private void setTextSizeLabel(int textSize){
        String text = String.format(getString(R.string.text_size), textSize);
        textView.setText(text);
    }

    @Override
    public void onColorPicked(int r, int g, int b) {
        color = Color.rgb(r,g,b);
        colorIndicator.setBackgroundColor(color);
    }

    public interface TextEditor{
        void onDone(String inputText, int colorCode, int textSize);
    }
}
