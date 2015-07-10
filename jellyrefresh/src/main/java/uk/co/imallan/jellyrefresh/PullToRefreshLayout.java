package uk.co.imallan.jellyrefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by yilun
 * on 09/07/15.
 */
class PullToRefreshLayout extends FrameLayout {

    private float mTouchStartY;

    private float mCurrentY;

    private View mChildView;

    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(10);

    private float mPullHeight;

    private float mHeaderHeight;

    private boolean isRefreshing;

    private PullToRefreshListener mPullToRefreshListener;

    private PullToRefreshPullingListener mPullToRefreshPullingListener;

    private FrameLayout mHeader;

    public PullToRefreshLayout(Context context) {
        super(context);
        init();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("You can only attach one child");
        }


        mPullHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                150,
                getContext().getResources().getDisplayMetrics());

        mHeaderHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                100,
                getContext().getResources().getDisplayMetrics());

        this.post(() -> {
            mChildView = getChildAt(0);
            addHeaderContainer();
        });

    }

    public void setHeaderView(View headerView) {
        post(() -> mHeader.addView(headerView));
    }

    public void setPullHeight(float pullHeight) {
        this.mPullHeight = pullHeight;
    }

    public void setHeaderHeight(float headerHeight) {
        this.mHeaderHeight = headerHeight;
    }

    public float getmPullHeight() {
        return mPullHeight;
    }

    public float getmHeaderHeight() {
        return mHeaderHeight;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    private void addHeaderContainer() {
        FrameLayout headerContainer = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.gravity = Gravity.TOP;
        headerContainer.setLayoutParams(layoutParams);

        mHeader = headerContainer;
        addViewInternal(headerContainer);
        setUpChildViewAnimator();
    }

    private void setUpChildViewAnimator() {
        if (mChildView == null) {
            return;
        }
        mChildView.animate().setInterpolator(new DecelerateInterpolator());
        mChildView.animate().setUpdateListener(animation -> {
                    int height = (int) mChildView.getTranslationY();
                    mHeader.getLayoutParams().height = height;
                    mHeader.requestLayout();
                    if (mPullToRefreshPullingListener != null) {
                        mPullToRefreshPullingListener.onReleasing(this, height / mHeaderHeight);
                    }
                }
        );
    }

    private void addViewInternal(@NonNull View child) {
        super.addView(child);
    }

    @Override
    public void addView(@NonNull View child) {
        if (getChildCount() >= 1) {
            throw new RuntimeException("You can only attach one child");
        }
        mChildView = child;
        super.addView(child);
        setUpChildViewAnimator();
    }

    public boolean canChildScrollUp() {
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (isRefreshing) {
            return true;
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartY = e.getY();
                mCurrentY = mTouchStartY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = e.getY();
                float dy = currentY - mTouchStartY;
                if (dy > 0 && !canChildScrollUp()) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        if (isRefreshing) {
            return super.onTouchEvent(e);
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurrentY = e.getY();
                float dy = MathUtils.constrains(
                        0,
                        mPullHeight * 2,
                        mCurrentY - mTouchStartY);
                if (mChildView != null) {
                    float offsetY = decelerateInterpolator.getInterpolation(dy / mPullHeight / 2) * dy / 2;
                    mChildView.setTranslationY(
                            offsetY
                    );
                    mHeader.getLayoutParams().height = (int) offsetY;
                    mHeader.requestLayout();
                    if (mPullToRefreshPullingListener != null) {
                        mPullToRefreshPullingListener.onPulling(this, offsetY / mHeaderHeight);
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView != null) {
                    if (mChildView.getTranslationY() >= mHeaderHeight) {
                        mChildView.animate().translationY(mHeaderHeight).start();
                        isRefreshing = true;
                        if (mPullToRefreshListener != null) {
                            mPullToRefreshListener.onRefresh(this);
                        }
                    } else {
                        mChildView.animate().translationY(0).start();
                    }

                }
                return true;
            default:
                return super.onTouchEvent(e);
        }
    }

    public void setPullToRefreshListener(PullToRefreshListener pullToRefreshListener) {
        this.mPullToRefreshListener = pullToRefreshListener;
    }

    public void setPullingListener(PullToRefreshPullingListener pullingListener) {
        this.mPullToRefreshPullingListener = pullingListener;
    }

    public void finishRefreshing() {
        if (mChildView != null) {
            mChildView.animate().translationY(0).start();
        }
        isRefreshing = false;
    }


    interface PullToRefreshListener {

        void onRefresh(PullToRefreshLayout pullToRefreshLayout);

    }

    interface PullToRefreshPullingListener {

        void onPulling(PullToRefreshLayout pullToRefreshLayout, float fraction);

        void onReleasing(PullToRefreshLayout pullToRefreshLayout, float fraction);

    }
}
