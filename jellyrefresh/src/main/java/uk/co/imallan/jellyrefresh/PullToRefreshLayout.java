package uk.co.imallan.jellyrefresh;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Created by yilun
 * on 09/07/15.
 */
public class PullToRefreshLayout extends FrameLayout {

    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(10);

    static final int STATE_IDLE = 0;
    static final int STATE_DRAGGING = 1;
    static final int STATE_RELEASING = 2;
    static final int STATE_SETTLING = 3;
    static final int STATE_REFRESHING = 4;
    static final int STATE_REFRESHING_SETTLING = 5;

    @IntDef({STATE_IDLE,
            STATE_DRAGGING,
            STATE_REFRESHING,
            STATE_RELEASING,
            STATE_REFRESHING_SETTLING,
            STATE_SETTLING})
    @interface State {
    }

    private float mTouchStartY;
    private float mCurrentY;
    private View mChildView;
    float mPullHeight;
    float mHeaderHeight;
    float mTriggerHeight;
    @State
    private int mState = STATE_IDLE;
    private PullToRefreshListener mPullToRefreshListener;
    private PullToRefreshPullingListener mPullToRefreshPullingListener;
    private FrameLayout mHeader;

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
                56,
                getContext().getResources().getDisplayMetrics());

        mTriggerHeight = mHeaderHeight;

        this.post(() -> {
            mChildView = getChildAt(0);
            addHeaderContainer();
        });

    }

    public void setHeaderView(View headerView) {
        post(() -> mHeader.addView(headerView));
    }

    private void addHeaderContainer() {
        FrameLayout headerContainer = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
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
        mChildView.animate().setUpdateListener(valueAnimator -> {
            if (mPullToRefreshPullingListener != null) {
                mPullToRefreshPullingListener.onTranslationYChanged(mChildView.getTranslationY());
            }
        });
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

        return ViewCompat.canScrollVertically(mChildView, -1);
    }

    @State
    public int getState() {
        return mState;
    }

    public void setState(@State int state) {
        if (mState != state) {
            mState = state;
            onStateChanged(mState);
        }
    }

    protected void onStateChanged(@State int newState) {
    }

    public boolean isRefreshing() {
        return getState() == STATE_REFRESHING;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (getState() == STATE_REFRESHING
                || getState() == STATE_SETTLING
                || getState() == STATE_REFRESHING_SETTLING) {
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
        if (getState() == STATE_REFRESHING
                || getState() == STATE_RELEASING
                || getState() == STATE_SETTLING
                || getState() == STATE_REFRESHING_SETTLING) {
            return super.onTouchEvent(e);
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                setState(STATE_DRAGGING);
                mCurrentY = e.getY();
                float currentX = e.getX();
                float dy = MathUtils.constrains(
                        0,
                        mPullHeight * 2,
                        mCurrentY - mTouchStartY);
                if (mChildView != null) {
                    float offsetY = decelerateInterpolator.getInterpolation(dy / mPullHeight / 2) * dy / 2;
                    mChildView.setTranslationY(offsetY);
                    if (mPullToRefreshPullingListener != null) {
                        mPullToRefreshPullingListener.onTranslationYChanged(offsetY);
                        mPullToRefreshPullingListener.onPulling(offsetY / mHeaderHeight, currentX);
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mChildView != null) {
                    if (mChildView.getTranslationY() >= mTriggerHeight) {
                        mChildView.animate().translationY(mHeaderHeight)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                        setState(STATE_REFRESHING_SETTLING);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        setState(STATE_REFRESHING);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                        setState(STATE_REFRESHING);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                })
                                .start();
                        if (mPullToRefreshListener != null) {
                            mPullToRefreshListener.onRefresh(this);
                        }
                    } else {
                        mChildView.animate().translationY(0)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                        setState(STATE_RELEASING);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {
                                        setState(STATE_IDLE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {
                                        setState(STATE_IDLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                })
                                .start();
                    }

                } else {
                    setState(STATE_IDLE);
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

    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            if (mChildView != null) {
                mChildView.animate().translationY(mHeaderHeight)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                setState(STATE_SETTLING);
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                setState(STATE_REFRESHING);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                                setState(STATE_REFRESHING);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .start();
            }
        } else {
            if (!isRefreshing()) return;
            if (mChildView != null) {
                mChildView.animate().translationY(0)
                        .setListener(null)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                setState(STATE_SETTLING);
                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                setState(STATE_IDLE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {
                                setState(STATE_IDLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        }).start();
            } else {
                setState(STATE_IDLE);
            }
        }
    }


    public interface PullToRefreshListener {

        void onRefresh(PullToRefreshLayout pullToRefreshLayout);

    }

    interface PullToRefreshPullingListener {

        void onPulling(float fraction, float pointXPosition);

        void onTranslationYChanged(float translationY);
    }
}
