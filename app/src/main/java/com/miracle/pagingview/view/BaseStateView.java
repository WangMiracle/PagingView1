package com.miracle.pagingview.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miracle.pagingview.R;

/**
 * com.air.basecommon.view.paging.view
 * (c)2018 AIR Times Inc. All rights reserved.
 *
 *
 * @author WangJQ
 * @version 1.0
 * @date 2019/1/4 8:51
 */
public class BaseStateView extends LinearLayout implements IState {

    public static final String TAG = "BaseStateView";
    Context mContext;
    protected ImageView mIvStatePic;
    protected TextView mTvState;
    protected View mView;

    public BaseStateView(Context context) {
        this(context, null);
    }

    public BaseStateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseStateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    protected void initView() {
        mView = LayoutInflater.from(mContext).inflate(getLayout(), this);
        mIvStatePic = mView.findViewById(R.id.iv_nodata);
        mTvState = mView.findViewById(R.id.tv_state);
        this.setVisibility(GONE);
    }

    @LayoutRes
    public int getLayout() {
        return R.layout.view_state;
    }

    @Override
    public void onNoData() {
        Log.d(TAG, "onNoData: ");
        if (getVisibility() == GONE) {
            this.setVisibility(VISIBLE);
        }
        mTvState.setText("暂无数据");
    }

    @Override
    public void onError(String msg) {
        Log.d(TAG, "onError: ");
        if (getVisibility() == GONE) {
            this.setVisibility(VISIBLE);
        }
        mTvState.setText((TextUtils.isEmpty(msg)) ? "网络异常" : msg);
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "onSuccess: ");
        this.setVisibility(GONE);
    }

    public ImageView getIvStatePic() {
        return mIvStatePic;
    }

    public TextView getTvState() {
        return mTvState;
    }
}
