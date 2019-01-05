package com.miracle.pagingview.view;

/**
 * com.air.basecommon.view.paging.view
 * (c)2018 AIR Times Inc. All rights reserved.
 *
 *
 * @author WangJQ
 * @version 1.0
 * @date 2019/1/4 8:44
 */
public interface IState {
    /**
     * 无数据
     */
    void onNoData();

    /**
     * 异常
     * @param msg
     */
    void onError(String msg);

    /**
     * 成功
     */
    void onSuccess();
}
