package com.example.mycamera.view;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;

import static android.hardware.Camera.*;

/**
 * Created by marui on 13-9-9.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public static final int MEASURE_STATE_FIT_XY = 0;
    public static final int MEASURE_STATE_ORIGINAL_SIZE = 1;
    public static final int MEASURE_STATE_SCALE_SHORT = 2;
    public static final int MEASURE_STATE_SCALE_LONG = 3;
    private Camera mCameraDevice;

    public int getMeasureMode() {
        return mMeasureMode;
    }

    private int mMeasureMode = MEASURE_STATE_ORIGINAL_SIZE;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCameraDevice = camera;
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//在3.0以下必须使用这句
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2,
                               int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private void startPreview(SurfaceHolder holder) {
        if (mCameraDevice == null || holder == null) return;
//        if (mCameraState == STATE_PREVIEW_STOP) {
        try {
            mCameraDevice.setPreviewDisplay(holder);
            mCameraDevice.startPreview();
//                mCameraState = STATE_IDEL;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mCameraDevice != null) {
            if (mMeasureMode == MEASURE_STATE_FIT_XY) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else if (mMeasureMode == MEASURE_STATE_ORIGINAL_SIZE) {
                Camera.Size size = mCameraDevice.getParameters().getPreviewSize();
                setMeasuredDimension(MeasureSpec.makeMeasureSpec(size.height, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size.width, MeasureSpec.EXACTLY));
            } else if (mMeasureMode == MEASURE_STATE_SCALE_SHORT) {
                Camera.Size size = mCameraDevice.getParameters().getPreviewSize();
                float previewRate = (float) size.width / size.height;
                ViewGroup parent = (ViewGroup) getParent();
                int viewWidth = parent.getMeasuredWidth();
                int viewHeight = parent.getMeasuredHeight();
                boolean reverse = false;
                if (viewHeight > viewWidth) {
                    int temp = viewWidth;
                    viewWidth = viewHeight;
                    viewHeight = temp;
                    reverse = true;
                }
                float viewRate = (float) viewWidth / viewHeight;
                int scaleWidth;
                int scaleHeight;
                if (previewRate > viewRate) {
                    scaleWidth = viewWidth;
                    scaleHeight = (int) (viewWidth / previewRate);
                } else {
                    scaleHeight = viewHeight;
                    scaleWidth = (int) (viewHeight * previewRate);
                }
                if (reverse) {
                    setMeasuredDimension(MeasureSpec.makeMeasureSpec(scaleHeight, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(scaleWidth, MeasureSpec.EXACTLY));
                } else {
                    setMeasuredDimension(MeasureSpec.makeMeasureSpec(scaleWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(scaleHeight, MeasureSpec.EXACTLY));
                }
            } else if (mMeasureMode == MEASURE_STATE_SCALE_LONG) {
                Camera.Size size = mCameraDevice.getParameters().getPreviewSize();
                float previewRate = (float) size.width / size.height;
                ViewGroup parent = (ViewGroup) getParent();
                int viewWidth = parent.getMeasuredWidth();
                int viewHeight = parent.getMeasuredHeight();
                boolean reverse = false;
                if (viewHeight > viewWidth) {
                    int temp = viewWidth;
                    viewWidth = viewHeight;
                    viewHeight = temp;
                    reverse = true;
                }
                float viewRate = (float) viewWidth / viewHeight;
                int scaleWidth;
                int scaleHeight;
                if (previewRate < viewRate) {
                    scaleWidth = viewWidth;
                    scaleHeight = (int) (viewWidth / previewRate);
                } else {
                    scaleHeight = viewHeight;
                    scaleWidth = (int) (viewHeight * previewRate);
                }
                if (reverse) {
                    setMeasuredDimension(MeasureSpec.makeMeasureSpec(scaleHeight, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(scaleWidth, MeasureSpec.EXACTLY));
                } else {
                    setMeasuredDimension(MeasureSpec.makeMeasureSpec(scaleWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(scaleHeight, MeasureSpec.EXACTLY));
                }
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setMeasureMode(int mode) {
        if (mode >= MEASURE_STATE_FIT_XY && mode <= MEASURE_STATE_SCALE_LONG) {
            mMeasureMode = mode;
        } else {
            mMeasureMode = MEASURE_STATE_FIT_XY;
        }
        requestLayout();
    }

    public void setCameraDevice(Camera camera) {
        mCameraDevice = camera;
    }
}
