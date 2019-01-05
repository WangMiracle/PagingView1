package com.miracle.pagingview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.miracle.pagingview.ILoading;
import com.miracle.pagingview.R;


/**
 * com.air.basecommon.view.paging
 * (c)2018 AIR Times Inc. All rights reserved.
 * loading
 *
 * @author WangJQ
 * @version 1.0
 * @date 2018/10/15 9:40
 */
public class LoadingView extends LinearLayout implements ILoading {

    private Context mContext;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        View container = LayoutInflater.from(mContext).inflate(R.layout.view_paging_loading,this);

    }

    @Override
    public void setStyle(int style) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
