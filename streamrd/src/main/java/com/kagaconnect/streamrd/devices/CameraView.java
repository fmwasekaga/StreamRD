package com.kagaconnect.streamrd.devices;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Build;
import android.util.Range;
import android.util.Size;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.kagaconnect.rtp.Camera1ApiManagerExtension;
import com.kagaconnect.rtp.Camera2ApiManagerExtension;
import com.kagaconnect.rtp.CameraInfoCallback;
import com.kagaconnect.streamrd.helpers.AudioQuality;
import com.kagaconnect.streamrd.helpers.Filters;
import com.kagaconnect.streamrd.helpers.Flash;
import com.kagaconnect.streamrd.helpers.Mode;
import com.kagaconnect.streamrd.helpers.RtspServer;
import com.kagaconnect.streamrd.helpers.VideoQuality;
import com.pedro.encoder.input.audio.CustomAudioEffect;
import com.pedro.encoder.input.audio.MicrophoneMode;
import com.pedro.encoder.input.video.Camera1ApiManager;
import com.pedro.encoder.input.video.Camera2ApiManager;
import com.pedro.encoder.input.video.CameraCallbacks;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtplibrary.util.FpsListener;
import com.pedro.rtplibrary.view.LightOpenGlView;
import com.pedro.rtplibrary.view.OpenGlView;
import com.pedro.rtsp.rtsp.VideoCodec;
import org.jetbrains.annotations.NotNull;
//import com.kagaconnect.rtp.util.FpsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraView {
    private Context context;
    private Camera1Base camera1;
    private Camera2Base camera2;

    private int cameraTechnologyUsed = 0;
    private CameraHelper.Facing facing = CameraHelper.Facing.BACK;
    private Flash flash = Flash.OFF;
    private Filters filter = Filters.NO_FILTER;

    private Mode exposureMode = Mode.AUTO;
    private Mode focusMode = Mode.AUTO;
    private Mode colorCorrectionMode = Mode.AUTO;

    public CameraView(Context context) {
        this.context = context;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            camera1 = new Camera1Base(context);
            cameraTechnologyUsed = 1;
        }
        else
        {
            camera2 = new Camera2Base(context, true);
            cameraTechnologyUsed = 2;
        }
    }

    public CameraView(SurfaceView surfaceView) {
        this.context = surfaceView.getContext();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            camera1 = new Camera1Base(surfaceView);
            cameraTechnologyUsed = 1;
        }
        else
        {
            camera2 = new Camera2Base(surfaceView);
            cameraTechnologyUsed = 2;
        }
    }

    public CameraView(TextureView textureView) {
        this.context = textureView.getContext();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            camera1 = new Camera1Base(textureView);
            cameraTechnologyUsed = 1;
        }
        else
        {
            camera2 = new Camera2Base(textureView);
            cameraTechnologyUsed = 2;
        }
    }

    public CameraView(OpenGlView openGlView) {
        this.context = openGlView.getContext();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            camera1 = new Camera1Base(openGlView);
            cameraTechnologyUsed = 1;
        }
        else
        {
            camera2 = new Camera2Base(openGlView);
            cameraTechnologyUsed = 2;
        }
    }

    public CameraView(LightOpenGlView lightOpenGlView) {
        this.context = lightOpenGlView.getContext();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            camera1 = new Camera1Base(lightOpenGlView);
            cameraTechnologyUsed = 1;
        }
        else
        {
            camera2 = new Camera2Base(lightOpenGlView);
            cameraTechnologyUsed = 2;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraView(Context context, boolean useOpengl) {
        this.context = context;
        camera2 = new Camera2Base(context, useOpengl);
        cameraTechnologyUsed = 2;
    }

    public int getCameraTechnologyUsed(){ return  cameraTechnologyUsed; }

    public void attachServer(RtspServer server) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.attachServer(server);
        else if(camera2 != null)
            camera2.attachServer(server);
    }

    public void detachServer() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.detachServer();
        else if(camera2 != null)
            camera2.detachServer();
    }

    public void startRecording(Context context, @NotNull File folder){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.startRecording(context, folder);
        else if(camera2 != null)
            camera2.startRecording(context, folder);
    }

    public void stopRecording(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.stopRecording();
        else if(camera2 != null)
            camera2.stopRecording();
    }

    public void attachSurface(SurfaceHolder holder, CameraHelper.Facing facing){
        this.facing = facing;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.attachSurface(holder, this.facing);
        else if(camera2 != null)
            camera2.attachSurface(holder, this.facing);
    }

    public void setVideoCodec(VideoCodec videoCodec) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setVideoCodec(videoCodec);
        else if(camera2 != null)
            camera2.setVideoCodec(videoCodec);
    }

    public String getEndPointConnection() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getEndPointConnection();
        else if(camera2 != null)
            return camera2.getEndPointConnection();

        return "";
    }

    public void stopStream(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.stopStream();
        else if(camera2 != null)
            camera2.stopStream();
    }

    public void stopPreview() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.stopPreview();
        else if(camera2 != null)
            camera2.stopPreview();
    }

    public void startPreview(CameraHelper.Facing facing, int width, int height, int rotation) {
        this.facing = facing;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.startPreview(this.facing, width, height, rotation);
        else if(camera2 != null)
            camera2.startPreview(this.facing, width, height, rotation);
    }

    public void startPreview(CameraHelper.Facing facing, int width, int height) {
        this.facing = facing;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.startPreview(this.facing, width, height);
        else if(camera2 != null)
            camera2.startPreview(this.facing, width, height);
    }

    public void startPreview(CameraHelper.Facing facing) {
        this.facing = facing;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.startPreview(this.facing);
        else if(camera2 != null)
            camera2.startPreview(this.facing);
    }

    public void startPreview() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.startPreview(this.facing);
        else if(camera2 != null)
            camera2.startPreview(this.facing);
    }

    public CameraHelper.Facing getCameraFacing(){
        return this.facing;
    }

    public void switchCamera(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
        {
            camera1.switchCamera();
            this.facing = camera1.isFrontCamera() ?
                    CameraHelper.Facing.FRONT : CameraHelper.Facing.BACK;
        }
        else if(camera2 != null)
        {
            camera2.switchCamera();
            this.facing = camera2.isFrontCamera() ?
                    CameraHelper.Facing.FRONT : CameraHelper.Facing.BACK;
        }
    }

    public Flash getFlashState(){
        return flash;
    }

    public void setFlashState(Flash flash){
        this.flash = flash;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
        {
            if(this.flash == Flash.OFF) {
                camera1.disableLantern();
            }
            else if(this.flash == Flash.ON) {
                try{ camera1.enableLantern(); }
                catch (Exception ex){
                    this.flash = Flash.OFF;
                }
            }
        }
        else if(camera2 != null)
        {
            if(this.flash == Flash.OFF) {
                camera2.disableLantern();
            }
            else if(this.flash == Flash.ON) {
                try{ camera2.enableLantern(); }
                catch (Exception ex){
                    this.flash = Flash.OFF;
                }
            }
        }
    }

    public boolean isRecording(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.isRecording();
        else if(camera2 != null)
            return camera2.isRecording();

        return false;
    }

    public boolean isStreaming(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.isStreaming();
        else if(camera2 != null)
            return camera2.isStreaming();

        return false;
    }

    public void startStream(){
        startStream("");
    }

    public void startStream(String url){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.startStream(url);
        else if(camera2 != null)
            camera2.startStream(url);
    }

    public void setFilter(Filters filter){
        this.filter = filter;
    }

    public Filters getFilter(){
        return this.filter;
    }


    public void setAudioQuality(AudioQuality quality) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setAudioQuality(quality);
        else if(camera2 != null)
            camera2.setAudioQuality(quality);
    }

    public void setVideoQuality(VideoQuality quality) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setVideoQuality(quality);
        else if(camera2 != null)
            camera2.setVideoQuality(quality);
    }

    public AudioQuality getAudioQuality() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getAudioQuality();
        else if(camera2 != null)
            return camera2.getAudioQuality();

        return null;
    }

    public VideoQuality getVideoQuality() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getVideoQuality();
        else if(camera2 != null)
            return camera2.getVideoQuality();

        return null;
    }

    public void setMicrophoneMode(MicrophoneMode microphoneMode) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setMicrophoneMode(microphoneMode);
        else if(camera2 != null)
            camera2.setMicrophoneMode(microphoneMode);
    }

    public void setCameraCallbacks(CameraCallbacks callbacks) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setCameraCallbacks(callbacks);
        else if(camera2 != null)
            camera2.setCameraCallbacks(callbacks);
    }

    public void setCustomAudioEffect(CustomAudioEffect customAudioEffect) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setCustomAudioEffect(customAudioEffect);
        else if(camera2 != null)
            camera2.setCustomAudioEffect(customAudioEffect);
    }

    public void setFpsListener(FpsListener.Callback callback) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setFpsListener(callback);
        else if(camera2 != null)
            camera2.setFpsListener(callback);
    }

    public void enableFaceDetection(Camera1ApiManagerExtension.FaceDetectorCallback faceDetectorCallback) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.enableFaceDetection(faceDetectorCallback);
    }

    public void enableFaceDetection(Camera2ApiManagerExtension.FaceDetectorCallback faceDetectorCallback) {
        if(camera2 != null)
            camera2.enableFaceDetection(faceDetectorCallback);
    }

    public void disableFaceDetection() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.disableFaceDetection();
        else if(camera2 != null)
            camera2.disableFaceDetection();
    }

    public boolean isFaceDetectionEnabled() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.isFaceDetectionEnabled();
        else if(camera2 != null)
            return camera2.isFaceDetectionEnabled();

        return false;
    }

    public boolean isFrontCamera() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.isFrontCamera();
        else if(camera2 != null)
            return camera2.isFrontCamera();

        return false;
    }

    public void enableLantern() throws Exception {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.enableLantern();
        else if(camera2 != null)
            camera2.enableLantern();
    }

    public void disableLantern() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.disableLantern();
        else if(camera2 != null)
            camera2.disableLantern();
    }

    public boolean isLanternEnabled() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.isLanternEnabled();
        else if(camera2 != null)
            return camera2.isLanternEnabled();

        return false;
    }

    public boolean isLanternSupported() {
        if(camera2 != null)
            return camera2.isLanternSupported();

        return false;
    }

    public void enableAutoFocus() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.enableAutoFocus();
        else if(camera2 != null)
            camera2.enableAutoFocus();
    }

    public void disableAutoFocus() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.disableAutoFocus();
        else if(camera2 != null)
            camera2.disableAutoFocus();
    }

    public boolean isAutoFocusEnabled() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.isAutoFocusEnabled();
        else if(camera2 != null)
            return camera2.isAutoFocusEnabled();

        return false;
    }

    public List<Camera.Size> getResolutionsBack_Camera1Technology() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getResolutionsBack();
        return new ArrayList<>();
    }

    public List<Size> getResolutionsBack() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
        {
            List<Size> newSizes = new ArrayList<>();
            List<Camera.Size> sizes = camera1.getResolutionsBack();
            for(Camera.Size size : sizes){
                newSizes.add(new Size(size.width, size.height));
            }
            return newSizes;
        }
        else if(camera2 != null)
            return camera2.getResolutionsBack();

        return new ArrayList<>();
    }

    public List<Size> getResolutionsFront() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
        {
            List<Size> newSizes = new ArrayList<>();
            List<Camera.Size> sizes =  camera1.getResolutionsFront();
            for(Camera.Size size : sizes){
                newSizes.add(new Size(size.width, size.height));
            }
            return newSizes;
        }
        else if(camera2 != null)
            return camera2.getResolutionsFront();

        return new ArrayList<>();
    }

    public List<int[]> getSupportedFps_Camera1Technology() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getSupportedFps();

        return null;
    }

    public Range<Integer>[] getSupportedFps() {
        if(camera2 != null)
            return camera2.getSupportedFps();

        return null;
    }

    public CameraCharacteristics getCameraCharacteristics() {
        if(camera2 != null)
            return camera2.getCameraCharacteristics();

        return null;
    }

    public void disableAudio() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.disableAudio();
        else if(camera2 != null)
            camera2.disableAudio();
    }

    public void enableAudio() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.enableAudio();
        else if(camera2 != null)
            camera2.enableAudio();
    }

    public boolean isAudioMuted() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.isAudioMuted();
        else if(camera2 != null)
            return camera2.isAudioMuted();

        return false;
    }

    public boolean isVideoEnabled() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.isVideoEnabled();
        else if(camera2 != null)
            return camera2.isVideoEnabled();

        return false;
    }

    public float getMaxZoom() {
        if(camera2 != null)
            return camera2.getMaxZoom();

        return 0;
    }

    public float getZoom() {
        if(camera2 != null)
            return camera2.getZoom();

        return 0;
    }

    public void setZoom(float level) {
        if(camera2 != null)
            camera2.setZoom(level);
    }

    public void setZoom(MotionEvent event) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setZoom(event);
        else if(camera2 != null)
            camera2.setZoom(event);
    }

    public int getBitrate() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getBitrate();
        else if(camera2 != null)
            return camera2.getBitrate();

        return 0;
    }

    public int getResolutionValue() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getResolutionValue();
        else if(camera2 != null)
            return camera2.getResolutionValue();

        return 0;
    }

    public int getStreamWidth() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getStreamWidth();
        else if(camera2 != null)
            return camera2.getStreamWidth();

        return 0;
    }

    public int getStreamHeight() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getStreamHeight();
        else if(camera2 != null)
            return camera2.getStreamHeight();

        return 0;
    }

    public void setExposure(int value) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.setExposure(value);
        else if(camera2 != null)
            camera2.setExposure(value);
    }

    public int getExposure() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getExposure();
        else if(camera2 != null)
            return camera2.getExposure();

        return 0;
    }

    public int getMaxExposure() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getMaxExposure();
        else if(camera2 != null)
            return camera2.getMaxExposure();

        return 0;
    }

    public int getMinExposure() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            return camera1.getMinExposure();
        else if(camera2 != null)
            return camera2.getMinExposure();

        return 0;
    }

    public void tapToFocus(MotionEvent event) {
        if(camera2 != null)
            camera2.tapToFocus(event);
    }

    public void tapToFocus(View view, MotionEvent event) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.tapToFocus(view, event);
        else if(camera2 != null)
            camera2.tapToFocus(event);
    }

    public void fixDarkPreview(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.fixDarkPreview();
        else if(camera2 != null)
            camera2.fixDarkPreview();
    }

    /**
     * Experimental
     */
    public void captureCameraInfo(CameraInfoCallback cameraInfoCallback) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
            camera1.captureCameraInfo(cameraInfoCallback);
        else if(camera2 != null)
            camera2.captureCameraInfo(cameraInfoCallback);
    }

    public void setExposureTime(float time, float maximumExposureTime){
        if (exposureMode == Mode.MANUAL) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
                camera1.setExposureTime(time, maximumExposureTime);
            else if (camera2 != null)
                camera2.setExposureTime(time, maximumExposureTime);
        }
    }

    public void setISO(float iso){
        if(exposureMode == Mode.MANUAL) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
                camera1.setISO(iso);
            else if (camera2 != null)
                camera2.setISO(iso);
        }
    }

    public void setExposureCompensation(int compensation){
        if(exposureMode == Mode.AUTO) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
                camera1.setExposureCompensation(compensation);
            else if (camera2 != null)
                camera2.setExposureCompensation(compensation);
        }
    }

    public void setFocusDistanceInMeters(float distance){
        if(focusMode == Mode.MANUAL) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
                camera1.setFocusDistanceInMeters(distance);
            else if (camera2 != null)
                camera2.setFocusDistanceInMeters(distance);
        }
    }

    public void setColorCorrection(RggbChannelVector colorCorrection){
        if(colorCorrectionMode == Mode.MANUAL) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && camera1 != null)
                camera1.setColorCorrection(colorCorrection);
            else if (camera2 != null)
                camera2.setColorCorrection(colorCorrection);
        }
    }

    public Mode getExposureMode() {
        return exposureMode;
    }

    public void setExposureMode(Mode exposureMode) {
        this.exposureMode = exposureMode;
    }

    public void setFocusMode(Mode focusMode) {
        this.focusMode = focusMode;
    }

    public Mode getFocusMode() {
        return focusMode;
    }

    public void setColorCorrectionMode(Mode colorCorrectionMode) {
        this.colorCorrectionMode = colorCorrectionMode;
    }

    public Mode getColorCorrectionMode() {
        return colorCorrectionMode;
    }

}
