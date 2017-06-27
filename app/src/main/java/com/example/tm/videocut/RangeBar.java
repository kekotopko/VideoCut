package com.example.tm.videocut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.tm.videocut.R;
import com.example.tm.videocut.ActivityUtil;

/**
 * Created by ruslan.kolbasa on 16.08.2016.
 * mobilebankingandroid
 */
public class RangeBar extends RelativeLayout implements View.OnTouchListener {


    public RangeBar(Context context) {
        super(context);
        init(context, null);
    }

    public RangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private static ShapeDrawable sOvalDrawable = new ShapeDrawable(new OvalShape());

    static {
        sOvalDrawable.getPaint().setColor(Color.BLACK);
    }

    private ImageView mFirstCircleView;
    private ImageView mSecondCircleView;
    private ImageView mPalkaCircleView;

    private float mLineStrokeWidth;
    private int mFullCircleViewSize;

    private void init(Context context, AttributeSet attrs) {
        // TODO: 16.08.2016 Do attrs from XML
        setWillNotDraw(false);

        int circleSize = (int) ActivityUtil.dpToPx(16f, context);

        mLineStrokeWidth = ActivityUtil.dpToPx(2f, context);

        int circlePadding = (int) ActivityUtil.dpToPx(6f, context);

        mFullCircleViewSize = circlePadding * 2 + circleSize;

        mFirstCircleView = new ImageView(context);
        mFirstCircleView.setOnTouchListener(this);
        mFirstCircleView.setImageResource(R.drawable.ic_circle_white_vector);
        mFirstCircleView.setPadding(circlePadding, circlePadding, circlePadding, circlePadding);

        addView(mFirstCircleView, mFullCircleViewSize, mFullCircleViewSize);

        mSecondCircleView = new ImageView(context);
        mSecondCircleView.setOnTouchListener(this);
        mSecondCircleView.setImageResource(R.drawable.ic_circle_white_vector);
        mSecondCircleView.setPadding(circlePadding, circlePadding, circlePadding, circlePadding);

        addView(mSecondCircleView, mFullCircleViewSize, mFullCircleViewSize);

        mPalkaCircleView = new ImageView(context);
        mPalkaCircleView.setOnTouchListener(this);
        mPalkaCircleView.setImageResource(R.drawable.wrench);
        mPalkaCircleView.setPadding(circlePadding, circlePadding, circlePadding, circlePadding);

        addView(mPalkaCircleView, mFullCircleViewSize, mFullCircleViewSize);

        resolveCircleViewsImagesVisibility();
        postRequestLayout();
    }

    private void resolveCircleViewsImagesVisibility(){
        mFirstCircleView.setVisibility(isEnabled() ? VISIBLE : INVISIBLE);
        mSecondCircleView.setVisibility(isEnabled() ? VISIBLE : INVISIBLE);
        mPalkaCircleView.setVisibility(isEnabled() ? VISIBLE : INVISIBLE);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        resolveCircleViewsImagesVisibility();
    }

    private void postRequestLayout() {
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    private int getLeftMarginForPosition(double position) {
        if (position == mValueMin) {
            return 0;
        }

        if (position == mValueMax) {
            return getWidth() - mFullCircleViewSize;
        }

        float x = getXForPosition(position);
        return (int) (x - (float) mFullCircleViewSize / 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        postRequestLayout();
    }

    @ColorInt
    private int mLineNoactiveColor = Color.parseColor("#000000");

    @ColorInt
    private int mLineActiveColor = Color.parseColor("#FF231C");

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LayoutParams firstCircleParams = (LayoutParams) mFirstCircleView.getLayoutParams();
        firstCircleParams.leftMargin = getLeftMarginForPosition(mCurrentStart);

        LayoutParams secontCircleParams = (LayoutParams) mSecondCircleView.getLayoutParams();
        secontCircleParams.leftMargin = getLeftMarginForPosition(mCurrentEnd);

        LayoutParams palkaParams = (LayoutParams)  mPalkaCircleView.getLayoutParams();




        super.onLayout(changed, l, t, r, b);
    }

    private float getXForPosition(double position) {
        float coef = (float) ((position - mValueMin) / (mValueMax - mValueMin));
        return ((float) getWidth() - (float) mFullCircleViewSize) * coef + ((float) mFullCircleViewSize / 2f);
    }

    private static final Paint PAINT = new Paint();

    private double mValueMin = 0;
    private double mValueMax = 100;
    private double mValuePalk = 0;

    private double mCurrentStart = 0;
    private double mCurrentPalk =0;
    private double mCurrentEnd = 1000;

    @Override
    protected void onDraw(Canvas canvas) {
        PAINT.setStrokeWidth(mLineStrokeWidth);

        float activeXStart = getXForPosition(mCurrentStart);
        float activeXEnd = getXForPosition(mCurrentEnd);

        float y = (float) getHeight() / 2f;

        PAINT.setColor(mLineNoactiveColor);
        canvas.drawLine(0, y, activeXStart, y,PAINT);

        PAINT.setColor(mLineActiveColor);
        canvas.drawLine(activeXStart, y, activeXEnd, y, PAINT);

        PAINT.setColor(mLineNoactiveColor);
        canvas.drawLine(activeXEnd, y, getWidth(), y, PAINT);

        super.onDraw(canvas);
    }

    private void onMove(View view, float dx) {
        float targetMarginLeft = dx - _xDelta;

        if (targetMarginLeft < 0) {
            targetMarginLeft = 0;
        }

        if (targetMarginLeft > getWidth() - mFullCircleViewSize) {
            targetMarginLeft = getWidth() - mFullCircleViewSize;
        }

        double value = mValueMin + (targetMarginLeft * getValueForOnePixel());

        if (view == mFirstCircleView) {
            mCurrentStart = value;
        } else if (view == mSecondCircleView) {
            mCurrentEnd = value;
        } else if (view == mPalkaCircleView) {
            mCurrentPalk = value;
        } else {
            throw new IllegalStateException();
        }

        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.leftMargin = (int) targetMarginLeft;
        view.setLayoutParams(params);

        if (mValueChangeListener != null) {
            mValueChangeListener.onSlide(mCurrentStart < mCurrentEnd ? mCurrentStart : mCurrentEnd,
                    mCurrentStart < mCurrentEnd ? mCurrentEnd : mCurrentStart);
        }
    }

    private float getValueForOnePixel() {
        return (float) (getDiapason() / ((float) getWidth() - mFullCircleViewSize));
    }

    private double getDiapason() {
        return mValueMax - mValueMin;
    }

    private float _xDelta;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final float x = event.getRawX();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                LayoutParams lParams = (LayoutParams) view.getLayoutParams();
                _xDelta = x - lParams.leftMargin;
                break;
            case MotionEvent.ACTION_UP:
                onMove(view, x);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(view, x);
                break;
        }

        invalidate();
        return true;


    }



    public interface ValueChangeListener {
        void onSlide(double start, double end);
    }


    private ValueChangeListener mValueChangeListener;

    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        this.mValueChangeListener = valueChangeListener;
    }

    public void setMax(double max) {
        mValueMax = max;
    }

    public void setMin(double min) {
        mValueMin = min;
    }
    public void setPalk(double palk) { mValuePalk= palk; }

    public void setSelection(double start, double end,double palka) {
        mCurrentStart = start;
        mCurrentPalk=palka;
        mCurrentEnd = end;
    }

    public double getCurentstart(){
        return mCurrentStart;
    }
    public double getCurentend(){
        return mCurrentEnd;
    }
    public double getmCurrentPalk(){
        return mCurrentPalk;
    }
}
