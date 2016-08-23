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
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

public class JellyLayout extends FrameLayout {
    private static final String TAG = "JellyLayout";

    private Paint mPaint;
    private Path mPath;
    @ColorInt
    private int mColor = Color.GRAY;
    // FIXME: 23/08/2016 hardcoded
    private int mHeaderHeight = 200;
    private int mPullHeight = 400;
    private ViewOutlineProvider mViewOutlineProvider;
    private float mPointX;

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
        final int width = canvas.getWidth();
        Log.d(TAG, "onDraw: height: " + getHeight());

        mHeaderHeight = getHeight() / 2;
        mPullHeight = getHeight();

        mPaint.setColor(mColor);

        float mDisplayX = (mPointX - width / 2f) * 0.5f + width / 2f;

        mPath.rewind();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, mHeaderHeight);
        mPath.quadTo(mDisplayX, mPullHeight, width, mHeaderHeight);
        mPath.lineTo(width, 0);
        mPath.close();

        canvas.drawPath(mPath, mPaint);

        canvas.drawLine(0, mPullHeight, width, mPullHeight, mPaint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(mViewOutlineProvider);
        }

    }
}
