package com.dantelab.flowbutton.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by ivan on 07.08.14.
 */
public class FlowButton extends View {


    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static final int DEF_SHADOW_COLOR = Color.BLACK;
    private float mTransitionProgess;
    private ValueAnimator mAnimator;
    private long mAnimationDuration;
    private float mToggleTransitionFactor;
    private long mClickDelay;


    public enum BUTTON_MODE {
        CLICKED, TOGGLE
    }

    private static final int DEF_FIRST_COLOR = Color.WHITE;
    private static final int DEF_SECOND_COLOR = Color.GRAY;

    private static final int DEF_SHADOW_OFFSET_X = 0;
    private static final int DEF_SHADOW_OFFSET_Y = 0;

    private static final int DEF_TRANSITION_DIRECTION = 0;

    private static final int DEF_TRANSITION_FACTOR = 1;

    private static final Interpolator DEF_INTERPOLATOR = new DecelerateInterpolator();

    private static final int DEF_ANIMATION_DURATION = 500;

    private static final int DEF_CLICK_DELAY = 300;

    private static final int DEF_ICON_PADDING = 0;

    private static final BUTTON_MODE DEF_BUTTON_MODE = BUTTON_MODE.TOGGLE;

    private static final boolean DEF_PRESSED = false;
    private OnClickListener mOnClickListener;
    private OnLongClickListener mOnLongClickListener;

    private int DEF_SHADOW_RADIUS() {
        return getResources().getDimensionPixelSize(R.dimen.flowbutton_shadow_radius_default);
    }


    private Paint mPaint;
    private int mShadowRadius;
    private int mSecondColor;
    private int mShadowColor;
    private int mFirstColor;
    private int mShadowOffsetX;
    private int mShadowOffsetY;
    private RectF mCircleRect;
    private boolean mChecked;
    private BUTTON_MODE mButtonMode;
    private TouchEventListener mTouchEventListener;
    private int mFirstIcon;
    private int mSecondIcon;
    private float mToggleTransitionDirection;

    private BitmapDrawable mDrawable1;
    private BitmapDrawable mDrawable2;


    private int mIconPaddingLeft;
    private int mIconPaddingTop;
    private int mIconPaddingRight;
    private int mIconPaddingBottom;
    private Rect mIconRect;


    private Drawer mDrawer = new Drawer();

    private Interpolator mAnimationInterpolator;

    public FlowButton(Context context) {
        super(context);
        init(context);
    }

    public FlowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public FlowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context) {
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (attrs == null) {
            mFirstColor = DEF_FIRST_COLOR;
            mSecondColor = DEF_SECOND_COLOR;
            mShadowRadius = DEF_SHADOW_RADIUS();
            mShadowOffsetX = DEF_SHADOW_OFFSET_X;
            mShadowOffsetY = DEF_SHADOW_OFFSET_Y;
            mShadowColor = DEF_SHADOW_COLOR;
            mChecked = DEF_PRESSED;
            mButtonMode = DEF_BUTTON_MODE;
            mShadowColor = DEF_SECOND_COLOR;
            mToggleTransitionDirection = DEF_TRANSITION_DIRECTION;
            mIconPaddingLeft = mIconPaddingBottom = mIconPaddingRight = mIconPaddingTop = DEF_ICON_PADDING;
            mAnimationInterpolator = DEF_INTERPOLATOR;
            mAnimationDuration = DEF_ANIMATION_DURATION;
            mToggleTransitionFactor = DEF_TRANSITION_FACTOR;
            mClickDelay = DEF_CLICK_DELAY;

        } else {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.FlowButton, 0, 0);
            try {
                mFirstColor = a.getColor(R.styleable.FlowButton_flowbutton_firstColor, DEF_FIRST_COLOR);
                mSecondColor = a.getColor(R.styleable.FlowButton_flowbutton_secondColor, DEF_SECOND_COLOR);
                mShadowRadius = a.getDimensionPixelOffset(R.styleable.FlowButton_flowbutton_shadowRadius, DEF_SHADOW_RADIUS());
                mShadowColor = a.getColor(R.styleable.FlowButton_flowbutton_shadowColor, DEF_SHADOW_COLOR);
                mShadowOffsetX = a.getDimensionPixelOffset(R.styleable.FlowButton_flowbutton_shadowOffsetX, DEF_SHADOW_OFFSET_X);
                mShadowOffsetY = a.getDimensionPixelOffset(R.styleable.FlowButton_flowbutton_shadowOffsetY, DEF_SHADOW_OFFSET_Y);
                mChecked = a.getBoolean(R.styleable.FlowButton_flowbutton_pressed, DEF_PRESSED);
                mToggleTransitionFactor = a.getFloat(R.styleable.FlowButton_flowbutton_toggleTransitionFactor, DEF_TRANSITION_FACTOR);
                mToggleTransitionDirection = a.getFloat(R.styleable.FlowButton_flowbutton_toggleTransitionDirection, DEF_TRANSITION_DIRECTION);
                mIconPaddingLeft = mIconPaddingBottom = mIconPaddingRight = mIconPaddingTop = a.getDimensionPixelSize(R.styleable.FlowButton_flowbutton_iconPadding, DEF_ICON_PADDING);
                mIconPaddingLeft = a.getDimensionPixelOffset(R.styleable.FlowButton_flowbutton_iconPaddingLeft, mIconPaddingLeft);
                mIconPaddingRight = a.getDimensionPixelOffset(R.styleable.FlowButton_flowbutton_iconPaddingRight, mIconPaddingRight);
                mIconPaddingTop = a.getDimensionPixelOffset(R.styleable.FlowButton_flowbutton_iconPaddingTop, mIconPaddingTop);
                mIconPaddingBottom = a.getDimensionPixelOffset(R.styleable.FlowButton_flowbutton_iconPaddingBottom, mIconPaddingBottom);
                mAnimationDuration = a.getInt(R.styleable.FlowButton_flowbutton_animationDuration, DEF_ANIMATION_DURATION);
                mClickDelay = a.getInt(R.styleable.FlowButton_flowbutton_clickDelay, DEF_CLICK_DELAY);

                mFirstIcon = a.getResourceId(R.styleable.FlowButton_flowbutton_firstIcon, 0);
                mSecondIcon = a.getResourceId(R.styleable.FlowButton_flowbutton_secontIcon, 0);

                int interpolatorId = a.getResourceId(R.styleable.FlowButton_flowbutton_animationInterpolator, 0);
                if (interpolatorId == 0) mAnimationInterpolator = DEF_INTERPOLATOR;
                else
                    mAnimationInterpolator = AnimationUtils.loadInterpolator(getContext(), interpolatorId);

                int b_mode = a.getInteger(R.styleable.FlowButton_flowbutton_mode, 0);
                switch (b_mode) {
                    case 0:
                        mButtonMode = BUTTON_MODE.CLICKED;
                        break;
                    case 1:
                        mButtonMode = BUTTON_MODE.TOGGLE;
                        break;
                    default:
                        mButtonMode = DEF_BUTTON_MODE;
                }

            } finally {
                a.recycle();
            }
        }


        reloadDrawables();
        mPaint.setColor(mFirstColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShadowLayer(mShadowRadius, mShadowOffsetX, mShadowOffsetY, mShadowColor);
        createDrawingRect();
        mTouchEventListener = new TouchEventListener();
        super.setOnClickListener(mTouchEventListener);
        super.setOnLongClickListener(mTouchEventListener);

    }

    private void reloadDrawables() {

        if (mDrawable1 != null)
            try {
                mDrawable1.getBitmap().recycle();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mDrawable1 = null;
            }

        if (mDrawable2 != null)
            try {
                mDrawable2.getBitmap().recycle();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mDrawable2 = null;
            }

        if (mFirstIcon != 0) {

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), mFirstIcon);
            mDrawable1 = new BitmapDrawable(getResources(), bmp);
        }

        if (mSecondIcon != 0) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), mSecondIcon);
            mDrawable2 = new BitmapDrawable(getResources(), bmp);
        }
    }


    private void createDrawingRect() {

        int padTop = getPaddingTop();
        int padBottom = getPaddingBottom();
        int padLeft = getPaddingLeft();
        int padRight = getPaddingRight();

        mCircleRect = new RectF(padLeft + (mShadowRadius - mShadowOffsetX), padTop + (mShadowRadius - mShadowOffsetY), getWidth() - padRight - (mShadowRadius + mShadowOffsetX), getHeight() - padBottom - (mShadowRadius + mShadowOffsetY));
        mIconRect = new Rect((int) mCircleRect.left + (int) mIconPaddingLeft, (int) mCircleRect.top + (int) mIconPaddingTop, (int) mCircleRect.right - mIconPaddingRight, (int) mCircleRect.bottom - mIconPaddingBottom);
        //invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        createDrawingRect();
        invalidate();
    }

    private Matrix mMatrix = new Matrix();
    private RectF mAnimationBitmapRect = new RectF();
    private RectF mAnimationScaledRect = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mDrawer.drawBaseLayer(canvas);

        if (mAnimator == null) {

            mDrawer.drawBaseLayer(canvas);
            mDrawer.drawBaseIconLayer(canvas);
            /*int toDrawColor;
            BitmapDrawable toDrawDrawable;

            if (isChecked()) {
                toDrawColor = mSecondColor;
                toDrawDrawable = mDrawable2;
            } else {
                toDrawColor = mFirstColor;
                toDrawDrawable = mDrawable1;
            }*/
/*
            mPaint.setShadowLayer(mShadowRadius, mShadowOffsetX, mShadowOffsetY, mShadowColor);
            mPaint.setColor(toDrawColor);
            canvas.drawCircle(mCircleRect.centerX(), mCircleRect.centerY(), mCircleRect.width() / 2, mPaint);*/

            /*if (toDrawDrawable != null) {
                mPaint.setShadowLayer(0, 0, 0, 0);
                toDrawDrawable.setBounds(mIconRect);
                toDrawDrawable.draw(canvas);
            }*/

        } else {

            if (mButtonMode == BUTTON_MODE.TOGGLE)
               mDrawer.drawToggleAnimation(canvas);
            else {
               mDrawer.drawClickAnimation(canvas);
            }
        }

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mOnLongClickListener = l;
    }


    private class TouchEventListener implements OnClickListener, OnLongClickListener {
        @Override
        public void onClick(final View v) {
            if (mOnClickListener != null && mAnimator == null) {
                final OnClickListener l = mOnClickListener;

                if (mButtonMode == BUTTON_MODE.TOGGLE)
                    mChecked = !mChecked;
                else
                    mChecked = false;

                initAnimation();
                sHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        l.onClick(v);
                    }
                }, mClickDelay);

            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnLongClickListener != null) {
                return mOnLongClickListener.onLongClick(v);
            } else {
                return false;
            }
        }
    }

    private void initAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(mAnimationDuration);
        animator.setInterpolator(mAnimationInterpolator);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                mTransitionProgess = value;
                invalidate();
            }
        });

        if (mAnimator != null) {
            mAnimator.removeAllUpdateListeners();
            mAnimator.removeAllListeners();
        }

        mAnimator = animator;

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator = null;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimator = null;
                invalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();

    }

    public int getShadowRadius() {
        return mShadowRadius;
    }

    public void setShadowRadius(int shadowRadius) {
        this.mShadowRadius = shadowRadius;
        createDrawingRect();
        invalidate();
    }

    public int getSecondColor() {
        return mSecondColor;
    }

    public void setSecondColor(int secondColor) {
        this.mSecondColor = secondColor;
        invalidate();
    }

    public int getmShadowColor() {
        return mShadowColor;
    }

    public void setmShadowColor(int mShadowColor) {
        this.mShadowColor = mShadowColor;
        invalidate();
    }

    public int getmFirstColor() {
        return mFirstColor;
    }

    public void setFirstColor(int firstColor) {
        this.mFirstColor = firstColor;
        invalidate();
    }

    public int getmShadowOffsetX() {
        return mShadowOffsetX;
    }

    public void setShadowOffsetX(int shadowOffsetX) {
        this.mShadowOffsetX = shadowOffsetX;
        createDrawingRect();
        invalidate();
    }

    public int getShadowOffsetY() {
        return mShadowOffsetY;
    }

    public void setShadowOffsetY(int shadowOffsetY) {
        this.mShadowOffsetY = shadowOffsetY;
        createDrawingRect();
        invalidate();
    }


    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean pressed) {
        this.mChecked = pressed;
        invalidate();
    }

    public BUTTON_MODE getButtonMode() {
        return mButtonMode;
    }

    public void setButtonMode(BUTTON_MODE buttonMode) {
        this.mButtonMode = buttonMode;
        invalidate();
    }

    public int getmFirstIcon() {
        return mFirstIcon;
    }

    public void setFirstIcon(int firstIcon) {
        this.mFirstIcon = firstIcon;
        reloadDrawables();
        invalidate();
    }

    public int getSecondIcon() {
        return mSecondIcon;
    }

    public void setSecondIcon(int secondIcon) {
        this.mSecondIcon = secondIcon;
        reloadDrawables();
        invalidate();
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.isChecked = mChecked ? 1 : 0;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState st = (SavedState) state;
        super.onRestoreInstanceState(st.getSuperState());
        mChecked = st.isChecked == 1;

    }

    public static class SavedState extends BaseSavedState {

        int isChecked;

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isChecked);
        }

        private SavedState(Parcel in) {
            super(in);
            isChecked = in.readInt();
        }
    }

    private final class Drawer{



        /*

        Base layers

         */

        public void drawBaseLayer(Canvas canvas) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setShadowLayer(mShadowRadius, mShadowOffsetX, mShadowOffsetY, mShadowColor);
            mPaint.setColor(isChecked() ? mSecondColor : mFirstColor);
            canvas.drawCircle(mCircleRect.centerX(), mCircleRect.centerY(), mCircleRect.width() / 2, mPaint);
        }

        public void drawBaseIconLayer(Canvas canvas) {

            mPaint.setShadowLayer(0, 0, 0, 0);
            Drawable drawable = isChecked() ? mDrawable2 : mDrawable1;
            if (drawable!=null) {
                drawable.setBounds(mIconRect);
                drawable.draw(canvas);
            }
        }


        /*

        Toggle Animation

         */


        private void drawToggleAnimation(Canvas canvas){
            BitmapDrawable outDrawable, inDrawable;
            int inColor, outColor;

            if (isChecked()) {
                outColor = mFirstColor;
                inColor = mSecondColor;
                inDrawable = mDrawable2;
                outDrawable = mDrawable1;
            } else {
                inColor = mFirstColor;
                outColor = mSecondColor;
                outDrawable = mDrawable2;
                inDrawable = mDrawable1;
            }

            mPaint.setShadowLayer(mShadowRadius, mShadowOffsetX, mShadowOffsetY, mShadowColor);
            mPaint.setColor(outColor);
            canvas.drawCircle(mCircleRect.centerX(), mCircleRect.centerY(), mCircleRect.width() / 2, mPaint);
            mPaint.setShadowLayer(0, 0, 0, 0);
            drawToggleAnimationInCircle(canvas, inColor, mTransitionProgess);


            if (outDrawable != null && mTransitionProgess != 1) {
                Bitmap bmp = outDrawable.getBitmap();
                drawToggleAnimationBitmapInAnimation(canvas, bmp, mTransitionProgess);
            }
            if (inDrawable != null && mTransitionProgess != 0) {
                Bitmap bmp = inDrawable.getBitmap();
                drawToggleAnimationBitmapInAnimation(canvas, bmp, 1 - mTransitionProgess);
            }
        }

        private void drawToggleAnimationBitmapInAnimation(Canvas canvas, Bitmap bmp, float progress) {
            mMatrix.reset();

            mAnimationBitmapRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
            int cX = mIconRect.centerX();
            int cY = mIconRect.centerY();
            mAnimationScaledRect.set(
                    mIconRect.left + progress*(cX - mIconRect.left),
                    mIconRect.top + progress*(cY - mIconRect.top),
                    mIconRect.right - progress*( mIconRect.right - cX),
                    mIconRect.bottom - progress*( mIconRect.bottom - cY)
            );
           /* mAnimationBitmapRect.set(0, 0, bmp.getWidth(), bmp.getHeight());
            mAnimationScaledRect.set(
                    mIconRect.left + progress * bmp.getWidth() / 2,
                    mIconRect.top + progress * bmp.getHeight() / 2,
                    mIconRect.right - progress * bmp.getWidth() / 2,
                    mIconRect.bottom - progress * bmp.getHeight() / 2
            );*/

            mMatrix.setRectToRect(mAnimationBitmapRect, mAnimationScaledRect, Matrix.ScaleToFit.CENTER);
            canvas.drawBitmap(bmp, mMatrix, null);
        }

        private void drawToggleAnimationInCircle(Canvas canvas, int color, float progress) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(color);
            canvas.drawArc(mCircleRect, mToggleTransitionDirection + (-180 * mToggleTransitionFactor) * progress, progress * 360, false, mPaint);
        }

        /*

        Click Animation

         */


        public void drawClickAnimation(Canvas canvas) {
            drawBaseLayer(canvas);
            drawBaseIconLayer(canvas);

            float radius = mCircleRect.width()/2;

            mPaint.setStyle(Paint.Style.STROKE);
            float strokeWidth = radius*(1-mTransitionProgess);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setColor(mSecondColor);
            mPaint.setAlpha((int) ((1-mTransitionProgess)*255));
            canvas.drawCircle(mCircleRect.centerX(), mCircleRect.centerY(), (radius-strokeWidth/2)*(mTransitionProgess),mPaint);
        }


    }
}
