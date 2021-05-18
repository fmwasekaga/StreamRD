package com.kagaconnect.streamrd.helpers;

public class AudioQuality {
    private int bitrate;
    private int sampleRate;
    private boolean isStereo;
    private boolean echoCanceler;
    private boolean noiseSuppressor;

    public static AudioQuality Default = new AudioQuality();

    public AudioQuality() {
        this(64 * 32000, 32000, true, false, false);
    }

    public AudioQuality(int bitrate, int sampleRate, boolean isStereo) {
        this(bitrate, sampleRate, isStereo, false, false);
    }

    public AudioQuality(int bitrate, int sampleRate, boolean isStereo, boolean echoCanceler,
                                boolean noiseSuppressor) {
        this.bitrate = bitrate;
        this.sampleRate = sampleRate;
        this.isStereo = isStereo;
        this.echoCanceler = echoCanceler;
        this.noiseSuppressor = noiseSuppressor;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public boolean isStereo() {
        return isStereo;
    }

    public void setStereo(boolean stereo) {
        isStereo = stereo;
    }

    public boolean isEchoCanceler() {
        return echoCanceler;
    }

    public void setEchoCanceler(boolean echoCanceler) {
        this.echoCanceler = echoCanceler;
    }

    public boolean isNoiseSuppressor() {
        return noiseSuppressor;
    }

    public void setNoiseSuppressor(boolean noiseSuppressor) {
        this.noiseSuppressor = noiseSuppressor;
    }
}
