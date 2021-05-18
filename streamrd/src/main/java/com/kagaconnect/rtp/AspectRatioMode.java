package com.kagaconnect.rtp;

public enum AspectRatioMode {
    ADJUST(0), FILL(1), ADJUST_ROTATE(2), FILL_ROTATE(3);

    private final int value;
    private AspectRatioMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
