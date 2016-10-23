package com.rongyi.diamond.pulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author:    Diamond_Lin
 * Version    V1.0
 * Date:      16/10/17 下午4:46
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 16/10/17      Diamond_Lin            1.0                    1.0
 * Why & What is modified:
 */
public class Bezier extends View {

    private Paint mPaint;
    private int centerX, centerY;
    public static int DP20;

    private PointF start, end, control;
    private int mWidth;
    private float max;
    private float min;

    public Bezier(Context context) {
        super(context);
        DP20 = Utils.dp2px(getContext(), 20);
    }

    public Bezier(Context context, AttributeSet attrs) {
        super(context, attrs);
        DP20 = Utils.dp2px(getContext(), 20);
    }

    public Bezier(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DP20 = Utils.dp2px(getContext(), 20);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        start = new PointF(mWidth / 4, 8 + DP20);
        end = new PointF(mWidth / 4 * 3, 8 + DP20);
        control = new PointF(mWidth / 2, (float) (mWidth / 4 * 1.732) + DP20);
        max = (float) (mWidth / 4 * 1.732 + DP20);
        min = (float) (-mWidth / 4 * 1.732 + DP20);
    }

    public void refreshFinish(){
        isRefresh = false;
    }

    public void setControlY(float y) {
        if (isRefresh){
            return;
        }
        if (y + min < max) {
            control.y = y + min;
        } else if (y + min > max && y + min < 2 * (max - min)) {
            control.y = 2 * max - min - y;
        } else {
            control.y = min;
        }
        invalidate();
    }
    boolean isRefresh = false;
    public void setControlDown(){
        isRefresh= true;
        control.y = max;
        invalidate();
    }
    public void setControlUp(){
        control.y = min;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制贝塞尔曲线
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(2);

        Path path = new Path();

        path.moveTo(start.x, start.y);
        path.quadTo(control.x, control.y, end.x, end.y);

        canvas.drawPath(path, mPaint);
    }
}
