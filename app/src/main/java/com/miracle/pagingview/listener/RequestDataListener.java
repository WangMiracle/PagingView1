package com.miracle.pagingview.listener;


import com.miracle.pagingview.PagingView;

/**
 * com.air.basecommon.view.paging.listener
 * (c)2018 AIR Times Inc. All rights reserved.
 * 请求数据
 *
 * @author WangJQ
 * @version 1.0
 * @date 2018/9/6 14:30
 */
public interface RequestDataListener<T, M> {
    /**
     * 请求数据,回调
     * @param pagingView
     */
    void requestData(PagingView<T, M> pagingView);
}
