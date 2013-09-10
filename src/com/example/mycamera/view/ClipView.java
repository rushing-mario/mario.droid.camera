package com.example.mycamera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by marui on 13-9-10.
 */
public class ClipView extends View {

    private float mClipRate = 1;//默认认为横向
    private Paint mClipPaint;

    public void setReferenceView(View mReference) {
        this.mReference = mReference;
    }

    private View mReference;

    public ClipView(Context context) {
        this(context, null);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mClipPaint = new Paint();
        mClipPaint.setColor(0x802222aa);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mReference == null){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width = mReference.getMeasuredWidth();
        int height = mReference.getMeasuredHeight();
        if(width > 0 && height > 0){
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        boolean landscape = width >= height;
        float viewRate;
        if (landscape) {
            viewRate = (float) width / height;
        } else {
            viewRate = (float) height / width;
        }
        if (landscape) {
            //TODO 未实现横屏
        } else {
            if (mClipRate >= viewRate) {
                //图片width更大，更宽屏
                int clipWidth = (int) (height / mClipRate);
                int left = (width - clipWidth) / 2;
                int right = left + clipWidth;
                canvas.drawRect(0, 0, left, height, mClipPaint);
                canvas.drawRect(right, 0, width, height, mClipPaint);
            } else {
                //图片height更大
                int clipHeight = (int) (width * mClipRate);
                int top = (height - clipHeight) / 2;
                int bottom = top + clipHeight;
                canvas.drawRect(0, 0, width, top, mClipPaint);
                canvas.drawRect(0, bottom, width, height, mClipPaint);
            }
        }
    }

    public void setClipRate(float rate){
        mClipRate = rate;
        invalidate();;
    }
}
