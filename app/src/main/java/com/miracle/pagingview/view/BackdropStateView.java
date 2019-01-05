package com.miracle.pagingview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;

import com.miracle.pagingview.R;


/**
 * com.air.basecommon.view.paging.view
 * (c)2018 AIR Times Inc. All rights reserved.
 *
 * backdrop 状态布局
 *
 * @author WangJQ
 * @version 1.0
 * @date 2019/1/4 8:49
 */
public class BackdropStateView extends BaseStateView {

    private Button mClearButton;

    public BackdropStateView(Context context) {
        super(context);
    }

    public BackdropStateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BackdropStateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        mClearButton = mView.findViewById(R.id.btn_clear);
    }

    @Override
    public int getLayout() {
        return R.layout.view_state_backdrop;
    }

    public Button getClearButton() {
        return mClearButton;
    }
}
