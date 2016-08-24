package uk.co.imallan.jellyrefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

class JellyLayout extends FrameLayout {

    private Paint mPaint;
    private Path mPath;
    @ColorInt
    private int mColor = Color.GRAY;
    private ViewOutlineProvider mViewOutlineProvider;
    private float mPointX;
    float mHeaderHeight = 0;
    float mPullHeight = 0;

    public JellyLayout(Context context) {
        this(context, null);
    }

    public JellyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JellyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mViewOutlineProvider = new ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void getOutline(View view, Outline outline) {
                    if (mPath.isConvex()) outline.setConvexPath(mPath);
                }
            };

        }
    }

    public void setColor(int color) {
        mColor = color;
    }

    public void setPointX(float pointX) {
        boolean needInvalidate = pointX != mPointX;
        mPointX = pointX;
        if (needInvalidate) invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPulling(canvas);
    }

    private void drawPulling(Canvas canvas) {

        final int width = canvas.getWidth();
        final float mDisplayX = (mPointX - width / 2f) * 0.5f + width / 2f;
        mPaint.setColor(mColor);

        int headerHeight = (int) mHeaderHeight;
        int pullHeight = (int) mPullHeight;

        mPath.rewind();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, headerHeight);
        mPath.quadTo(mDisplayX, pullHeight, width, headerHeight);
        mPath.lineTo(width, 0);
        mPath.close();

        canvas.drawPath(mPath, mPaint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(mViewOutlineProvider);
        }

    }

    public void setHeaderHeight(float headerHeight) {
        mHeaderHeight = headerHeight;
    }
}
