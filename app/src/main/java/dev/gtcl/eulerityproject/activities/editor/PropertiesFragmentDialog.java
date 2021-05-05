package dev.gtcl.eulerityproject.activities.editor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import dev.gtcl.eulerityproject.R;
import dev.gtcl.eulerityproject.activities.editor.adapters.ColorPickerAdapter;


public class PropertiesFragmentDialog extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private PropertiesChangeListener propertiesChangeListener;

    public interface PropertiesChangeListener {
        void onColorChanged(@ColorInt int colorCode);
        void  onOpacityChanged(int opacity);
        void onBrushSizeChanged(int brushSize);
    }

    public void setPropertiesChangeListener(PropertiesChangeListener propertiesChangeListener){
        this.propertiesChangeListener = propertiesChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_properties, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.properties_color_rv);
        SeekBar opacitySeekBar = view.findViewById(R.id.properties_opacity_sb);
        SeekBar brushSizeSeekBar = view.findViewById(R.id.properties_brush_sb);

        opacitySeekBar.setOnSeekBarChangeListener(this);
        brushSizeSeekBar.setOnSeekBarChangeListener(this);
        recyclerView.setAdapter(new ColorPickerAdapter(getContext(), (colorCode -> {
            if(propertiesChangeListener != null){
                dismiss();
                propertiesChangeListener.onColorChanged(colorCode);
            }
        })));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch(seekBar.getId()){
            case R.id.properties_opacity_sb:
                if(propertiesChangeListener != null){
                    propertiesChangeListener.onOpacityChanged(i);
                }
                break;
            case R.id.properties_brush_sb:
                if(propertiesChangeListener != null){
                    propertiesChangeListener.onBrushSizeChanged(i);
                }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }


}
