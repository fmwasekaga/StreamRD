package com.kagaconnect.streamrd.streaming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.kagaconnect.rtp.ColorTemperatureConverter;
import com.kagaconnect.streamrd.R;
import com.kagaconnect.streamrd.adapters.FilterAdapter;
import com.kagaconnect.streamrd.devices.CameraView;
import com.kagaconnect.streamrd.helpers.AudioQuality;
import com.kagaconnect.streamrd.helpers.ExposureTimeConverter;
import com.kagaconnect.streamrd.helpers.FilterInfo;
import com.kagaconnect.streamrd.helpers.Flash;
import com.kagaconnect.streamrd.helpers.RtspServer;
import com.kagaconnect.streamrd.helpers.VideoQuality;
import com.kagaconnect.streamrd.helpers.WindowHelper;
import com.kagaconnect.streamrd.sliders.ColorCorrectionSlider;
import com.kagaconnect.streamrd.sliders.ExposureTimeSlider;
import com.kagaconnect.streamrd.sliders.FocusSlider;
import com.kagaconnect.streamrd.sliders.ISOSlider;
import com.kagaconnect.streamrd.widget.TwoLineSeekBar;
import com.pedro.encoder.input.gl.render.filters.*;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.rtsp.utils.ConnectCheckerRtsp;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.List;

public class Server extends AppCompatActivity {
    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private int port = 8554;
    private float saturation = 0.0f;
    private static final String TAG = "Server";

    private CameraView Camera;
    private ISOSlider isoSlider;
    private View mSeekBarClickArea;
    private FocusSlider focusSlider;
    private TwoLineSeekBar mSeekBar;
    private FrameLayout valueSlider;
    private RecyclerView mFilterListView;
    private LinearLayout mSeekBarItemMenu;
    private ContrastFilterRender filter_contrast;
    private ExposureFilterRender filter_exposure;
    private BrightnessFilterRender filter_bright;
    private ExposureTimeSlider exposureTimeSlider;
    private HorizontalScrollView mAdjustScrollview;
    private SharpnessFilterRender filter_sharpness;
    private ColorCorrectionSlider colorCorrectionSlider;
    private SaturationFilterRender filter_saturation;
    private RotationFilterRender filter_rotation;
    private com.pedro.rtplibrary.view.OpenGlView CameraSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.streaming_server);

        filter_contrast     = new ContrastFilterRender();
        filter_exposure     = new ExposureFilterRender();
        filter_saturation   = new SaturationFilterRender();
        filter_sharpness    = new SharpnessFilterRender();
        filter_bright       = new BrightnessFilterRender();
        filter_rotation     = new RotationFilterRender();

        valueSlider         = findViewById(R.id.valueSlider);
        CameraSurface       = findViewById(R.id.cameraSurface);
        mFilterListView     = findViewById(R.id.filter_listView);
        mSeekBarClickArea   = findViewById(R.id.seek_bar_click_area);
        mSeekBar            = findViewById(R.id.item_seek_bar);
        mAdjustScrollview   = findViewById(R.id.fragment_adjust_scrollview);
        mSeekBarItemMenu    = findViewById(R.id.seek_bar_item_menu);

        TextView whiteBalanceInfo       = findViewById(R.id.wbInfo);
        TextView focusInfo              = findViewById(R.id.focusInfo);
        TextView isoInfo                = findViewById(R.id.isoInfo);
        TextView shutterInfo            = findViewById(R.id.expInfo);
        TextView apertureInfo           = findViewById(R.id.apertureInfo);
        ImageView ivCamera              = findViewById(R.id.ivCameraSwitch);
        ImageView ivFlash               = findViewById(R.id.ivFlashSwitch);
        ImageView ivRecord              = findViewById(R.id.ivVideoCaptureButton);
        ImageView ivSettings            = findViewById(R.id.ivSettings);
        ImageView ivCast                = findViewById(R.id.ivCastSwitch);
        ImageView ivAspectRatio         = findViewById(R.id.ivAspectRatioSwitch);
        ImageView ivAdjustments         = findViewById(R.id.ivAdjustments);
        ImageView ivFilters             = findViewById(R.id.ivfilters);
        ImageView ivRotate              = findViewById(R.id.ivRotate);
        TextView mFilterText            = findViewById(R.id.item_val);
        ImageView mFilterImage          = findViewById(R.id.item_label);
        RadioGroup mAdjustRadioGroup    = findViewById(R.id.fragment_adjust_radiogroup);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        List<FilterInfo> filters    = FilterInfo.getFilters();
        RtspServer rtspServer       = new RtspServer(this, new ConnectCheckerRtsp() {
            @Override
            public void onAuthSuccessRtsp() {
                runOnUiThread(() -> Toast.makeText(Server.this,
                        "Camera auth success", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onAuthErrorRtsp() {
                runOnUiThread(() -> Toast.makeText(Server.this,
                        "Camera auth error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDisconnectRtsp() {
                runOnUiThread(() -> Toast.makeText(Server.this,
                        "Camera disconnected", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onNewBitrateRtsp(long bitrate) { }

            @Override
            public void onConnectionFailedRtsp(@NotNull String reason) {
                runOnUiThread(() -> {
                    Toast.makeText(Server.this,
                            "Camera connection failed. $reason", Toast.LENGTH_SHORT).show();
                    if (Camera != null) Camera.stopStream();
                });
            }

            @Override
            public void onConnectionSuccessRtsp() {
                runOnUiThread(() -> Toast.makeText(Server.this,
                        "Camera connection success", Toast.LENGTH_SHORT).show());
            }
        }, port);
        FilterAdapter mAdapter      = new FilterAdapter(this, filters);

        filter_rotation.setRotation(0);
        filter_bright.setBrightness(0.0f);
        filter_sharpness.setSharpness(0.0f);
        filter_saturation.setSaturation(0.0f);
        filter_contrast.setContrast(toSeekValue(10.0f));
        filter_exposure.setExposure(toSeekValue(-10.0f));

        mFilterListView.setAdapter(mAdapter);

        CameraSurface.setVisibility(View.GONE);
        Camera = new CameraView(CameraSurface);
        Camera.attachServer(rtspServer);
        Camera.setAudioQuality(AudioQuality.Default);
        Camera.setVideoQuality(VideoQuality.HIGH);
        Camera.attachSurface(CameraSurface.getHolder(), CameraHelper.Facing.BACK);
        Camera.captureCameraInfo(info -> {
            int k = ColorTemperatureConverter.rgbNormalizedToKelvin(info.colorCorrection);
            final int kelvin = Math.round(k / 100) * 100;
            final int focusDistance = (int) (100 / info.focusDistance);
            final int iso = Math.round(info.ISO / 50) * 50;
            final String expTime = ExposureTimeConverter.secondsToFraction(info.exposureTime);
            final float aperture = info.aperture;

            runOnUiThread(() -> {
                whiteBalanceInfo.setText(kelvin + "K");
                focusInfo.setText(focusDistance + "cm");
                isoInfo.setText("ISO" + iso);
                shutterInfo.setText(expTime + "s");
                apertureInfo.setText("F" + aperture);
            });
        });

        CameraSurface.setKeepAspectRatio(true);
        CameraSurface.setCameraFlip(false, false);
        CameraSurface.setIsStreamHorizontalFlip(false);
        CameraSurface.setIsStreamVerticalFlip(false);
        //CameraSurface.setStreamRotation(0);
        //CameraSurface.enableAA(!CameraSurface.isAAEnabled());

        colorCorrectionSlider   = new ColorCorrectionSlider(this, Camera);
        focusSlider             = new FocusSlider(this, Camera);
        isoSlider               = new ISOSlider(this, Camera);
        exposureTimeSlider      = new ExposureTimeSlider(this, Camera);

        mAdjustRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            valueSlider.removeAllViews();
            valueSlider.setVisibility(View.GONE);
            mSeekBar.setVisibility(View.GONE);
            switch (checkedId) {
                case R.id.fragment_radio_contrast:
                    mSeekBar.setVisibility(View.VISIBLE);
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(0, 20, 10, 1);
                    mSeekBar.setValue(filter_contrast.getContrast()*10);
                    mFilterImage.setBackgroundResource(R.drawable.edit_bg_adjust_contrast);
                    break;
                case R.id.fragment_radio_exposure:
                    mSeekBar.setVisibility(View.VISIBLE);
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-20, 20, -10, 1);
                    mSeekBar.setValue(filter_exposure.getExposure()*10);
                    mFilterImage.setBackgroundResource(R.drawable.edit_bg_adjust_exposure);
                    break;
                case R.id.fragment_radio_saturation:
                    mSeekBar.setVisibility(View.VISIBLE);
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-10, 10, 0, 1);
                    mSeekBar.setValue(saturation);
                    mFilterImage.setBackgroundResource(R.drawable.edit_bg_adjust_saturation);
                    break;
                case R.id.fragment_radio_sharpness:
                    mSeekBar.setVisibility(View.VISIBLE);
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-4, 4, 0, 1);
                    mSeekBar.setValue(filter_sharpness.getSharpness()*10);
                    mFilterImage.setBackgroundResource(R.drawable.edit_bg_adjust_sharpness);
                    break;
                case R.id.fragment_radio_bright:
                    mSeekBar.setVisibility(View.VISIBLE);
                    mSeekBar.reset();
                    mSeekBar.setSeekLength(-5, 5, 0, 1);
                    mSeekBar.setValue(filter_bright.getBrightness()*10);
                    mFilterImage.setBackgroundResource(R.drawable.edit_bg_adjust_hdr);
                    break;
                case R.id.fragment_radio_whitebalance:
                    valueSlider.setVisibility(View.VISIBLE);
                    valueSlider.addView(colorCorrectionSlider);
                    mFilterImage.setBackgroundResource(R.drawable.ic_whitebalance);
                    mFilterText.setText(String.valueOf(colorCorrectionSlider.getSelectedValue()));
                    break;
                case R.id.fragment_radio_focus:
                    valueSlider.setVisibility(View.VISIBLE);
                    valueSlider.addView(focusSlider);
                    mFilterImage.setBackgroundResource(R.drawable.ic_focus_auto);
                    mFilterText.setText(String.valueOf(focusSlider.getSelectedValue()));
                    break;
                case R.id.fragment_radio_iso:
                    valueSlider.setVisibility(View.VISIBLE);
                    valueSlider.addView(isoSlider);
                    mFilterImage.setBackgroundResource(R.drawable.ic_iso);
                    mFilterText.setText(String.valueOf(isoSlider.getSelectedValue()));
                    break;
                case R.id.fragment_radio_shutter:
                    valueSlider.setVisibility(View.VISIBLE);
                    valueSlider.addView(exposureTimeSlider);
                    mFilterImage.setBackgroundResource(R.drawable.ic_shutter);
                    mFilterText.setText(String.valueOf(exposureTimeSlider.getSelectedValue()));
                    break;
                default:
                    break;
            }
        });

        mSeekBar.setOnSeekChangeListener(new TwoLineSeekBar.OnSeekChangeListener() {
            @Override
            public void onSeekStopped(float value, float step) { }

            @Override
            public void onSeekChanged(float value, float step) {
                mFilterImage.setPressed(value != 0.0f);

                switch (mAdjustRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.fragment_radio_contrast:
                        float contrast = toSeekValue(value);
                        mFilterText.setText(String.valueOf(contrast));
                        filter_contrast.setContrast(contrast);
                        break;
                    case R.id.fragment_radio_exposure:
                        float exposure = toSeekValue(value);
                        mFilterText.setText(String.valueOf(exposure));
                        filter_exposure.setExposure(exposure);
                        break;
                    case R.id.fragment_radio_saturation:
                        saturation = value;
                        float _saturation = toSeekValue(value);
                        mFilterText.setText(String.valueOf(_saturation));
                        filter_saturation.setSaturation(_saturation);
                        break;
                    case R.id.fragment_radio_sharpness:
                        float sharpness = toSeekValue(value);
                        mFilterText.setText(String.valueOf(sharpness));
                        filter_sharpness.setSharpness(sharpness);
                        break;
                    case R.id.fragment_radio_bright:
                        float brightness = toSeekValue(value);
                        mFilterText.setText(String.valueOf(brightness));
                        filter_bright.setBrightness(brightness);
                        break;
                    default:
                        mFilterText.setText(String.valueOf(value));
                        break;
                }
            }
        });

        mSeekBarClickArea.setOnClickListener(v -> {
            hideMenu();
        });

        ivCamera.setOnClickListener(v -> {
            hideMenu();
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {
                Camera.switchCamera();
                if (Camera.getCameraFacing() == CameraHelper.Facing.FRONT) {
                    Camera.setFlashState(Flash.OFF);
                    if (ivFlash != null)
                        ivFlash.setImageDrawable(ActivityCompat.getDrawable(
                                Server.this, R.drawable.ic_flash_off));
                }
            }
        });

        ivFlash.setOnClickListener(v -> {
            hideMenu();
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {
                if (Camera.getCameraFacing() != CameraHelper.Facing.FRONT) {
                    if (Camera.getFlashState() == Flash.OFF) {
                        Camera.setFlashState(Flash.ON);
                        ivFlash.setImageDrawable(ActivityCompat.getDrawable(
                                Server.this, R.drawable.ic_flash_on));
                    }
                    else if (Camera.getFlashState() == Flash.ON) {
                        Camera.setFlashState(Flash.OFF);
                        ivFlash.setImageDrawable(ActivityCompat.getDrawable(
                                Server.this, R.drawable.ic_flash_off));
                    }
                }
            }
        });

        ivRecord.setOnClickListener(v -> {
            hideMenu();
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {
                try{
                    if (!Camera.isRecording()) {
                        File folder = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        "/streamrd");
                        Camera.startRecording(Server.this, folder);
                    } else Camera.stopRecording();

                    ivRecord.setImageDrawable(ActivityCompat.getDrawable(
                            Server.this, Camera.isRecording() ?
                                    R.drawable.ic_camera_video_capture_started :
                                    R.drawable.ic_camera_video_capture_stopped));
                }
                catch (Exception ex){
                    Toast.makeText(this,
                            ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivSettings.setOnClickListener(v -> {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {

            }
        });

        ivCast.setOnClickListener(v -> {
            hideMenu();
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {
                boolean isFlashOn = false;
                if (Camera.getCameraFacing() != CameraHelper.Facing.FRONT) {
                    if (Camera.getFlashState() == Flash.ON) {
                        isFlashOn = true;
                        Camera.setFlashState(Flash.OFF);
                        ivFlash.setImageDrawable(ActivityCompat.getDrawable(
                                Server.this, R.drawable.ic_flash_off));
                    }
                }

                if (!Camera.isStreaming()) Camera.startStream();
                else Camera.stopStream();

                Camera.fixDarkPreview();

                ivCast.setImageDrawable(ActivityCompat.getDrawable(
                        Server.this, Camera.isStreaming() ?
                                R.drawable.ic_cast :
                                R.drawable.ic_cast_off));

                if (Camera.getCameraFacing() != CameraHelper.Facing.FRONT) {
                    if (isFlashOn) {
                        Camera.setFlashState(Flash.ON);
                        ivFlash.setImageDrawable(ActivityCompat.getDrawable(
                                Server.this, R.drawable.ic_flash_on));
                    }
                }

                setFilters();
            }
        });

        ivAspectRatio.setOnClickListener(v -> {
            hideMenu();
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {
                CameraSurface.setKeepAspectRatio(!CameraSurface.isKeepAspectRatio());
                //CameraSurface.setAspectRatioMode(CameraSurface.isKeepAspectRatio() ?
                //        AspectRatioMode.ADJUST : AspectRatioMode.FILL);
            }
        });

        ivAdjustments.setOnClickListener(v -> {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {
                if (mSeekBarItemMenu.getVisibility() == View.GONE) {
                    mAdjustRadioGroup.check(R.id.fragment_radio_contrast);
                }
                mAdjustScrollview.scrollTo(0,0);
                valueSlider.removeAllViews();
                mFilterListView.setVisibility(View.GONE);
                mSeekBarItemMenu.setVisibility(mSeekBarItemMenu.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);

                mSeekBarClickArea.setVisibility(mSeekBarItemMenu.getVisibility());
            }
        });

        ivFilters.setOnClickListener(v -> {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {
                valueSlider.removeAllViews();
                mSeekBarItemMenu.setVisibility(View.GONE);

                mFilterListView.setVisibility(mFilterListView.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
                mSeekBarClickArea.setVisibility(mFilterListView.getVisibility());
            }
        });

        ivRotate.setOnClickListener(v -> {
            hideMenu();
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            }
            else {
                filter_rotation.setRotation(-90);
            }
        });

        isoSlider.setOnValueChangeListener((newValue, oldValue) -> mFilterText.setText(newValue));
        focusSlider.setOnValueChangeListener((newValue, oldValue) -> mFilterText.setText(newValue));
        exposureTimeSlider.setOnValueChangeListener((newValue, oldValue) -> mFilterText.setText(newValue));
        colorCorrectionSlider.setOnValueChangeListener((newValue, oldValue) -> mFilterText.setText(newValue));

        mAdapter.setOnFilterChangeListener(filter -> CameraSurface.setFilter(6, filter.getFilter()));

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
        else {
            CameraSurface.setVisibility(View.VISIBLE);
            setFilters();
        }
    }

    /*public void unlockAe() {
        whiteBalanceButton.deactivate();
        focusButton.deactivate();

        shutterButton.disable();
        isoButton.disable();
        exposureButton.enable();
    }

    public void lockAe() {
        whiteBalanceButton.deactivate();
        focusButton.deactivate();

        shutterButton.enable();
        isoButton.enable();
        exposureButton.disable();
    }*/

    private void setFilters(){
        CameraSurface.setFilter(0, filter_contrast);
        CameraSurface.setFilter(1, filter_exposure);
        CameraSurface.setFilter(2, filter_saturation);
        CameraSurface.setFilter(3, filter_sharpness);
        CameraSurface.setFilter(4, filter_bright);
        CameraSurface.setFilter(5, filter_rotation);
    }

    private void hideMenu(){
        mAdjustScrollview.scrollTo(0,0);
        valueSlider.removeAllViews();
        mSeekBarItemMenu.setVisibility(View.GONE);
        mFilterListView.setVisibility(View.GONE);
        mSeekBarClickArea.setVisibility(mSeekBarItemMenu.getVisibility());
    }

    private float toSeekValue(float value){
        return (float)(Math.round((value/10.0) * 10.0) / 10.0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode == 1) {
            if (hasPermissions(this, permissions)) {
                CameraSurface.setVisibility(View.VISIBLE);
                setFilters();
            }
            else {
                Toast.makeText(this,
                        "Permissions required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowHelper.hideSystemUI(getWindow());
        }
    }

}