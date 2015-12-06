package com.mykrobb2020.presshere.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.mykrobb2020.presshere.R;

/**
 * Created by Mike on 10/22/2015.
 */
public class CircleAnimationView extends ImageView {

    private int mImageNumber;

    public CircleAnimationView(Context context) {
        super(context);
    }

    public CircleAnimationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CircleAnimationView(Context context, AttributeSet attributeSet, int defStyleAttribute) {
        super(context, attributeSet, defStyleAttribute);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mImageNumber == 0) {
            postDelayed(circleAnimator, 3000);
        }
    }

    private Runnable circleAnimator = new Runnable() {
        @Override
        public void run() {
            if (mImageNumber == 0) {
                setImageResource(R.drawable.blue_circle_finger);
            } else if (mImageNumber == 1) {
                setImageResource(R.drawable.yellow_fill1);
            } else if (mImageNumber == 2) {
                setImageResource(R.drawable.yellow_fill2);
            } else if (mImageNumber >= 3) {
                setImageResource(R.drawable.welcome_yellow_circle);
            }

            ++mImageNumber;

            if (mImageNumber < 4) {
                postDelayed(this, 1500);
            }

            invalidate();
        }
    };
}
