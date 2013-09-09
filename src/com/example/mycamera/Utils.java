package com.example.mycamera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import java.util.List;

public class Utils {

    public static String getSystemInfo(Context context) {
        StringBuffer sb = new StringBuffer();
        sb.append("------------------System Info------------------\n");
        //screen
        sb.append("[");
        float width = context.getResources().getDisplayMetrics().widthPixels;
        float height = context.getResources().getDisplayMetrics().heightPixels;
        float density = context.getResources().getDisplayMetrics().density;
        float densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        sb.append("screen:width:" + width + ", height:" + height + ", density:" + density + ", densityDpi:" + densityDpi);
        sb.append("]");
        sb.append("\n\n");
        //File
        sb.append("[");
        String fileDir = context.getFilesDir().getAbsolutePath();
        String cache = context.getCacheDir().getAbsolutePath();
        String externalCache = context.getExternalCacheDir().getAbsolutePath();
        sb.append("getFilesDir:" + fileDir + ", getCacheDir:" + cache + ", getExternalCacheDir:" + externalCache + "");
        sb.append("]");
        sb.append("\n\n");
        //Enviorment
        sb.append("[");
        String esd = Environment.getExternalStorageDirectory().getAbsolutePath();
        String espd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        sb.append("Enviroment.getExternalStorageDirectory:" + esd + ", Enviroment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES):" + espd);
        sb.append("]");
        sb.append("\n");
        sb.append("------------------System Info------------------\n\n");
        return sb.toString();
    }

    public static String getSupportParametersInfo(Camera.Parameters para) {

        List<String> supportedAntibanding = para.getSupportedAntibanding();
        List<String> supportedColorEffects = para.getSupportedColorEffects();
        List<String> supportedFlashModes = para.getSupportedFlashModes();
        List<String> supportedFocusModes = para.getSupportedFocusModes();
        List<Size> supportedJpegThumbnailSizes = para.getSupportedJpegThumbnailSizes();
        List<Integer> supportedPictureFormats = para.getSupportedPictureFormats();
        List<Size> supportedPictureSizes = para.getSupportedPictureSizes();
        List<Integer> supportedPreviewFormats = para.getSupportedPreviewFormats();
        List<int[]> supportedPreviewFpsRange = para.getSupportedPreviewFpsRange();
        List<Size> supportedPreviewSizes = para.getSupportedPreviewSizes();
        List<String> supportedSceneModes = para.getSupportedSceneModes();
        List<Size> supportedVideoSizes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            supportedVideoSizes = para.getSupportedVideoSizes();
        }
        List<String> supportedWhiteBalance = para.getSupportedWhiteBalance();
        List<Integer> supportedPreviewFrameRates = para.getSupportedPreviewFrameRates();

        StringBuffer sb = new StringBuffer();
        sb.append("------------------Camera Parameter------------------\n");
        sb.append("[supportedAntibanding:" + listToString(supportedAntibanding) + "]\n\n");
        sb.append("[supportedColorEffects:" + listToString(supportedColorEffects) + "]\n\n");
        sb.append("[supportedFlashModes:" + listToString(supportedFlashModes) + "]\n\n");
        sb.append("[supportedFocusModes:" + listToString(supportedFocusModes) + "]\n\n");
        sb.append("[supportedJpegThumbnailSizes:" + listToString(supportedJpegThumbnailSizes) + "]\n\n");
        sb.append("[supportedPictureFormats:" + listToString(supportedPictureFormats) + "]\n\n");
        sb.append("[supportedPreviewFpsRange:" + listToString(supportedPreviewFpsRange) + "]\n\n");
        sb.append("[supportedPictureSizes:" + listToString(supportedPictureSizes) + "]\n\n");
        sb.append("[supportedPreviewFormats:" + listToString(supportedPreviewFormats) + "]\n\n");
        sb.append("[supportedPreviewSizes:" + listToString(supportedPreviewSizes) + "]\n\n");
        sb.append("[supportedSceneModes:" + listToString(supportedSceneModes) + "]\n\n");
        sb.append("[supportedVideoSizes:" + listToString(supportedVideoSizes) + "]\n\n");
        sb.append("[supportedWhiteBalance:" + listToString(supportedWhiteBalance) + "]\n\n");
        sb.append("[supportedPreviewFrameRates:" + listToString(supportedPreviewFrameRates) + "]\n\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            sb.append("[getMaxNumMeteringAreas:" + para.getMaxNumMeteringAreas() + "]\n\n");
            sb.append("[getMaxNumFocusAreas:" + para.getMaxNumFocusAreas() + "]\n\n");
            sb.append("[getMaxNumDetectedFaces:" + para.getMaxNumDetectedFaces() + "]\n\n");
        }
        sb.append("[getMaxZoom:" + para.getMaxZoom() + "]\n\n");
        sb.append("[getMaxExposureCompensation:" + para.getMaxExposureCompensation() + "]\n");
        sb.append("------------------Camera Parameter------------------\n\n");

        return sb.toString();
    }

    private static String listToString(List list) {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        if (list != null) {
            for (Object o : list) {
                sb.append(StringUtils.oToString(o));
                sb.append("|");
            }
        } else {
            sb.append("null");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 根据窗口大小获取合适的预览大小(copy from Google source)
     *
     * @param currentActivity
     * @param sizes           supportedSizes
     * @param targetRatio
     * @return
     */
    public static Size getOptimalPreviewSize(Activity currentActivity,
                                             List<Size> sizes, double targetRatio) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of mSurfaceView. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size

        Display display = currentActivity.getWindowManager()
                .getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());

        if (targetHeight <= 0) {
            // We don't know the size of SurfaceView, use screen height
            targetHeight = display.getHeight();
        }

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d("test", "getOptimalPreviewSize, width:" + optimalSize.width + "|height:" + optimalSize.height);
        return optimalSize;
    }

    public static Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d("test", "getOptimalPreviewSize,request:" + w + "|" + h + ",optimal:" + optimalSize.width + "|" + optimalSize.height);
        return optimalSize;
    }

    /**
     * 根据当前屏幕方向旋转相机的预览方向(copy from Google doc)
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void getPara(Camera.Parameters para) {
    }
}
