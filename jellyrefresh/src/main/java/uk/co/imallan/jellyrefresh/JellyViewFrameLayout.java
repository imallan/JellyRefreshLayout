package uk.co.imallan.jellyrefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * User: TaurusXi
 */
public class JellyViewFrameLayout extends FrameLayout {
    Path path;
    Paint paint;
    private int minimumHeight;
    private int jellyHeight;

    public JellyViewFrameLayout(Context context) {
        this(context, null, 0);
    }

    public JellyViewFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JellyViewFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JellyViewFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        if (isInEditMode()) {
            return;
        }
        path = new Path();
        paint = new Paint();
        paint.setColor(getContext().getResources().getColor(android.R.color.holo_blue_bright));
        paint.setAntiAlias(true);
    }

    public void setJellyColor(int jellyColor) {
        paint.setColor(jellyColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        path.reset();
        path.lineTo(0, minimumHeight);
        path.quadTo(measuredWidth / 2, minimumHeight + jellyHeight, measuredWidth, minimumHeight);
        path.lineTo(measuredWidth, 0);
        canvas.drawPath(path, paint);
    }

    @Override
    public void setMinimumHeight(int minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    public void setJellyHeight(int ribbonHeight) {
        this.jellyHeight = ribbonHeight;
    }

    @Override
    public int getMinimumHeight() {
        return minimumHeight;
    }

    public int getJellyHeight() {
        return jellyHeight;
    }
}
