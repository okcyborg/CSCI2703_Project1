package com.mykrobb2020.presshere.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.mykrobb2020.presshere.R;
import com.mykrobb2020.presshere.activities.MainActivity;

import java.math.BigDecimal;

/**
 * Created by Mike on 9/16/2015.
 */
public class MyView extends View {

    private ArcCoordinates mArcCoordinates;
    private boolean resettingCircle;
    private int centerX;
    private int centerY;
    private float lengthOfTime = 1.0f;
    private float arcIncrement = .24f;
    private float timeElapsed;

    private Runnable clockwiseArcAnimator = new Runnable() {
        @Override
        public void run() {
            if (timeElapsed >= 14960) {
                int x = 1;
                x+= 9;
            }
            mArcCoordinates.increment();
            timeElapsed+=40f;
            boolean needNewFrame = mArcCoordinates.getArcCoordinate() < ArcCoordinates.MAX_COORDINATE;
            if (needNewFrame) {
                postDelayed(this, 40);
            } else {
                ((MainActivity)getContext()).performPostHoldCompletionSteps();
            }

            invalidate();
        }
    };

    private Runnable counterClockwiseArcAnimator = new Runnable() {
        @Override
        public void run() {
            mArcCoordinates.decrement();
            boolean needNewFrame = mArcCoordinates.getArcCoordinate() > 0;
            if (needNewFrame) {
                postDelayed(this, 1);
            } else {
                resettingCircle = false;
                ((MainActivity)getContext()).toggleTextViewVisiblity();
            }
            invalidate();
        }
    };

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyView(Context context, AttributeSet attributeSet, int defStyleAttribute) {
        super(context, attributeSet, defStyleAttribute);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        centerX = getWidth()/2;
        centerY = getHeight()/2;

        //todo add 30 sec option

//        drawBackground(canvas);
//        drawArc(canvas);
        drawArcV2(canvas);
    }

    private void drawBackground(Canvas canvas) {
        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#c4c4c4"));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(50);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX, centerY, centerX - 25, paint);
    }

    private void drawArc(Canvas canvas) {
        if (mArcCoordinates != null) {
            Paint paint = new Paint();

            paint.setAntiAlias(true);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.circle_base));
            paint.setStrokeWidth(50);
            final RectF oval = new RectF();
            paint.setStyle(Paint.Style.STROKE);
            oval.set(25, 25, getWidth() - 25, getHeight() - 25);
            canvas.drawArc(oval, 270, (float)mArcCoordinates.getArcCoordinate(), false, paint);
        }
    }

    private void drawArcV2(Canvas canvas) {
        float secondSweep = 360.0f;
        float secondStartAngle = 270.0f;
        float strokeWidth = 50.0f;
        float ovalOffset = strokeWidth/2 + 2;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        final RectF oval = new RectF();
        oval.set(ovalOffset, ovalOffset, getWidth() - ovalOffset, getHeight() - ovalOffset);

        if (mArcCoordinates != null) {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.circle_fill));
            canvas.drawArc(oval, 270, (float)mArcCoordinates.getArcCoordinate(), false, paint);
            secondStartAngle = 270.0f + (float)mArcCoordinates.getArcCoordinate();
            secondSweep = 360.0f - (float)mArcCoordinates.getArcCoordinate();
        }

        paint.setColor(ContextCompat.getColor(getContext(), R.color.circle_base));
        canvas.drawArc(oval, secondStartAngle, secondSweep, false, paint);
    }

    public void startDrawingArc() {
        timeElapsed = 0f;
        arcIncrement = 360.f/((lengthOfTime * 60 * 1000)/40);

        mArcCoordinates = new ArcCoordinates();
        removeCallbacks(clockwiseArcAnimator);
        postDelayed(clockwiseArcAnimator, 40);
        ((MainActivity)getContext()).startColorAnimation((long) (lengthOfTime * 60 * 1000));
    }

    public void stopDrawingArc() {
        resettingCircle = true;
        removeCallbacks(clockwiseArcAnimator);
        postDelayed(counterClockwiseArcAnimator, 40);
        ((MainActivity)getContext()).startColorAnimation((long) (mArcCoordinates.getArcCoordinate()/8) * 8);
    }

    public boolean isResettingCircle() {
        return resettingCircle;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }

    public void setLengthOfTime(float lengthOfTime) {
        this.lengthOfTime = lengthOfTime;
    }

    public class ArcCoordinates {
        private double arcCoordinate;
        public static final int MAX_COORDINATE = 360;

        public void setArcCoordinate(float arcCoordinate) {
            this.arcCoordinate = arcCoordinate;
        }

        public void increment() {
            if (arcIncrement < MAX_COORDINATE - arcCoordinate) {
                arcCoordinate += arcIncrement;
            } else {
                arcCoordinate = MAX_COORDINATE;
            }
        }
        public void decrement() {
            if (arcCoordinate > 8) {
                arcCoordinate = arcCoordinate - 8;
            } else {
                arcCoordinate = 0;
            }
        }

        public double getArcCoordinate() {
            return arcCoordinate;
        }
    }


}
