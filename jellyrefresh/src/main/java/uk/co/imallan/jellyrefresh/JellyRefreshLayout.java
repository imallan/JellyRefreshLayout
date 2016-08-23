package uk.co.imallan.jellyrefresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

public class JellyRefreshLayout extends PullToRefreshLayout {

    public JellyRefreshLayout(Context context) {
        super(context);
        init();
    }

    public JellyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JellyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        JellyLayout jellyLayout = new JellyLayout(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jellyLayout.setElevation(getElevation());
        }
        setHeaderView(jellyLayout);
        setPullingListener(new PullToRefreshPullingListener() {
            @Override
            public void onPulling(PullToRefreshLayout pullToRefreshLayout, float fraction, float pointXPosition) {
                jellyLayout.setPointX(pointXPosition);
            }

            @Override
            public void onReleasing(PullToRefreshLayout pullToRefreshLayout, float fraction) {

            }
        });
    }
}
