package com.miracle.pagingview.listener;

import java.util.List;

/**
 * com.air.basecommon.view.paging.listener
 * (c)2018 AIR Times Inc. All rights reserved.
 * 状态回调
 *
 * @author WangJQ
 * @version 1.0
 * @date 2018/12/20 20:45
 */
public interface IStateListener<T> {
    /**
     * 加载成功
     * @param list
     */
    void onSuccess(List<T> list);

    /**
     * 无数据
     */
    void onNoData();

    /**
     * 加载错误
     * @param msg
     */
    void onError(String msg);
}
