package com.example.mycamera;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.*;
import com.example.mycamera.utils.FileUtils;
import com.example.mycamera.utils.StringUtils;
import com.example.mycamera.utils.Utils;
import com.example.mycamera.view.CameraPreview;
import com.example.mycamera.view.ClipView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // 预览状态
    private static int STATE_PREVIEW_STOP = 0;
    private static int STATE_IDEL = 1;
    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d("test", "onShutter'd");
        }
    };
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("test", "onPictureTaken - raw");
        }
    };
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("test", "onPictureTaken - jpeg");
            boolean success = FileUtils.saveFile(data);
            if (success) {
                Toast.makeText(MainActivity.this, "save success!", Toast.LENGTH_SHORT).show();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Log.d("test", "rePreview");
                    mCameraDevice.startPreview();
                }
            }, 4000);
        }
    };
    private Button mTakeButton;
    private Button mPlusButton;
    private Button mMinusButton;
    private Button mInfoButton;
    private Button mMoreInfoButton;
    private Button mMatchPictureButton;
    private Button mSwitchButton;
    private TextView mInfoPanel;
    private Spinner mPictureSpinner;
    private Spinner mPreviewSpinner;
    private Spinner mPreviewModeSpinner;
    private CameraPreview mPreview;
    private ClipView mClipView;
    private CheckBox mClipShowCheck;
    private int mFrontCameraId = -1;
    private int mBackCameraId = -1;
    private int mCurrentCameraId = -1;// 当前相机id
    private int mLastOpenedCameraId = -1;
    private Camera mCameraDevice;
//    private SurfaceView mSurface;
    private int mCameraState = STATE_PREVIEW_STOP;
    private OnClickListener mZoomClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int maxZoom = mCameraDevice.getParameters().getMaxZoom();
            int currentZoom = mCameraDevice.getParameters().getZoom();
//            int zoomStep = maxZoom / 5;
            int zoomStep = 1;
            boolean changed = false;
            if (view == mPlusButton && currentZoom + zoomStep <= maxZoom) {
                currentZoom += zoomStep;
                changed = true;
            } else if (view == mMinusButton && currentZoom - zoomStep >= 0) {
                currentZoom -= zoomStep;
                changed = true;
            }
            if (changed) {
                Parameters para = mCameraDevice.getParameters();
                para.setZoom(currentZoom);
                mCameraDevice.setParameters(para);
            }
        }
    };
    private LocationListener mLocListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(MainActivity.this, "onLocationChanged:" + "longitude:" + location.getLongitude() + ", latitude:" + location.getLatitude() + ", altitude:" + location.getAltitude(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Toast.makeText(MainActivity.this, "onStatusChanged:" + s, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(MainActivity.this, "onProviderEnabled:" + s, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(MainActivity.this, "onProviderDisabled:" + s, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        getCamera();
        openCamera();
        initCameraPara();
        setContentView(R.layout.activity_main);
        //set preview
        ViewGroup previewContainer = (ViewGroup) findViewById(R.id.preview_container);
        mPreview = new CameraPreview(this, mCameraDevice);
        previewContainer.addView(mPreview);
        //
        mTakeButton = (Button) findViewById(R.id.take_button);
        mTakeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mCameraDevice.takePicture(shutterCallback, rawCallback, jpegCallback);
            }

        });

        mTakeButton.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mCameraDevice.autoFocus(new AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        Log.d("test", "focus:" + b);
                    }
                });
                return true;
            }
        });

        mPlusButton = (Button) findViewById(R.id.plus);
        mMinusButton = (Button) findViewById(R.id.minus);
        mPlusButton.setOnClickListener(mZoomClick);
        mMinusButton.setOnClickListener(mZoomClick);

        mInfoButton = (Button) findViewById(R.id.show_info);
        mInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showHideCurrentInfo();
            }
        });

        mMoreInfoButton = (Button) findViewById(R.id.show_more_info);
        mMoreInfoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCameraDevice != null) {
                    ShowInfoActivity.startInfoActivity(MainActivity.this, Utils.getSystemInfo(MainActivity.this) + Utils.getSupportParametersInfo(mCameraDevice.getParameters()));
                }
            }
        });

        mInfoPanel = (TextView) findViewById(R.id.info_panel);

        mClipView = (ClipView) findViewById(R.id.clip_view);
        mClipView.setReferenceView(mPreview);
        mClipShowCheck = (CheckBox) findViewById(R.id.clip_check);
        mClipShowCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mClipView.setVisibility(View.VISIBLE);
                }else{
                    mClipView.setVisibility(View.GONE);
                }
            }
        });

        mPictureSpinner = (Spinner) findViewById(R.id.picture_spinner);
        mPreviewSpinner = (Spinner) findViewById(R.id.preview_spinner);
        mPreviewModeSpinner = (Spinner) findViewById(R.id.preview_mode_spinner);
        initSpinner();

        mMatchPictureButton = (Button) findViewById(R.id.match_button);
        mMatchPictureButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int sizeIndex = Utils.getPictureMatchingIndex(mCameraDevice);
                if(sizeIndex != -1){
                    mPreviewSpinner.setSelection(sizeIndex);
                }else{
                    Toast.makeText(MainActivity.this, "no supportPreviewSize find!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSwitchButton = (Button) findViewById(R.id.switch_camera);
        mSwitchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        // set Location
//        LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocListener);
//        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraDevice == null) {
            openCamera();
            mPreview.setCameraDevice(mCameraDevice);
            initCameraPara();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPreview();
        closeCamera();
    }

    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 获取相机id
     */
    private void getCamera() {
        int count = Camera.getNumberOfCameras();
        for (int i = 0; i < count; i++) {
            CameraInfo cInfo = new CameraInfo();
            Camera.getCameraInfo(i, cInfo);
            if (cInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                mFrontCameraId = i;
            } else if (cInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                mBackCameraId = i;
            }
        }
    }

    private void openCamera() {
        if (mCurrentCameraId == -1) {
            int cameraId;
            if(mLastOpenedCameraId != -1){
                cameraId = mLastOpenedCameraId;
            }else if(mBackCameraId != -1){
                cameraId = mBackCameraId;
            }else if(mFrontCameraId != -1){
                cameraId = mFrontCameraId;
            }else {
                throw new RuntimeException("no camera found!");
            }
            mCameraDevice = Camera.open(cameraId);
            mCurrentCameraId = cameraId;
        }
    }

    private void openCamera(int cameraId){
        mCameraDevice = Camera.open(cameraId);
        mCurrentCameraId = cameraId;
    }

    private void initCameraPara() {
        Parameters para = mCameraDevice.getParameters();
        //设置合适的预览大小
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        Size size = Utils.getOptimalPreviewSize(mCameraDevice.getParameters().getSupportedPreviewSizes(), height, width);
//		Size size = Utils.getOptimalPreviewSize(this, mCameraDevice.getParameters().getSupportedPreviewSizes(), height / width);
        para.setPreviewSize(size.width, size.height);
        //设置旋转方向
        Utils.setCameraDisplayOrientation(this, mCurrentCameraId, mCameraDevice);
        //设置图片大小
        List<Size> supportedPictureSizes = para.getSupportedPictureSizes();
        Size pictureSize = supportedPictureSizes.get(supportedPictureSizes.size() - 2);
        para.setPictureSize(pictureSize.width, pictureSize.height);
        //
        mCameraDevice.setParameters(para);
    }

    private void stopPreview() {
        if (mCameraDevice != null && mCameraState != STATE_PREVIEW_STOP) {
            mCameraDevice.cancelAutoFocus();
            mCameraDevice.stopPreview();
            mCameraState = STATE_PREVIEW_STOP;
        }
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.release();
            mCameraDevice = null;
            mCameraState = STATE_PREVIEW_STOP;
            mCurrentCameraId = -1;
        }
    }

    private void initSpinner(){
        // set spinner
        List<Size> supportedPictureSizes = mCameraDevice.getParameters().getSupportedPictureSizes();
        List<CharSequence> supportedPictureSizesStr = new ArrayList<CharSequence>();
        Size currentPictureSize = mCameraDevice.getParameters().getPictureSize();
        int currentPictureIndex = 0;
        for(int i = 0; i < supportedPictureSizes.size();i++){
            Size size = supportedPictureSizes.get(i);
            String str = StringUtils.oToString(size);
            if(str.equals(StringUtils.oToString(currentPictureSize))){
                currentPictureIndex = i;
            }
            supportedPictureSizesStr.add(str);
        }

        ArrayAdapter<CharSequence> pictureAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, supportedPictureSizesStr);
        pictureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPictureSpinner.setAdapter(pictureAdapter);
        mPictureSpinner.setSelection(currentPictureIndex);
        mPictureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Parameters para = mCameraDevice.getParameters();
                Size selectedSize = para.getSupportedPictureSizes().get(i);
                para.setPictureSize(selectedSize.width, selectedSize.height);
                mCameraDevice.setParameters(para);
                mClipView.setClipRate((float)selectedSize.width / selectedSize.height);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<Size> supportedPreviewSizes = mCameraDevice.getParameters().getSupportedPreviewSizes();
        List<CharSequence> supportedPreviewSizesStr = new ArrayList<CharSequence>();
        Size currentPreviewSize = mCameraDevice.getParameters().getPreviewSize();
        int currentPreviewIndex = 0;
        for(int i = 0; i < supportedPreviewSizes.size();i++){
            Size size = supportedPreviewSizes.get(i);
            String str = StringUtils.oToString(size);
            if(str.equals(StringUtils.oToString(currentPreviewSize))){
                currentPreviewIndex = i;
            }
            supportedPreviewSizesStr.add(str);
        }

        ArrayAdapter<CharSequence> previewAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, supportedPreviewSizesStr);
        previewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPreviewSpinner.setAdapter(previewAdapter);
        mPreviewSpinner.setSelection(currentPreviewIndex);
        mPreviewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Parameters para = mCameraDevice.getParameters();
                Size selectedSize = para.getSupportedPreviewSizes().get(i);
                para.setPreviewSize(selectedSize.width, selectedSize.height);
                mCameraDevice.stopPreview();
                mCameraDevice.setParameters(para);
                mCameraDevice.startPreview();
                mPreview.requestLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<CharSequence> previewModeList = new ArrayList<CharSequence>();
        previewModeList.add("MEASURE_STATE_FIT_XY");
        previewModeList.add("MEASURE_STATE_ORIGINAL_SIZE");
        previewModeList.add("MEASURE_STATE_SCALE_SHORT");
        previewModeList.add("MEASURE_STATE_SCALE_LONG");

        ArrayAdapter<CharSequence> previewModeAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, previewModeList);
        previewModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPreviewModeSpinner.setAdapter(previewModeAdapter);
        mPreviewModeSpinner.setSelection(mPreview.getMeasureMode());
        mPreviewModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPreview.setMeasureMode(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void switchCamera(){
        if(mBackCameraId != -1 && mFrontCameraId != -1){
            int cameraToOpen = mCurrentCameraId == mBackCameraId ? mFrontCameraId : mBackCameraId;
            stopPreview();
            closeCamera();
            openCamera(cameraToOpen);
            mPreview.setCameraDevice(mCameraDevice);
            initCameraPara();
            initSpinner();
            mPreview.startPreview();
        }else{
            Toast.makeText(this, "just have one camera!", Toast.LENGTH_SHORT).show();
        }
    }


    private void showHideCurrentInfo(){
        if(mInfoPanel.getVisibility() == View.GONE){
            if(mCameraDevice != null){
                StringBuilder sb = new StringBuilder();
                Parameters para = mCameraDevice.getParameters();
                Size previewSize = para.getPreviewSize();
                sb.append("previewSize:" + StringUtils.oToString(previewSize));
                sb.append("\n\n");
                para.getPictureSize();
                Size pictureSize = para.getPictureSize();
                sb.append("pictureSize:" + StringUtils.oToString(pictureSize));
                sb.append("\n\n");
                sb.append("surfaceView:" + StringUtils.oToString(mPreview));
                sb.append("\n\n");
                sb.append("container:" + StringUtils.oToString(mPreview.getParent()));
                sb.append("\n\n");
                sb.append("max:" + StringUtils.oToString(Utils.getMaxNormalPreviewSize(mCameraDevice)));
                sb.append("\n\n");
                sb.append("isPreviewMatchingAllPictureSize:" + Utils.isPreviewMatchingAllPictureSize(mCameraDevice));
                sb.append("\n\n");
                mInfoPanel.setText(sb.toString());
            }
            mInfoPanel.setVisibility(View.VISIBLE);
        }else{
            mInfoPanel.setVisibility(View.GONE);
        }
    }

}
