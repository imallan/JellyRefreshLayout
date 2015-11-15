package uk.co.imallan.jellyrefresh;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

/**
 * User: Yilun Chen
 * Date: 15/7/9
 */
public class JellyRefreshLayout extends PullToRefreshLayout {

    JellyRefreshListener mJellyRefreshListener;

    private String mLoadingText = "Loading...";

    private int mLoadingTextColor;

    private int mJellyColor;

    public JellyRefreshLayout(Context context) {
        super(context);
        setupHeader();
    }

    public JellyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        setupHeader();
    }

    public JellyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(attrs);
        setupHeader();
    }

    public JellyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setAttributes(attrs);
        setupHeader();
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.JellyRefreshLayout);
        try {
            Resources resources = getResources();
            mLoadingText = a.getString(R.styleable.JellyRefreshLayout_android_text);
            mLoadingTextColor = a.getColor(R.styleable.JellyRefreshLayout_android_textColor,
                    resources.getColor(android.R.color.white));
            mJellyColor = a.getColor(R.styleable.JellyRefreshLayout_jellyColor,
                    resources.getColor(android.R.color.holo_blue_bright));
        } finally {
            a.recycle();
        }
    }

    public void setRefreshListener(JellyRefreshListener jellyRefreshListener) {
        this.mJellyRefreshListener = jellyRefreshListener;
    }

    private void setupHeader() {
        if (isInEditMode()) {
            return;
        }

        @SuppressLint("InflateParams") View headerView = LayoutInflater.from(getContext()).inflate(R.layout.new_view_pull_header, null);
        final JellyViewFrameLayout jellyView = (JellyViewFrameLayout) headerView.findViewById(R.id.jelly);
        final TextView textLoading = (TextView) headerView.findViewById(R.id.text_loading);
        jellyView.setJellyColor(mJellyColor);
        textLoading.setText(mLoadingText);
        textLoading.setTextColor(mLoadingTextColor);
        final float headerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        setHeaderHeight(headerHeight);
        final float pullHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        setPullHeight(pullHeight);
        setHeaderView(headerView);
        setPullToRefreshListener(
                new PullToRefreshListener() {
                    @Override
                    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                        if (mJellyRefreshListener != null) {
                            mJellyRefreshListener.onRefresh(JellyRefreshLayout.this);
                        }
                        jellyView.setMinimumHeight((int) (headerHeight));
                        ValueAnimator animator = ValueAnimator.ofInt(jellyView.getJellyHeight(), 0);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                jellyView.setJellyHeight((int) animation.getAnimatedValue());
                                jellyView.invalidate();
                            }
                        });
                        animator.setInterpolator(new OvershootInterpolator(3));
                        animator.setDuration(200);
                        animator.start();
                        pullToRefreshLayout.postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        textLoading.setAlpha(0.01f);
                                        textLoading.setVisibility(View.VISIBLE);
                                        textLoading.animate().alpha(1f).setInterpolator(new AccelerateInterpolator()).setDuration(200);
                                    }
                                }, 120
                        );
                    }
                }
        );
        setPullingListener(new PullToRefreshLayout.PullToRefreshPullingListener() {
            @Override
            public void onPulling(PullToRefreshLayout pullToRefreshLayout, float fraction) {
                textLoading.setVisibility(View.GONE);
                jellyView.setMinimumHeight((int) (headerHeight * MathUtils.constrains(0, 1, fraction)));
                jellyView.setJellyHeight((int) (pullHeight * Math.max(0, fraction - 1)));
                jellyView.invalidate();
            }

            @Override
            public void onReleasing(PullToRefreshLayout pullToRefreshLayout, float fraction) {
                if (!pullToRefreshLayout.isRefreshing()) {
                    textLoading.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void setPullHeight(float pullHeight) {
        super.setPullHeight(pullHeight);
    }

    @Override
    public void setHeaderHeight(float headerHeight) {
        super.setHeaderHeight(headerHeight);
    }

    @Override
    public boolean isRefreshing() {
        return super.isRefreshing();
    }

    @Override
    public void finishRefreshing() {
        super.finishRefreshing();
    }

    public interface JellyRefreshListener {

        void onRefresh(JellyRefreshLayout jellyRefreshLayout);

    }
}
