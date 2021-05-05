package dev.gtcl.eulerityproject.activities.editor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.adapters.ColorPickerAdapter;

public class EditTextFragmentDialog extends DialogFragment {

    private static final String TAG = EditTextFragmentDialog.class.getSimpleName();

    private EditText editText;
    private TextView textView;
    private RecyclerView recyclerView;

    private TextEditor textEditor;

    public interface TextEditor{
        void onDone(String inputText, int colorCode);
    }

    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";

    private int colorCode;

    public static EditTextFragmentDialog show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText,
                                                @ColorInt int colorCode,
                                                TextEditor textEditor) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        EditTextFragmentDialog fragment = new EditTextFragmentDialog();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        fragment.setOnTextEditorListener(textEditor);
        return fragment;
    }

    public static EditTextFragmentDialog show(@NonNull AppCompatActivity appCompatActivity, TextEditor textEditor) {
        return show(appCompatActivity,
                "", ContextCompat.getColor(appCompatActivity, R.color.white), textEditor);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_edit_text, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editText = view.findViewById(R.id.text_editor_edit_text);
        textView = view.findViewById(R.id.text_editor_done_tv);
        recyclerView = view.findViewById(R.id.color_picker_rv);
        recyclerView.setAdapter(new ColorPickerAdapter(getContext(), colorCode -> {
            this.colorCode = colorCode;
            editText.setTextColor(colorCode);
        }));
        editText.setText(Objects.requireNonNull(getArguments()).getString(EXTRA_INPUT_TEXT));
        colorCode = getArguments().getInt(EXTRA_COLOR_CODE);
        editText.setTextColor(colorCode);

        textView.setOnClickListener(v -> {
            hideKeyboard();
            dismiss();
            String inputText = editText.getText().toString();
            if(!TextUtils.isEmpty(inputText) && textEditor != null){
                textEditor.onDone(inputText, colorCode);
            }
        });

        showKeyboard();

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
}
