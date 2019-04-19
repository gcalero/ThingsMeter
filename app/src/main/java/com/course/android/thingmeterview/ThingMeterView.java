package com.course.android.thingmeterview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ThingMeterView extends View {

    private static final String TAG = ThingMeterView.class.getName();

    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_MARK_COLOR = Color.GREEN;
    private static final int DEFAULT_INDICATOR_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;

    private static final float DEFAULT_BORDER_WIDTH = 4.0f;
    private static final float DEFAULT_MARK_STROKE_WIDTH = 1f;
    private static final float DEFAULT_INDICATOR_STROKE_WIDTH = 4f;

    private static final float PROPORTION_VERTICAL_MARGIN = 0.15f;

    private static final float DEFAULT_MIN_VALUE = 0f;
    private static final float DEFAULT_MAX_VALUE = 100f;
    private static final int DEFAULT_TEXT_SIZE = 80;
    private static final int DEFAULT_MARK_PARTS = 10;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final double ANGLE_MIN= Math.PI / 5;
    private static final double ANGLE_MAX = Math.PI * 4 / 5;

    private int mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mMarkColor = Color.BLUE;
    private int mIndicatorColor = DEFAULT_INDICATOR_COLOR;
    private int mTextColor = DEFAULT_TEXT_COLOR;

    private float mBorderWidth = DEFAULT_BORDER_WIDTH;
    private float mMarkWidth = DEFAULT_MARK_STROKE_WIDTH;
    private float mIndicatorWidth = DEFAULT_INDICATOR_STROKE_WIDTH;

    private int mWidth = 320;
    private int mHeight = 200;

    private float mMinValue = DEFAULT_MIN_VALUE;
    private float mMaxValue = DEFAULT_MAX_VALUE;
    private float mValue = DEFAULT_MIN_VALUE;
    private String mLabel = "";
    private Typeface mTypeface;
    private int mFontId;
    private int mTextSize = DEFAULT_TEXT_SIZE;
    private int mMarkParts = DEFAULT_MARK_PARTS;


    public ThingMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupAttrs(attrs);
    }



    private void setupAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ThingMeterView, 0, 0);
        mBackgroundColor = typedArray.getColor(R.styleable.ThingMeterView_backgroundColor, DEFAULT_BACKGROUND_COLOR);
        mBorderColor = typedArray.getColor(R.styleable.ThingMeterView_borderColor, DEFAULT_BORDER_COLOR);
        mMarkColor = typedArray.getColor(R.styleable.ThingMeterView_markColor, DEFAULT_MARK_COLOR);
        mIndicatorColor = typedArray.getColor(R.styleable.ThingMeterView_indicatorColor, DEFAULT_INDICATOR_COLOR);

        mBorderWidth = typedArray.getDimension(R.styleable.ThingMeterView_borderWidth, DEFAULT_BORDER_WIDTH);
        mMarkWidth = typedArray.getDimension(R.styleable.ThingMeterView_markWidth, DEFAULT_MARK_STROKE_WIDTH);
        mIndicatorWidth = typedArray.getDimension(R.styleable.ThingMeterView_indicatorWidth, DEFAULT_INDICATOR_STROKE_WIDTH);

        mMinValue = typedArray.getFloat(R.styleable.ThingMeterView_minValue, DEFAULT_MIN_VALUE);
        mMaxValue = typedArray.getFloat(R.styleable.ThingMeterView_maxValue, DEFAULT_MAX_VALUE);


        int fontFamily = typedArray.getResourceId(R.styleable.ThingMeterView_font, 0);
        if (fontFamily > 0) {
            setFont(fontFamily);
        }

        mValue = typedArray.getFloat(R.styleable.ThingMeterView_value, mMinValue);
        mLabel = typedArray.getString(R.styleable.ThingMeterView_labelText);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.ThingMeterView_textSize, DEFAULT_TEXT_SIZE);
        mTextColor = typedArray.getColor(R.styleable.ThingMeterView_textColor, DEFAULT_TEXT_COLOR);
        mMarkParts = typedArray.getInt(R.styleable.ThingMeterView_markParts, DEFAULT_MARK_PARTS);
Log.d(TAG, "Mark parts: " + mMarkParts);
        typedArray.recycle();
        invalidate();
    }


    public void setMeterBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable p = super.onSaveInstanceState();
        SavedState ss = new SavedState(p);
        ss.borderColor = mBorderColor;
        ss.backgroundColor = mBackgroundColor;
        ss.markColor = mMarkColor;
        ss.indicatorColor = mIndicatorColor;
        ss.textColor = mTextColor;

        ss.borderWidth = mBorderWidth;
        ss.markWidth = mMarkWidth;
        ss.indicatorWidth = mIndicatorWidth;

        ss.width = mWidth;
        ss.height = mHeight;
        ss.minValue = mMinValue;
        ss.maxValue = mMaxValue;
        ss.value = mValue;
        ss.textSize = mTextSize;
        ss.fontId = mFontId;
        ss.markParts = mMarkParts;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (! (state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mBackgroundColor = ss.backgroundColor;
        mBorderColor = ss.borderColor;
        mMarkColor = ss.markColor;
        mIndicatorColor = ss.indicatorColor;
        mTextColor = ss.textColor;

        mBorderWidth = ss.borderWidth;
        mMarkWidth = ss.markWidth;
        mIndicatorWidth = ss.indicatorWidth;

        mWidth = ss.width;
        mHeight = ss.height;

        mMinValue = ss.minValue;
        mMaxValue = ss.maxValue;
        mValue = ss.value;

        mTextSize = ss.textSize;
        setFont(ss.fontId);
        mMarkParts = ss.markParts;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
        drawMark(canvas);
        drawIndicator(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = Math.min (getMeasuredWidth(), getMeasuredHeight());
        mHeight = mWidth;
        setMeasuredDimension(mWidth, mHeight);
    }

    private void drawBackground(Canvas canvas) {
        Rect r = new Rect(0, 0, mWidth, mHeight);

        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(r, mPaint);

        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        canvas.drawRect(mBorderWidth, mBorderWidth, mWidth - mBorderWidth, mHeight - mBorderWidth, mPaint);
    }

    private void drawMark(Canvas canvas) {
        mPaint.setColor(mMarkColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mMarkWidth);

        float startAngle = (float) (Math.toDegrees(ANGLE_MIN + Math.PI));
        float sweepAngle = (float) (Math.toDegrees(ANGLE_MAX - ANGLE_MIN));
        int arcTopY = mHeight / 2;
        RectF ovalRect = new RectF(0, arcTopY, mWidth, mHeight * 3 / 2);

        canvas.drawArc(ovalRect, startAngle, sweepAngle, false, mPaint);

        float cx = mWidth / 2f;
        float cy = mHeight;
        // draw a scale
        float angle = startAngle - 5;
        float stepAngle = sweepAngle / (float) mMarkParts;
        float stepValue = (getMaxValue() - getMinValue()) / mMarkParts;
        for (int i = 0; i <= mMarkParts; i++) {
            mPaint.setColor(mIndicatorColor);
            mPaint.setStrokeWidth(mIndicatorWidth);
            double angleV = ANGLE_MIN + (i / (float) mMarkParts ) * (ANGLE_MAX - ANGLE_MIN);
            drawRadialLine(canvas, cx, cy, angleV, arcTopY, (float) (arcTopY + mHeight * 0.05), mPaint);

            // Write min and max value
            mPaint.setTypeface(mTypeface);
            mPaint.setTextSize(25);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            Path path = new Path();
            path.arcTo(ovalRect, angle, stepAngle);
            float value;
            if (i < mMarkParts) {
                value = getMinValue() + i * stepValue;
            } else {
                value = getMaxValue();
            }
            canvas.drawTextOnPath(String.valueOf(value), path, 0, (float) (-mHeight * 0.075), mPaint);
            angle += stepAngle;
        }

        // Write label
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mLabel, cx, (float) (cy - mHeight * 0.05), mPaint);

    }

    private void drawIndicator(Canvas canvas) {
        float length = Math.min(mWidth / 2, mHeight / 2) - mMarkWidth; // max length of the indicator
        float cx = mWidth / 2f;
        float cy = mHeight;
        double angleV = ANGLE_MIN + (mValue - mMinValue) / (mMaxValue - mMinValue) * (ANGLE_MAX - ANGLE_MIN);

        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mIndicatorWidth);

        drawRadialLine(canvas, cx, cy, angleV, mWidth * PROPORTION_VERTICAL_MARGIN, length, mPaint);
    }

    private void drawRadialLine(Canvas canvas, float cx, float cy, double angle, float inRadius, float outRadius, Paint paint) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        float vx1 = (float) (cx - inRadius * cos);
        float vy1 = (float) (cy - inRadius * sin);
        float vx2 = (float) (cx - outRadius * cos);
        float vy2 = (float) (cy - outRadius * sin);

        canvas.drawLine(vx1, vy1, vx2, vy2, paint);
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(float value) {
        mValue = value;
        invalidate();
    }

    public float getMinValue() {
        return mMinValue;
    }

    public void setMinValue(float value) {
        mMinValue = value;
        invalidate();
    }

    public float getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(float value) {
        mMaxValue = value;
        invalidate();
    }

    public void setFont(int fontId) {
        mFontId = fontId;
        mTypeface = ResourcesCompat.getFont(getContext(), fontId);
    }

    static class SavedState extends BaseSavedState {
        private int backgroundColor;
        private int borderColor;
        private int markColor;
        private int indicatorColor;
        private int textColor;

        private float borderWidth;
        private float markWidth;
        private float indicatorWidth;

        public float minValue;
        public float maxValue;
        public float value;

        private int width;
        private int height;

        private int textSize;
        private int fontId;
        private int markParts;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            backgroundColor = in.readInt();
            borderColor = in.readInt();
            markColor = in.readInt();
            indicatorColor = in.readInt();
            textColor = in.readInt();

            borderWidth = in.readFloat();
            markWidth = in.readFloat();
            indicatorWidth = in.readFloat();

            width = in.readInt();
            height = in.readInt();

            minValue = in.readFloat();
            maxValue = in.readFloat();
            value = in.readFloat();

            textSize = in.readInt();
            fontId = in.readInt();
            markParts = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(backgroundColor);
            out.writeInt(borderColor);
            out.writeInt(markColor);
            out.writeInt(indicatorColor);
            out.writeInt(textColor);

            out.writeFloat(borderWidth);
            out.writeFloat(markWidth);
            out.writeFloat(indicatorWidth);

            out.writeInt(width);
            out.writeInt(height);

            out.writeFloat(minValue);
            out.writeFloat(maxValue);
            out.writeFloat(value);

            out.writeInt(textSize);
            out.writeInt(fontId);
            out.writeInt(markParts);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


}
