package com.kagaconnect.streamrd.helpers;

import android.content.Context;

import com.pedro.encoder.input.video.CameraHelper;

public class VideoQuality {
    private int width;
    private int height;
    private int fps;
    private int bitrate;
    private int iFrameInterval;
    private int rotation;
    private int avcProfile;
    private int avcProfileLevel;

    public final static VideoQuality LOW = new VideoQuality(176,144,30,
            500*1024, 0);
    public final static VideoQuality MEDIUM = new VideoQuality(640, 360, 30,
            800*1024, 0);//800 - 1200 kbps
    public final static VideoQuality HIGH = new VideoQuality(960, 540, 30,
            1200*1024, 0);//1200 - 1500 kbps
    public final static VideoQuality HIGH2 = new VideoQuality(854, 480, 30,
            1200*1024, 0);//1200 - 1500 kbps
    public final static VideoQuality HD720 = new VideoQuality(1280, 720, 30,
            1500*1024, 0);//1,500 - 4,000 kbps
    public final static VideoQuality HD1080 = new VideoQuality(1920, 1080, 30,
            4000*1024, 0);//4,000-8,000 kbps


    /*public static VideoQuality HD720 = new VideoQuality(1280, 720, 30,
            1200 * 1024, 0);

    public static VideoQuality HD1080 = new VideoQuality(1920, 1080, 30,
            2400 * 1024, 0);*/

    public VideoQuality(Context context) {
        this(1280, 720, 30, 1200 * 1024,
                CameraHelper.getCameraOrientation(context));
    }

    public VideoQuality(Context context, int width, int height, int bitrate) {
        this(width, height, 30, bitrate, 2,
                CameraHelper.getCameraOrientation(context));
    }

    public VideoQuality(Context context, int width, int height, int fps, int bitrate) {
        this(width, height, fps, bitrate, 2,
                CameraHelper.getCameraOrientation(context));
    }

    public VideoQuality(int width, int height, int fps, int bitrate, int rotation) {
        this(width, height, fps, bitrate, 2, rotation);
    }

    public VideoQuality(int width, int height, int fps, int bitrate, int iFrameInterval,
                                int rotation) {
        this(width, height, fps, bitrate, iFrameInterval, rotation, -1, -1);
    }

    public VideoQuality(int width, int height, int fps, int bitrate, int iFrameInterval,
                                int rotation, int avcProfile, int avcProfileLevel) {
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.bitrate = bitrate;
        this.iFrameInterval = iFrameInterval;
        this.rotation = rotation;
        this.avcProfile = avcProfile;
        this.avcProfileLevel = avcProfileLevel;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getIframeInterval() {
        return iFrameInterval;
    }

    public void setIframeInterval(int iFrameInterval) {
        this.iFrameInterval = iFrameInterval;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getAvcProfile() {
        return avcProfile;
    }

    public void setAvcProfile(int avcProfile) {
        this.avcProfile = avcProfile;
    }

    public int getAvcProfileLevel() {
        return avcProfileLevel;
    }

    public void setAvcProfileLevel(int avcProfileLevel) {
        this.avcProfileLevel = avcProfileLevel;
    }
}
