package com.miracle.pagingview.listener;

import java.util.List;

/**
 * com.air.basecommon.view.paging.listener
 * (c)2018 AIR Times Inc. All rights reserved.
 * 将Response转换为PagingView需要的List
 *
 * @author WangJQ
 * @version 1.0
 * @date 2018/9/6 14:31
 */
public interface ConvertResponseListener<T, M> {
    /**
     * 将Response转换为PagingView需要的List
     * @param baseResponse
     * @return
     */
    List<T> observerAdapter(M baseResponse);
}
