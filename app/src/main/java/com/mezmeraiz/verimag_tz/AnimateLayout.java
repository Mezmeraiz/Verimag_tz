package com.mezmeraiz.verimag_tz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by pc on 08.10.2016.
 */

public class AnimateLayout extends RelativeLayout implements View.OnTouchListener {
    private float mDx = 0;
    private float mDy = 0;

    public AnimateLayout(Context context) {
        super(context);
    }

    public AnimateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDx = getX() - event.getRawX();
                mDy = getY() - event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                animate()
                        .x(event.getRawX() + mDx)
                        .y(event.getRawY() + mDy)
                        .setDuration(0)
                        .start();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                return false;
        }
        return true;
    }
}
