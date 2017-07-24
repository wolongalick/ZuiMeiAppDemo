package com.alick.zuimeiapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 *
 */
class CustomHorizontalScrollView extends HorizontalScrollView{
    private static final String TAG=CustomHorizontalScrollView.class.getSimpleName();


    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i(TAG,"--->dispatchTouchEvent()");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.i(TAG,"--->onInterceptTouchEvent()");
        return false;
    }

}
