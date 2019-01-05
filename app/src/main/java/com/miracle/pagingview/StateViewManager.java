package com.miracle.pagingview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miracle.pagingview.listener.IStateListener;
import com.miracle.pagingview.view.BackdropStateView;
import com.miracle.pagingview.view.BaseStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import static com.miracle.pagingview.Config.STATEVIEW_TYPE_BACKDROP;


/**
 * com.air.basecommon.view.paging
 * (c)2018 AIR Times Inc. All rights reserved.
 * 状态管理器
 *
 * @author WangJQ
 * @version 1.0
 * @date 2018/9/6 13:56
 */
public class StateViewManager<T> {
    public static final String TAG = "StateViewManager";

    private View mPagingViewContainer;
    private SmartRefreshLayout mSmartRefreshLayout;
    /**
     * 状态布局（无数据、无网络）
     */
    private LinearLayout mStateContainer;
    private View mCustomStateLayout;
    private ImageView mIvState;
    private TextView mTvState;
    private String mNoDataMsg = "暂无数据";
    private String mErrorMsg = "网络异常";
    private IStateListener<T> mStateListener;
    private BaseStateView mStateView;

    private int mNoDataResIcon;
    private int mErrorResIcon;

    private PagingView.StateViewBuilder mStateViewBuilder;
    private Context mContext;
    private int mStateViewType = -1;

    /**
     * 状态布局管理
     * @param pagingViewContainer paging view
     * @param stateViewBuilder 状态属性构造
     * @param mStateViewType
     */
    public StateViewManager(Context context, View pagingViewContainer, PagingView.StateViewBuilder
            stateViewBuilder, SmartRefreshLayout smartRefreshLayout, IStateListener<T>
                                    iStateListener, int mStateViewType) {
        mContext = context;
        mSmartRefreshLayout = smartRefreshLayout;
        mPagingViewContainer = pagingViewContainer;
        mStateViewBuilder = stateViewBuilder;
        mStateListener = iStateListener;
        this.mStateViewType = mStateViewType;
        initStateLayout();
    }

    /**
     * 状态布局初始化
     */
    private void initStateLayout() {
        //状态布局容器
        mStateContainer = mPagingViewContainer.findViewById(R.id.ll_state_container);
        if (mStateViewBuilder != null) {
            if (mStateViewBuilder.mStateLayoutId != 0) {
                mCustomStateLayout = LayoutInflater.from(mContext).inflate(mStateViewBuilder
                        .mStateLayoutId, null);
                mIvState = mCustomStateLayout.findViewById(mStateViewBuilder.mIconId);
                mTvState = mCustomStateLayout.findViewById(mStateViewBuilder.mTextViewId);
                //移出默认状态布局，add自定义布局
                addToContainer(mCustomStateLayout);
            } else {
                initDefaultLayout();
            }
            //自定义文字
            mNoDataMsg = (TextUtils.isEmpty(mStateViewBuilder.mStateText) ? mNoDataMsg :
                    mStateViewBuilder.mStateText);
        } else {
            //默认
            initDefaultLayout();
        }
        //TODO
        mStateContainer.setVisibility(View.GONE);
    }

    /**
     * 获取状态布局实例
     * @return
     */
    public View getStateLayout() {
        if (mStateView != null) {
            return mStateView;
        } else {
            return mCustomStateLayout;
        }
    }

    /**
     * 无数据状态
     */
    public void onNoDataState() {
        Log.d(TAG, "onNoDataState");
        setLoadingState(false);
        closeSmartRefreshLayout();
        mSmartRefreshLayout.setEnableLoadmore(false);
        setStateLayoutVisiable(View.VISIBLE);
        setStateText(true);
        setStateRes(true);
        if (mStateView != null) {
            mStateView.onNoData();
        }
        if (mStateListener != null) {
            mStateListener.onNoData();
        }
    }

    /**
     * 没有网络状态
     * @param errorMsg
     */
    public void onErrorState(String errorMsg) {
        Log.d(TAG, "onErrorState: " + errorMsg);
        setLoadingState(false);
        closeSmartRefreshLayout();
        setStateLayoutVisiable(View.VISIBLE);
        setStateText(false);
        setStateRes(false);
        if (mStateView != null) {
            mStateView.onError(errorMsg);
        }
        if (mStateListener != null) {
            mStateListener.onError(errorMsg);
        }
    }

    /**
     * 成功
     */
    public void onSuccessState(boolean enableLoadMore) {
        closeSmartRefreshLayout();
        setStateLayoutVisiable(View.GONE);
        setLoadingState(false);
        if (mStateView != null) {
            mStateView.onSuccess();
        }
        if (mStateListener != null) {
            mStateListener.onSuccess(null);
        }
    }

    /**
     * 没有更多数据
     */
    public void onNoLoadMoreState() {
        onSuccessState(false);
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    /**
     * 关闭刷新布局
     */
    private void closeSmartRefreshLayout() {
        if (mSmartRefreshLayout != null) {
            //关闭刷新
            mSmartRefreshLayout.finishRefresh();
            //关闭加载更多
            mSmartRefreshLayout.finishLoadmore();
        }
    }

    /**
     * 设置状态布局可见性
     * @param visiable {@link }
     */
    private void setStateLayoutVisiable(int visiable) {
        if (mStateContainer != null) {
            mStateContainer.setVisibility(visiable);
        }
        if (mCustomStateLayout != null) {
            mCustomStateLayout.setVisibility(visiable);
        }
    }

    /**
     * 设置刷新状态
     * @param isLoading
     */
    private void setLoadingState(boolean isLoading) {
        //TODO 目前用系统默认的刷新
//        if (mLoadingLayout == null) {
//            return;
//        }
//
//        if (isLoading) {
//            mLoadingLayout.setVisibility(View.VISIBLE);
//        } else {
//            mLoadingLayout.setVisibility(View.GONE);
//        }
    }

    /**
     * 设置状态图片
     * @param isNodaState 是无数据状态
     */
    private void setStateRes(boolean isNodaState) {
        if (isNodaState) {
            if (mIvState != null) {
                mIvState.setImageResource(mNoDataResIcon);
            }
        } else {
            if (mIvState != null) {
                mIvState.setImageResource(mErrorResIcon);
            }
        }
    }

    /**
     * 设置状态文字
     * @param isNodaState 是无数据状态
     */
    private void setStateText(boolean isNodaState) {
        if (isNodaState) {
            if (mTvState != null) {
                mTvState.setText(mNoDataMsg);
            }
        } else {
            if (mTvState != null) {
                mTvState.setText(mErrorMsg);
            }
        }
    }

    /**
     * 初始化默认的状态布局
     */
    private void initDefaultLayout() {
        if (mStateViewType == STATEVIEW_TYPE_BACKDROP) {
            mStateView = new BackdropStateView(mContext);
        } else {
            mStateView = new BaseStateView(mContext);
        }
        addToContainer(mStateView);
    }

    /**
     * 添加到容器中
     * @param stateView
     */
    private void addToContainer(View stateView) {
        mStateContainer.removeAllViews();
        mStateContainer.addView(stateView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
    }
}
