package com.kagaconnect.streamrd.sliders;

import android.content.Context;
import android.hardware.camera2.params.RggbChannelVector;

import com.kagaconnect.streamrd.devices.CameraView;
import com.kagaconnect.streamrd.helpers.ExposureTimeConverter;
import com.kagaconnect.streamrd.helpers.Mode;

public class ExposureTimeSlider extends CameraStringSlider {

    private static final float maximumPreviewExposureTime = 0.15f;
    private static final float maximumCaptureExposureTime = 30;

    public ExposureTimeSlider(Context context, CameraView camera) {
        super(context, camera, ExposureTimeConverter.exposureTimeFractions);
    }

    public void applyToCamera(String value) {
        float time = stringToValue(value);
        camera.setExposureMode(Mode.MANUAL);
        camera.setExposureTime(time, maximumPreviewExposureTime);
    }
}
