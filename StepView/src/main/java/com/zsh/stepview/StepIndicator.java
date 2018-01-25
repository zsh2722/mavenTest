package com.zsh.stepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zsh.stepview.util.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zsh27
 * @date 2018/1/25
 * description .
 * @since 0
 */

public class StepIndicator extends View {
    public static final String TAG = "zsh StepIndicator";
    private Context mContext;
    private int default_step_length = 1; // 默认长度
    private int default_circle_select_color = Color.BLUE; // 默认选中圆的色值
    private int default_circle_unselect_color = Color.GRAY; // 默认未选中圆的色值
    private int default_text_select_color = Color.BLACK; // 默认选中文本的色值
    private int default_text_unselect_color = Color.GRAY; // 默认未选中文本的色值
    private int default_line_color = Color.GRAY; // 默认连接线的色值
    private int default_text_size = 12; // 默认字体大小
    private int default_indicator_margin = 3; // 默认顶部icon的margin值
    private int default_line_height = 2;// 默认连接线的高度
    private int default_selected_position = 0; // 默认选中选项
    private String[] labels = {"关闭", "标准", "灵敏"};
    private Paint mCirclePaint; // 圆的画笔
    private Paint mTextPaint; // 字体画笔
    private Paint mLinePaint; // 线画笔
    private int mStepLength; // 选项长度
    private int mCicleSelectColor; // 选中色值
    private int mCircleUnselectColor; // 未选中色值
    private int mTextSelectColor; // 选中色值
    private int mTextUnselectColor; // 未选中色值
    private float mTextSize; // 字体大小
    private float mIndicatorMargin; // 顶部icon的margin
    private float mLineHeight; // 线的高度
    private int mLineColor; // 线的色值
    private float mPaddingLeft; // padding值
    private Bitmap mIndicatorBmp; // 顶部icon Bitmap
    private int mCircleRadius; // 圆的半径
    private float mTextY; // 文本的Y轴
    private int mCenterY; // 中心Y轴
    private float mLeftX; // x轴起点
    private int mLeftY; // Y轴中心
    private float mRightX; // x轴终点
    private int mRightY; // Y轴中心
    private float mDelta; // 间隔线
    private List<Float> mXPosition = new ArrayList<>();
    private int mSelectedPosition = 0;
    private OnDrawListener mDrawListener;
    private int mPaddingRight;

    public StepIndicator(Context context) {
        super(context);
    }

    public StepIndicator(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepIndicator(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initPaint();
        initBitmap();
    }

    private void initBitmap() {
        mIndicatorBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_triangle_indicator);
    }

    private void initPaint() {
        // 圆
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setStrokeWidth(2);
        // 文字
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        // 连接线
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(mLineHeight);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StepIndicator);
            mStepLength = a.getInteger(R.styleable.StepIndicator_step_length, default_step_length);
            mCicleSelectColor = a.getColor(R.styleable.StepIndicator_circle_select_color, default_circle_select_color);
            mCircleUnselectColor = a.getColor(R.styleable.StepIndicator_circle_unselect_color, default_circle_unselect_color);
            mTextSelectColor = a.getColor(R.styleable.StepIndicator_text_select_color, default_text_select_color);
            mTextUnselectColor = a.getColor(R.styleable.StepIndicator_text_unselect_color, default_text_unselect_color);
            mTextSize = a.getDimension(R.styleable.StepIndicator_step_text_size, DensityUtils.sp2px(mContext, default_text_size));
            mIndicatorMargin = a.getDimension(R.styleable.StepIndicator_top_indicator_margin, DensityUtils.sp2px(mContext, default_indicator_margin));
            mLineHeight = a.getDimension(R.styleable.StepIndicator_line_height, default_line_height);
            mLineColor = a.getColor(R.styleable.StepIndicator_step_line_color, default_line_color);
            mSelectedPosition = a.getInteger(R.styleable.StepIndicator_selected_position, default_selected_position);
            a.recycle();
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    private void init() {
        mCircleRadius = DensityUtils.dip2px(mContext, 12);
        mPaddingLeft = DensityUtils.dip2px(mContext, 25);
        mPaddingRight = DensityUtils.dip2px(mContext, 30);
        //        mPaddingLeft = 0.7f * getHeight() / 2;
        mTextY = 0.8f * getHeight();
        mCenterY = getHeight() / 2 - mCircleRadius / 2;
        mLeftX = mPaddingLeft;
        mLeftY = mCenterY;
        mRightX = getWidth() - mPaddingRight;
        mRightY = mCenterY;
        mDelta = (mRightX - mLeftX) / (mStepLength - 1);

        mXPosition.add(mLeftX);
        for (int i = 1; i < mStepLength - 1; i++) {
            mXPosition.add(mLeftX + (i * mDelta));
        }
        mXPosition.add(mRightX);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mXPosition.size() - 1; i++) {
            final float startX = mXPosition.get(i);
            final float endX = mXPosition.get(i + 1);
            // 画线
            canvas.drawLine(startX, mLeftY, endX, mRightY, mLinePaint);
        }


        for (int i = 0; i < mXPosition.size(); i++) {
            final float startX = mXPosition.get(i);
            float length = mTextPaint.measureText(labels[i]);

            //绘制文字级别
            if (i == mSelectedPosition) {
                mCirclePaint.setColor(mCicleSelectColor);
                mTextPaint.setColor(mTextSelectColor);
                // 画指示器
                canvas.drawBitmap(mIndicatorBmp, startX - mIndicatorBmp.getWidth() / 2
                        , mCenterY - mCircleRadius - mIndicatorBmp.getHeight() - mIndicatorMargin, null);
            } else {
                mCirclePaint.setColor(mCircleUnselectColor);
                mTextPaint.setColor(mTextUnselectColor);
            }
            // 绘制文本
            canvas.drawText(labels[i], startX - length / 2, mTextY, mTextPaint);
            // 绘制圆
            canvas.drawCircle(startX, mCenterY, mCircleRadius, mCirclePaint);

        }
        if (mDrawListener != null) {
            //            mDrawListener.onColumnClick(mSelectedPosition);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (int i = 0; i < mXPosition.size(); i++) {
            final float pos = mXPosition.get(i);
            if (event.getX() <= pos + mCircleRadius && event.getX() >= pos - mCircleRadius) {
                //                setSelectedPosition(i);
                if (mDrawListener != null) {
                    mDrawListener.onReady(i);
                }
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    public void setStep(String[] step) {
        mStepLength = step.length;
        this.labels = step;
        invalidate();
    }

    public void setDrawListener(OnDrawListener drawListener) {
        mDrawListener = drawListener;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        invalidate();
    }

    public void reset() {
        setSelectedPosition(0);
    }

    public interface OnDrawListener {
        void onReady(int level);
    }
}
