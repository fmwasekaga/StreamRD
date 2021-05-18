package com.kagaconnect.streamrd.sliders;

import android.content.Context;
import com.kagaconnect.streamrd.devices.CameraView;

public abstract class CameraValueSlider extends ValueHorizontalSlider {

    public interface OnValueChangeListener{
        void onValueChange(String newValue, String oldValue);
    }

    protected CameraView camera;
    private OnValueChangeListener listener;

    public CameraValueSlider(Context context, CameraView camera) {
        super(context);
        this.camera = camera;
    }

    public CameraValueSlider(Context context, CameraView camera, String[] values) {
        super(context, values);
        this.camera = camera;
    }

    public void setOnValueChangeListener(OnValueChangeListener listener){
        this.listener = listener;
    }

    public abstract void applyToCamera(String value);

    @Override
    public void onValueChange(String newValue, String oldValue) {
        applyToCamera(newValue);
        if(this.listener != null)this.listener.onValueChange(newValue, oldValue);
    }
}