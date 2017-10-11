package com.kubatatami.poker.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

/**
 * Created by jstasinski on 12/03/14.
 */
public class PaperStackLayout extends FrameLayout {

    protected SlideMode slideMode = SlideMode.ALL;
    private ViewDragHelper mDragHelper;
    protected LayoutInflater inflater;
    protected SwipeListener listener;
    protected Adapter adapter;
    protected View animatedView;
    protected int esp = 0;
    protected boolean anim = false;
    protected final float degrees[] = new float[]{1, 1.5f, -1};
    protected final int MAX_VIEWS = degrees.length + 1;
    protected DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            removeAllViews();
            for (int i = 0; i < Math.min(adapter.getCount(), MAX_VIEWS); i++) {
                addPaper(adapter.getView(i, null, PaperStackLayout.this), 0);
            }
            if (listener != null) {
                listener.onStackChanged(0);
            }
        }
    };

    public enum SlideMode {
        LEFT_ONLY, RIGHT_ONLY, TOP_ONLY, BOTTOM_ONLY, HORIZONTAL, VERTICAL, ALL
    }

    public PaperStackLayout(Context context) {
        super(context);
        init(context);
    }

    public PaperStackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaperStackLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void goToItem(int position) {
        this.esp = position;
        dataSetObserver.onChanged();
    }

    public void setSlideMode(SlideMode slideMode) {
        this.slideMode = slideMode;
    }

    private View shuffleOldView;

    public void shuffle(boolean anim) {
        if (esp > 0 && getChildCount() > 0) {
            shuffleOldView = getChildAt(getChildCount() - 1);
            if (anim) {
                shuffleOldView.animate()
                        .x(-getWidth())
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                detachViewFromParent(shuffleOldView);
                                addPaper(adapter.getView(adapter.getCount() - 1, shuffleOldView, PaperStackLayout.this), 0);
                                shuffleOldView.setX(getWidth());
                                shuffleOldView.animate()
                                        .x(getWidth() / 2 - shuffleOldView.getWidth() / 2)
                                        .setDuration(250)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                if (listener != null) {
                                                    listener.onStackChanged(esp - getChildCount());
                                                }
                                            }
                                        });
                            }
                        }).start();
            } else {
                detachViewFromParent(shuffleOldView);
                addPaper(adapter.getView(adapter.getCount() - 1, shuffleOldView, PaperStackLayout.this), 0);
                if (listener != null) {
                    listener.onStackChanged(esp - getChildCount());
                }
            }
            this.esp--;

        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return indexOfChild(child) >= getChildCount() - MAX_VIEWS && super.drawChild(canvas, child, drawingTime);
    }

    public void setAdapter(Adapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        esp = 0;
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(dataSetObserver);
        dataSetObserver.onChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        super.onDetachedFromWindow();
    }

    public void addPaper(View page) {
        addPaper(page, getChildCount());
    }

    public void addPaper(View page, int index) {
        LayoutParams layoutParams = (LayoutParams) page.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        page.setRotation(degrees[esp % degrees.length]);
        page.setLayerType(LAYER_TYPE_HARDWARE, null);
        addView(page, index);
        esp++;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(size, size);
        }

    }

    enum Direction {
        BOTTOM, TOP, RETURN, LEFT_OR_RIGHT
    }

    Direction dir;

    protected void init(Context context) {
        inflater = LayoutInflater.from(context);
        setClipToPadding(false);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            protected int dragMode = 0;

            @Override
            public boolean tryCaptureView(View view, int i) {
                return true;
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                invalidate(capturedChild.getLeft(), capturedChild.getTop(), capturedChild.getRight(), capturedChild.getBottom());
            }

            @Override
            public void onViewReleased(final View releasedChild, float xvel, float yvel) {
                final float MIN_X = getWidth() / 7;
                final float MIN_Y = getHeight() / 10;
                float x = releasedChild.getX() + releasedChild.getWidth() / 2 - (getWidth() / 2);
                float y = releasedChild.getY() + releasedChild.getHeight() / 2 - (getHeight() / 2);
                PaperStackLayout.this.animatedView = releasedChild;
                dir = Direction.LEFT_OR_RIGHT;
                if (x > MIN_X && Math.abs(x) > Math.abs(y) && xvel > 0) {
                    mDragHelper.settleCapturedViewAt((int) (getWidth() + (releasedChild.getWidth() * 0.1f)), getHeight() / 2 - releasedChild.getHeight() / 2);
                } else if (-x > MIN_X && Math.abs(x) > Math.abs(y) && xvel < 0) {
                    mDragHelper.settleCapturedViewAt((int) (-releasedChild.getWidth() * 1.1f), getHeight() / 2 - releasedChild.getHeight() / 2);
                } else if (y > MIN_Y && Math.abs(x) < Math.abs(y) && yvel > 0) {
                    mDragHelper.settleCapturedViewAt(getWidth() / 2 - mDragHelper.getCapturedView().getWidth() / 2, getHeight());
                    dir = Direction.BOTTOM;
                    if (listener != null) {
                        listener.onSwipeBottom(esp - getChildCount());
                    }

                } else if (-y > MIN_Y && Math.abs(x) < Math.abs(y) && yvel < 0) {
                    mDragHelper.settleCapturedViewAt(getWidth() / 2 - mDragHelper.getCapturedView().getWidth() / 2, -mDragHelper.getCapturedView().getHeight());
                    dir = Direction.TOP;
                    if (listener != null) {
                        listener.onSwipeTop(esp - getChildCount());
                    }
                } else {
                    dir = Direction.RETURN;
                    PaperStackLayout.this.animatedView = null;
                    mDragHelper.settleCapturedViewAt(getWidth() / 2 - releasedChild.getWidth() / 2, getHeight() / 2 - releasedChild.getHeight() / 2);
                }

                dragMode = 0;
                invalidate(releasedChild.getLeft(), releasedChild.getTop(), releasedChild.getRight(), releasedChild.getBottom());
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                invalidate(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                if (!slideMode.equals(SlideMode.HORIZONTAL) &&
                        !slideMode.equals(SlideMode.LEFT_ONLY) &&
                        !slideMode.equals(SlideMode.RIGHT_ONLY) &&
                        (!slideMode.equals(SlideMode.TOP_ONLY) || top <= 0 || dy <= 0) &&
                        (!slideMode.equals(SlideMode.BOTTOM_ONLY) || top >= 0 || dy >= 0)
                        ) {

                    if (dragMode == 0) {
                        dragMode = 1;
                    }

                    if (dragMode == 1) {
                        return top;
                    }
                    return getHeight() / 2 - child.getHeight() / 2;
                } else {
                    return getHeight() / 2 - child.getHeight() / 2;
                }
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                invalidate(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());

                if (!slideMode.equals(SlideMode.VERTICAL) &&
                        !slideMode.equals(SlideMode.TOP_ONLY) &&
                        !slideMode.equals(SlideMode.BOTTOM_ONLY) &&
                        (!slideMode.equals(SlideMode.LEFT_ONLY) || left >= 0 || dx >= 0) &&
                        (!slideMode.equals(SlideMode.RIGHT_ONLY) || left <= 0 || dx <= 0)) {
                    if (dragMode == 0) {
                        dragMode = 2;
                    }
                    if (dragMode == 2) {
                        return left;
                    }
                    return getWidth() / 2 - child.getWidth() / 2;
                } else {
                    return getWidth() / 2 - child.getWidth() / 2;
                }
            }


        });
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            anim = true;
            ViewCompat.postInvalidateOnAnimation(this);
        } else if (anim) {
            anim = false;
            if (dir != Direction.RETURN) {
                detachViewFromParent(animatedView);

                if (esp < adapter.getCount()) {
                    addPaper(adapter.getView(esp, animatedView, PaperStackLayout.this), 0);
                }
//                if (listener != null) {
//                    if (dir == Direction.TOP) {
//                        listener.onSwipeTop(esp - 1);
//                    } else if (dir == Direction.BOTTOM) {
//                        listener.onSwipeBottom(esp - 1);
//                    }
//                }
            }

            if (listener != null && getChildCount() > 0) {
                listener.onStackChanged(esp - getChildCount());
            }

        }
    }

    public void setListener(SwipeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isEnabled() && mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            mDragHelper.processTouchEvent(event);
        }
        return true;
    }


    public interface SwipeListener {
        public void onSwipeTop(int position);

        public void onSwipeBottom(int position);

        public void onStackChanged(int position);
    }

    public static class SwipeListenerAdapter implements SwipeListener {

        @Override
        public void onSwipeTop(int position) {

        }

        @Override
        public void onSwipeBottom(int position) {

        }

        @Override
        public void onStackChanged(int position) {

        }
    }
}
