package com.kagaconnect.streamrd.sliders;

import android.content.Context;

import com.kagaconnect.streamrd.devices.CameraView;
import com.kagaconnect.streamrd.helpers.Mode;

public class ISOSlider extends CameraValueSlider {
    public ISOSlider(Context context, CameraView camera) {
        super(context, camera, new String[]{
                "2700",
                "2000",
                "1500",
                "1000",
                "800",
                "700",
                "600",
                "500",
                "450",
                "400",
                "350",
                "300",
                "250",
                "200",
                "150",
                "100",
                "50",
        });
    }

    public void applyToCamera(String value) {
        float ISO = stringToValue(value);
        camera.setExposureMode(Mode.MANUAL);
        camera.setISO(ISO);
    }
}
