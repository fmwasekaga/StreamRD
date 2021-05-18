package com.kagaconnect.streamrd.devices;

import android.content.Context;
import android.hardware.camera2.params.RggbChannelVector;
import android.media.MediaCodec;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;


import com.kagaconnect.rtp.Camera1ApiManagerExtension;
import com.kagaconnect.rtp.Camera2ApiManagerExtension;
import com.kagaconnect.rtp.CameraInfoCallback;
import com.kagaconnect.rtp.view.LightOpenGlView;
import com.kagaconnect.rtp.view.OpenGlView;
import com.kagaconnect.streamrd.helpers.AudioQuality;
import com.kagaconnect.streamrd.helpers.RtspServer;
import com.kagaconnect.streamrd.helpers.VideoQuality;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.utils.CodecUtil;
import com.pedro.rtsp.rtsp.VideoCodec;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Camera1Base extends com.kagaconnect.rtp.base.Camera1Base implements SurfaceHolder.Callback {

    private RtspServer rtspServer;
    private CameraHelper.Facing facing = CameraHelper.Facing.BACK;
    private SurfaceHolder holder;
    private ByteBuffer newSps;
    private ByteBuffer newPps;
    private ByteBuffer newVps;

    private AudioQuality audioQuality;
    private VideoQuality videoQuality;

    public Camera1Base(SurfaceView surfaceView) {
        super(surfaceView);

        videoQuality = new VideoQuality(context);
        audioQuality = new AudioQuality();
    }

    public Camera1Base(TextureView textureView) {
        super(textureView);

        videoQuality = new VideoQuality(context);
        audioQuality = new AudioQuality();
    }

    public Camera1Base(OpenGlView openGlView) {
        super(openGlView);

        videoQuality = new VideoQuality(context);
        audioQuality = new AudioQuality();
    }

    public Camera1Base(LightOpenGlView lightOpenGlView) {
        super(lightOpenGlView);

        videoQuality = new VideoQuality(context);
        audioQuality = new AudioQuality();
    }

    public Camera1Base(Context context) {
        super(context);

        videoQuality = new VideoQuality(context);
        audioQuality = new AudioQuality();
    }

    public void attachServer(RtspServer server) {
        rtspServer = server;
    }

    public void detachServer() {
        rtspServer = null;
    }

    public void startRecording(Context context, @NotNull File folder){
        try {
            if (!folder.exists()) {
                folder.mkdir();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDateAndTime = sdf.format(new Date());
            if (!isStreaming()) {
                if (prepareAudio() && prepareVideo()) {
                    startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                    //bRecord!!.setText(R.string.stop_record)
                    Toast.makeText(context, "Recording started... ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error preparing stream, This device cant do it",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                Toast.makeText(context, "Recording started... ", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            stopRecord();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void stopRecording(){
        stopRecord();
        //stopStream();
        Toast.makeText(context, "Recording stopped... ", Toast.LENGTH_SHORT).show();
    }

    public void attachSurface(@NotNull SurfaceHolder holder, CameraHelper.Facing facing){
        this.facing = facing;
        this.holder = holder;
        this.holder.addCallback(this);
    }

    public void setVideoCodec(VideoCodec videoCodec) {
        videoEncoder.setType((videoCodec == VideoCodec.H265) ? CodecUtil.H265_MIME : CodecUtil.H264_MIME);
    }

    public String getEndPointConnection() {
        if(rtspServer == null) return "";
        return "rtsp://"+rtspServer.getServerIp()+":"+rtspServer.getPort()+"/";
    }

    public void startStream() {
        startStream("");
    }

    public void startStream(String url) {
        startStream(url, videoQuality, audioQuality);
    }

    public void startStream(String url, VideoQuality video, AudioQuality audio) {
        if (isRecording() || prepareAudio(audio.getBitrate(),
                                          audio.getSampleRate(),
                                          audio.isStereo(),
                                          audio.isEchoCanceler(),
                                          audio.isNoiseSuppressor()) &&
                              prepareVideo(video.getWidth(),
                                      video.getHeight(),
                                      video.getFps(),
                                      video.getBitrate(),
                                      video.getIframeInterval(),
                                      video.getRotation(),
                                      video.getAvcProfile(),
                                      video.getAvcProfileLevel())) {
            super.startStream(url);
        } else {
            Toast.makeText(context,
                    "Error preparing stream, This device cant do it",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setAuthorization(String user, String password) {

    }

    @Override
    protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
        if(rtspServer != null) {
            rtspServer.setStereo(isStereo);
            rtspServer.setSampleRate(sampleRate);
        }
    }

    @Override
    protected void startStreamRtp(String url) {

    }

    @Override
    protected void stopStreamRtp() {
        if(rtspServer != null)rtspServer.stopServer();
    }

    @Override
    public boolean shouldRetry(String reason) {
        return false;
    }

    @Override
    public void setReTries(int reTries) {

    }

    @Override
    protected void reConnect(long delay) {

    }

    @Override
    public boolean hasCongestion() {
        return false;
    }

    @Override
    public void resizeCache(int newSize) throws RuntimeException {

    }

    @Override
    public int getCacheSize() {
        return 0;
    }

    @Override
    public long getSentAudioFrames() {
        return 0;
    }

    @Override
    public long getSentVideoFrames() {
        return 0;
    }

    @Override
    public long getDroppedAudioFrames() {
        return 0;
    }

    @Override
    public long getDroppedVideoFrames() {
        return 0;
    }

    @Override
    public void resetSentAudioFrames() {
    }

    @Override
    public void resetSentVideoFrames() {
    }

    @Override
    public void resetDroppedAudioFrames() {
    }

    @Override
    public void resetDroppedVideoFrames() {
    }

    @Override
    protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
        if(rtspServer != null)rtspServer.sendAudio(aacBuffer, info);
    }

    @Override
    protected void onSpsPpsVpsRtp(@NotNull ByteBuffer sps, @NotNull ByteBuffer pps, ByteBuffer vps) {
        newSps = sps.duplicate();
        newPps = pps.duplicate();
        newVps = vps != null ? vps.duplicate() : null;
        if(rtspServer != null){
            rtspServer.setVideoInfo(newSps, newPps, newVps);
            rtspServer.startServer();
        }
    }

    @Override
    protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
        if(rtspServer != null)rtspServer.sendVideo(h264Buffer, info);
    }

    @Override
    public void setLogs(boolean enable) {
        if(rtspServer != null)rtspServer.setLogs(enable);
    }

    public void startPreview(CameraHelper.Facing cameraFacing, int width, int height, int rotation) {
        super.startPreview(cameraFacing, width, height, rotation);
    }

    public void startPreview(CameraHelper.Facing cameraFacing, int width, int height) {
        startPreview(cameraFacing, width, height);
    }

    public void startPreview(CameraHelper.Facing cameraFacing) {
        startPreview(cameraFacing, 640, 480);
    }

    public void startPreview(int width, int height) {
        startPreview(CameraHelper.Facing.BACK, width, height);
    }

    public void startPreview() {
        startPreview(CameraHelper.Facing.BACK);
    }

    public void stopPreview() {
        if (isRecording())super.stopRecord();
        if (isStreaming())super.stopStream();
        super.stopPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(this.facing, videoQuality.getWidth(), videoQuality.getHeight());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (isRecording())stopRecord();
        if (isStreaming())stopStream();
        stopPreview();
    }

    public void fixDarkPreview(){
        super.fixDarkPreview();
    }

    /**
     * Experimental
     */
    public void captureCameraInfo(CameraInfoCallback cameraInfoCallback) {
        super.captureCameraInfo(cameraInfoCallback);
    }

    public void setExposureTime(float time, float maximumExposureTime){
        super.setExposureTime(time, maximumExposureTime);
    }

    public void setISO(float iso){
        super.setISO(iso);
    }

    public void setExposureCompensation(int compensation){
        super.setExposureCompensation(compensation);
    }

    public void setFocusDistanceInMeters(float distance){
        super.setFocusDistanceInMeters(distance);
    }

    public void setColorCorrection(RggbChannelVector colorCorrection){
        super.setColorCorrection(colorCorrection);
    }

    public void setAudioQuality(AudioQuality quality) {
        audioQuality = quality;
    }

    public void setVideoQuality(VideoQuality quality) {
        videoQuality = quality;
    }

    public AudioQuality getAudioQuality() {
        return audioQuality;
    }

    public VideoQuality getVideoQuality() {
        return videoQuality;
    }

}
