package com.jianjian.largepictureview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author jian
 * @version 1.0
 * @date 2019/7/3 20:27
 */
public class LargeBitmapView extends View implements GestureDetector.OnGestureListener {

    private BitmapRegionDecoder mBitmapRegionDecoder;
    private GestureDetector mGestureDetector;
    private int mResId;
    private int mPictureWidth;
    private int mPictureHeight;

    private int mLastX;
    private int mLastY;

    private Rect mRect;
    private BitmapFactory.Options mOptions;

    public LargeBitmapView(Context context) {
        this(context, null);
    }

    public LargeBitmapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LargeBitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs,R.styleable.LargeBitmapView);
        mResId = array.getResourceId(R.styleable.LargeBitmapView_ResId,0);
        array.recycle();
        mRect = new Rect();
        mOptions = new BitmapFactory.Options();
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mGestureDetector = new GestureDetector(getContext(), this);
        try {
            InputStream is = getContext().getResources().openRawResource(mResId);
            mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getContext().getResources(), mResId, options);
        mPictureWidth = options.outWidth;
        mPictureHeight = options.outHeight;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mLastX = (int) e.getRawX();
        mLastY = (int) e.getRawY();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int x = (int) e2.getRawX();
        int y = (int) e2.getRawY();
        move(x, y);
        return true;
    }

    private void move(int x, int y) {
        int deltaX = x - mLastX;
        int deltaY = y - mLastY;
        if (mPictureWidth > getWidth()) {
            mRect.offset(-deltaX, 0);
            if (mRect.right > mPictureWidth) {
                mRect.right = mPictureWidth;
                mRect.left = mPictureWidth - getWidth();
            }
            if (mRect.left < 0) {
                mRect.left = 0;
                mRect.right = getWidth();
            }
        }
        if (mPictureHeight > getHeight()) {
            mRect.offset(0, -deltaY);
            if (mRect.top<0){
                mRect.top = 0;
                mRect.bottom = getHeight();
            }
            if (mRect.bottom>mPictureHeight){
                mRect.bottom = mPictureHeight;
                mRect.top = mPictureHeight - getHeight();
            }
        }
        invalidate();
        mLastX = x;
        mLastY = y;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int x = (int) e2.getRawX();
        int y = (int) e2.getRawY();
        move(x, y);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bm = mBitmapRegionDecoder.decodeRegion(mRect, mOptions);
        canvas.drawBitmap(bm,0,0,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mRect.top = mPictureHeight/2 - height/2;
        mRect.bottom = mRect.top + height;
        mRect.left = mPictureWidth/2 - width/2;
        mRect.right = mRect.left + width;
    }
}
