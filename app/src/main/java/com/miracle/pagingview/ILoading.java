package com.miracle.pagingview;

/**
 * com.air.basecommon.view.paging
 * (c)2018 AIR Times Inc. All rights reserved.
 *
 *
 * @author WangJQ
 * @version 1.0
 * @date 2018/10/15 9:48
 */
public interface ILoading {
    /**
     * 设置样式
     * @param style
     */
    void setStyle(int style);

    /**
     * 显示
     */
    void showLoading();

    /**
     * 隐藏
     */
    void hideLoading();
}
