package com.example.mycamera.utils;

import android.hardware.Camera;
import android.view.View;

/**
 * Created by marui on 13-9-9.
 */
public class StringUtils {

    /**
     * 将对象格式化输出
     *
     * @param o
     * @return
     */
    public static String oToString(Object o) {
        if (o == null) return null;
        if (o instanceof Camera.Size) {
            Camera.Size size = (Camera.Size) o;
            String s = "width:" + size.width + ", height:" + size.height + ", rate:" + ((float) size.width / size.height);
            return s;
        } else if (o instanceof int[]) {
            StringBuilder sb = new StringBuilder();
            for (int i : (int[]) o) {
                sb.append(i).append(",");
            }
            return sb.toString();
        } else if (o instanceof View) {
            View v = (View) o;
            int width = v.getMeasuredWidth();
            int height = v.getMeasuredHeight();
            float rate = 0;
            if (width > height) {
                rate = (float) width / height;
            } else {
                rate = (float                      ) height / width;
            }
            String s = "width:" + width + ", height:" + height + ", rate:" + rate;
            return s;
        } else {
            return o.toString();
        }
    }
}
