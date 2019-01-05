package com.miracle.pagingview.listener;

import java.util.List;

/**
 * com.air.basecommon.view.paging.listener
 * (c)2018 AIR Times Inc. All rights reserved.
 * 过滤文件接口
 *
 * @author WangJQ
 * @version 1.0
 * @date 2018/9/6 14:02
 */
public interface FilterInterface<T> {
    List<T> filterData(List<T> list);
}
