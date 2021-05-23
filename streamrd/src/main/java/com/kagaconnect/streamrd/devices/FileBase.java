package com.kagaconnect.streamrd.devices;

import android.content.Context;
import android.media.MediaCodec;
import com.kagaconnect.streamrd.helpers.RtspServer;
import com.pedro.encoder.input.decoder.AudioDecoderInterface;
import com.pedro.encoder.input.decoder.VideoDecoderInterface;
import com.pedro.encoder.utils.CodecUtil;
import com.pedro.rtplibrary.view.LightOpenGlView;
import com.pedro.rtplibrary.view.OpenGlView;
import com.pedro.rtsp.rtsp.VideoCodec;
import java.nio.ByteBuffer;

public class FileBase extends  com.kagaconnect.rtp.base.FromFileBase {
    private RtspServer rtspServer;
    private ByteBuffer newSps;
    private ByteBuffer newPps;
    private ByteBuffer newVps;

    public FileBase(VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
        super(videoDecoderInterface, audioDecoderInterface);
    }

    public FileBase(Context context, VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
        super(context, videoDecoderInterface, audioDecoderInterface);
    }

    public FileBase(OpenGlView openGlView, VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
        super(openGlView, videoDecoderInterface, audioDecoderInterface);
    }

    public FileBase(LightOpenGlView lightOpenGlView, VideoDecoderInterface videoDecoderInterface, AudioDecoderInterface audioDecoderInterface) {
        super(lightOpenGlView, videoDecoderInterface, audioDecoderInterface);
    }

    public void attachServer(RtspServer server) {
        rtspServer = server;
    }

    public void detachServer() {
        rtspServer = null;
    }

    public void setVideoCodec(VideoCodec videoCodec) {
        videoEncoder.setType((videoCodec == VideoCodec.H265) ? CodecUtil.H265_MIME : CodecUtil.H264_MIME);
    }

    public String getEndPointConnection() {
        if(rtspServer == null) return "";
        return "rtsp://"+rtspServer.getServerIp()+":"+rtspServer.getPort()+"/";
    }

    public void startStream() {
        super.startStream("");
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
    protected void onSpsPpsVpsRtp(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
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
}
