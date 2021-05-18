package com.kagaconnect.streamrd.sliders;

import android.content.Context;

import com.kagaconnect.streamrd.devices.CameraView;
import com.kagaconnect.streamrd.helpers.Mode;

public class ExposureCompensationSlider extends CameraValueSlider {
    public ExposureCompensationSlider(Context context, CameraView camera) {
        super(context, camera, new String[]{
                "12",
                "11",
                "10",
                "9",
                "8",
                "7",
                "6",
                "5",
                "4",
                "3",
                "2",
                "1",
                "0",
                "-1",
                "-2",
                "-3",
                "-4",
                "-5",
                "-6",
                "-7",
                "-8",
                "-9",
                "-10",
                "-11",
                "-12",
        });
    }

    public void applyToCamera(String value) {
        float compensation = stringToValue(value);
        camera.setExposureMode(Mode.AUTO);
        camera.setExposureCompensation((int) compensation);
    }
}
