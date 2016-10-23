package com.rongyi.diamond.pulltorefresh;

/**
 * Created by AItsuki on 2016/6/13.
 */
public interface RefreshHeader {

    /**
     * 松手，头部隐藏后会回调这个方法
     */
    void reset();

    /**
     * 下拉出头部的一瞬间调用
     */
    void pull();

    /**
     * 正在刷新的时候调用
     */
    void refreshing();

    /**
     * 头部滚动的时候持续调用
     *
     * @param currentPos target当前偏移高度
     * @param lastPos    target上一次的偏移高度
     * @param refreshPos 可以松手刷新的高度
     * @param isTouch    手指是否按下状态（通过scroll自动滚动时需要判断）
     * @param state      当前状态
     */
    void onPositionChange(float currentPos, float lastPos, float refreshPos, boolean isTouch, RefreshLayout.State state);

    /**
     * 刷新成功的时候调用
     */
    void complete();
}
