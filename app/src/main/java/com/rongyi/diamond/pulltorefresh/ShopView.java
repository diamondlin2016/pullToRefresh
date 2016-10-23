package com.rongyi.diamond.pulltorefresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Author:    Diamond_Lin
 * Version    V1.0
 * Date:      16/10/14 下午4:19
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 16/10/14      Diamond_Lin            1.0                    1.0
 * Why & What is modified:
 */
public class ShopView extends RelativeLayout implements RefreshHeader {
    private Context mContext;
    private Drawable[] drawables;
    //    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
//    private Interpolator dce = new DecelerateInterpolator();//减速
//    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速

    boolean isLeft;
    private int mHeight;
    private int mWidth;
    private LayoutParams lp;
    private Random random = new Random();
    private View rongYi;
    public Bezier mBezierLine;
    private View mIvTrans;


    public ShopView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ShopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ShopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }


    private void init() {
        //初始化显示的图片
        drawables = new Drawable[6];
        drawables[0] = getResources().getDrawable(R.mipmap.refresh_item1);
        drawables[1] = getResources().getDrawable(R.mipmap.refresh_item2);
        drawables[2] = getResources().getDrawable(R.mipmap.refresh_item3);
        drawables[3] = getResources().getDrawable(R.mipmap.refresh_item4);
        drawables[4] = getResources().getDrawable(R.mipmap.refresh_item5);
        drawables[5] = getResources().getDrawable(R.mipmap.refresh_item6);

        View view = View.inflate(getContext(), R.layout.widget_rongyi, this);
        mIvTrans = view.findViewById(R.id.iv_trans);
        rongYi = view.findViewById(R.id.iv_rongyi);
        mBezierLine = (Bezier) view.findViewById(R.id.bezier_line);
        //获取图的宽高 用于后面的计算
        //注意 我这里3张图片的大小都是一样的,所以我只取了一个

        //底部 并且 水平居中
        lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(CENTER_HORIZONTAL, TRUE);//这里的TRUE 要注意 不是true
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        lp.setMargins(0, 0, 0, rongYi.getHeight() + Utils.dp2px(getContext(), 20) + 8);
    }


    public void addShop() {
        ImageView imageView = new ImageView(mContext);
        //随机选一个
        int i = random.nextInt(6);
        imageView.setImageDrawable(drawables[i]);
        addView(imageView);
        imageView.setLayoutParams(lp);
        Animator set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();
        isLeft = !isLeft;
    }

    private Animator getAnimator(View target) {
        AnimatorSet set = getEnterAnimtor(target);
        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);
        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, bezierValueAnimator);
        finalSet.setInterpolator(acc);
        finalSet.setTarget(target);
        return finalSet;
    }

    private AnimatorSet getEnterAnimtor(final View target) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);
//        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f);
//        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f);
        ObjectAnimator trans = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, 10, 0);

        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(5);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha, trans);
        enter.setTarget(target);
        return enter;
    }

    private ValueAnimator getBezierValueAnimator(View target) {
        int nextInt = random.nextInt(50);
        int x = mWidth / 2;
        //初始化一个贝塞尔计算器- - 传入
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(false), getPointF(true));

        //这里最好画个图 理解一下 传入了起点 和 终点

        ValueAnimator animator = ValueAnimator.ofObject(evaluator,
                new PointF((mWidth - drawables[1].getIntrinsicWidth()) / 2, rongYi.getY() - drawables[0].getIntrinsicHeight()+8),
                new PointF(isLeft ? x - 150 : x + 150 - drawables[0].getIntrinsicWidth() - nextInt, mHeight - nextInt - Utils.dp2px(getContext(), 20)));
        animator.addUpdateListener(new BezierListener(target));
        animator.setTarget(target);
        animator.setDuration(800);
        return animator;
    }

    /**
     * 获取中间的两个 点
     */
    private PointF getPointF(boolean isSecond) {
        PointF pointF = new PointF();
        if (!isSecond){
            pointF.x = mWidth / 2 + (isLeft ? -50 : 50);//减去100 是为了控制 x轴活动范围,看效果 随意~~
            //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
            pointF.y = 0;
        }else {
            pointF.x = mWidth / 2 + (isLeft ? -100 : 100);
            pointF.y = 20;
        }
        return pointF;
    }

    @Override
    public void reset() {
        mBezierLine.refreshFinish();
    }

    @Override
    public void pull() {
    }

    @Override
    public void refreshing() {
        mBezierLine.setControlDown();
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.0f, 0.95f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(800);
        scaleAnimation.setRepeatCount(100);
        rongYi.startAnimation(scaleAnimation);
        star();
    }

    @Override
    public void onPositionChange(float currentPos, float lastPos, float refreshPos, boolean isTouch, RefreshLayout.State state) {
        mBezierLine.setControlY(currentPos);
        
        if (currentPos > mHeight) {
            int translationY = (int) (currentPos - mHeight);

            int dp20 = Utils.dp2px(getContext(), 20);
            if (translationY > dp20) {
                translationY = dp20;
            }
            mIvTrans.setTranslationY(dp20 - translationY);
        } else {
            mIvTrans.setTranslationY(Utils.dp2px(getContext(), 20));
        }
    }

    @Override
    public void complete() {
        stop();
        rongYi.getAnimation().cancel();
    }

    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里顺便做一个alpha动画
            target.setAlpha(1 - animation.getAnimatedFraction() / 2);
        }
    }


    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            addShop();
            handler.sendEmptyMessageDelayed(0, 250);
        }
    };

    public void star() {
        handler.sendEmptyMessage(0);
    }

    public void stop() {
        handler.removeCallbacksAndMessages(null);
    }

}
