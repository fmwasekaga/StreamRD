package com.kagaconnect.rtp;

import android.hardware.camera2.params.RggbChannelVector;

public class CameraState {
    public float exposureTime;
    public float focusDistance;
    public float ISO;
    public RggbChannelVector colorCorrection;
    public float aperture;

    CameraState(float exposureTime, float focusDistance, float ISO, RggbChannelVector colorCorrection, float aperture) {
        this.exposureTime = exposureTime;
        this.focusDistance = focusDistance;
        this.ISO = ISO;
        this.colorCorrection = colorCorrection;
        this.aperture = aperture;
    }

    CameraState() {
        this.aperture = 1.8f;
        this.exposureTime = 0.01f;
        this.focusDistance = 1;
        this.ISO = 800;
        this.colorCorrection = ColorTemperatureConverter.kelvinToRgb(6600);
    }
}
