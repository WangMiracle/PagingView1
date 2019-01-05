package com.miracle.pagingview;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.miracle.pagingview.listener.ConvertResponseListener;
import com.miracle.pagingview.listener.FilterInterface;
import com.miracle.pagingview.listener.IStateListener;
import com.miracle.pagingview.listener.OnLoadMoreListener;
import com.miracle.pagingview.listener.OnRefreshListener;
import com.miracle.pagingview.listener.RequestDataListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.List;

/**
 * PagingView.java
 * (c)2018 AIR Times Inc. All rights reserved.
 * 分页列表
 * <p>
 * 目前支持两种数据格式:
 * <pre>
 *     第一种:{"content":{ "rows":["name":"admin"]}}
 *     BaseResponse<Data>类型对应的Observer：
 *     <code>
 *          PagingView pagingView = new PagingView.Builder<Data,Response>(context, viewgroup)
 // 设置数据
 .setList(data)
 // 设置适配器
 .setAdapter(adapter)
 // 默认开启,如果没有额外需求可以不进行设置
 .setLoadMoreEnable(true)
 // 默认开启,如果没有额外需求可以不进行设置
 .setRefreshEnable(true)
 // 只有在网络请求时调用
 .setRequestListener({@link RequestDataListener})
 // 数据格式转换
 .setConvertResponseListener(baseResponse -> baseResponse.getResponse().getContent().getData())
 .build();
 // 只有在网络请求时调用
 pagingView.startRequest();
 *     </code>
 *     第二种:{"content":["name":"admin"]}
 *     BaseListResponse<Data>类型对应的Observer：
 *     <code>
 PagingView pagingView = new PagingView.Builder<Data,Object>(context, viewgroup)
 // 设置数据
 .setList(data)
 // 设置适配器
 .setAdapter(adapter)
 // 默认开启,如果没有额外需求可以不进行设置
 .setLoadMoreEnable(true)
 // 默认开启,如果没有额外需求可以不进行设置
 .setRefreshEnable(true)
 // 只有在网络请求时调用
 .setRequestListener({@link RequestDataListener})//只要请求网络数据，必须实现)
 .build();
 // 只有在网络请求时调用
 pagingView.startRequest();
 *     </code>
 * </pre>
 *
 * @author WangJQ
 * @version 1.0
 * @date 2018/7/18 19:00
 */
public class PagingView<Data, Response> extends LinearLayout implements OnLoadmoreListener,
        com.scwang.smartrefresh.layout.listener.OnRefreshListener {
    private View mPagingViewContainer;

    private Context mContext;
    private SmartRefreshLayout mSmartRefreshLayout;

    private List<Data> mArrayList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mItemDecoration;

    /**
     * so much listener
     * 请求数据
     */
    private RequestDataListener<Data, Response> mRequestListener;
    private ConvertResponseListener<Data, Response> mConvertResponseListener;
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private FilterInterface<Data> mFilterInterface;
    private IStateListener<Data> mStateListener;

    private boolean mIsLoadMore = false;
    private int mCurrPage = 1;
    private int mPageSize;
    private boolean mEnableRefresh;
    private boolean mEnableLoadMore;
    private ViewGroup mContainer;
    private int mFirstPageSize;
    private int mBackgroundColor;
    private int mStateViewType;

//    /**
//     * 自定义BaseResponse的Observer
//     */
//    RxDefaultObserver<BaseResponse<Response>> mObserver;
//    /**
//     * BaseListResponse的通用Observer
//     */
//    RxDefaultObserver<BaseListResponse<Data>> mCommonObserver;

    /**
     * 状态布局控制器
     */
    StateViewManager mStateViewManager;
    LayoutInflater mLayoutInflater;
    StateViewBuilder mStateViewBuilder;

    private boolean mShowLoading = true;

    public PagingView(Context context) {
        super(context);
    }

    private PagingView(Builder<Data, Response> builder) {
        super(builder.mContext);
        mContext = builder.mContext;
        mEnableRefresh = builder.mEnableRefresh;
        mEnableLoadMore = builder.mEnableLoadMore;
        mAdapter = builder.mAdapter;
        mArrayList = builder.mList;
        mLayoutManager = builder.mLayoutManager;
        mPageSize = builder.mPageSize;
        mContainer = builder.mContainer;
        mFirstPageSize = builder.mFirstPageSize;
        mStateViewBuilder = builder.mStateViewBuilder;
        mLayoutInflater = LayoutInflater.from(mContext);
        mItemDecoration = builder.mItemDecoration;
        mStateViewType = builder.mStateType;

        mRequestListener = builder.mRequestDataListener;
        mFilterInterface = builder.mFilterInterface;
        mConvertResponseListener = builder.mConvertResponseListener;
        mOnRefreshListener = builder.mOnRefreshListener;
        mOnLoadMoreListener = builder.mOnLoadMoreListener;
        mBackgroundColor = builder.mBackgroundColor;
        mStateListener = builder.mStateListener;

        initView();
        initManager();
        setParams();
        setRecyclerView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mPagingViewContainer = mLayoutInflater.inflate(R.layout.view_paging, this);
        mSmartRefreshLayout = mPagingViewContainer.findViewById(R.id.smr);
        mRecyclerView = mPagingViewContainer.findViewById(R.id.rv);

        mSmartRefreshLayout.setOnLoadmoreListener(this);
        mSmartRefreshLayout.setOnRefreshListener(this);

        if (mContainer != null) {
            mContainer.addView(this);
        }
    }

    /**
     * 初始化管理器
     */
    private void initManager() {
        mStateViewManager = new StateViewManager(mContext, mPagingViewContainer,
                mStateViewBuilder, mSmartRefreshLayout, mStateListener, mStateViewType);
    }


    /**
     * 设置相关参数
     */
    private void setParams() {
        mSmartRefreshLayout.setEnableRefresh(mEnableRefresh);
        mSmartRefreshLayout.setEnableLoadmore(mEnableLoadMore);
        if (!mEnableRefresh && !mEnableLoadMore) {
            mSmartRefreshLayout.setEnabled(false);
        }
        if (mBackgroundColor != -1) {
            mRecyclerView.setBackgroundColor(ContextCompat.getColor(mContext, mBackgroundColor));
        }
    }

    /**
     * 列表初始化
     */
    private void setRecyclerView() {
        if (mItemDecoration != null) {
            mRecyclerView.addItemDecoration(mItemDecoration);
        }

        RecyclerView.LayoutManager layoutManager = getLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 提供RecyclerView
     *
     * @return recycleView
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 获取状态管理器
     */
    public StateViewManager getStateViewManager() {
        return mStateViewManager;
    }

    /**
     * 获取状态View
     *
     * @return 状态布局view
     */
    public View getStateLayout() {
        return mStateViewManager.getStateLayout();
    }

    /**
     * 初始化请求网络需要的Observer
     * 如果调用此方法，需要实现ConvertResponseListener
     */
//    private void initObserver() {
//        mObserver = new
//                RxDefaultObserver<BaseResponse<Response>>(mContext, mShowLoading) {
//
//                    @Override
//                    public void onSucceed(BaseResponse<Response> arrayListBaseResponse) {
//                        PagingView.this.onComplete(arrayListBaseResponse, mConvertResponseListener
//                                .observerAdapter(arrayListBaseResponse));
//                    }
//
//                    @Override
//                    public void onFailed(BaseResponse.ResponseBean errorBean) {
//                        super.onFailed(errorBean);
//                        PagingView.this.onComplete(null, null);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        PagingView.this.onError("");
//                    }
//                };
//    }

//    /**
//     * 通用List Observer
//     */
//    private void initCommonObserver() {
//
//        mCommonObserver = new
//                RxDefaultObserver<BaseListResponse<Data>>(mContext, mShowLoading) {
//
//                    @Override
//                    public void onSucceed(BaseListResponse<Data>
//                                                  arrayListBaseResponse) {
//                        PagingView.this.onComplete(arrayListBaseResponse, arrayListBaseResponse
//                                .getResponse().getContent());
//                    }
//
//                    @Override
//                    public void onFailed(BaseResponse.ResponseBean errorBean) {
//                        super.onFailed(errorBean);
//                        PagingView.this.onComplete(null, null);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        PagingView.this.onError("");
//                    }
//                };
//    }

    /**
     * 开始请求数据
     */
    public void startRequest() {
        mRequestListener.requestData(this);
    }

    /**
     * 获取RecyclerView的布局方式，默认返回LinearLayoutManager
     *
     * @param context 上下文
     * @return mLayoutManager
     */
    private RecyclerView.LayoutManager getLayoutManager(Context context) {
        if (mLayoutManager == null) {
            return new LinearLayoutManager(context);
        }
        return mLayoutManager;
    }

    /**
     * 获取是否为加载更多
     *
     * @return 是否为加载更多
     */
    public boolean isLoadMore() {
        return mIsLoadMore;
    }

    /**
     * 获取当前页数
     *
     * @return currPage
     */
    public int getCurrPage() {
        return mCurrPage;
    }

    /**
     * 返回每页加载数量
     *
     * @return pageSize
     */
    public int getPageSize() {
        return (mCurrPage == 1) ? mFirstPageSize : mPageSize;
    }

//    /**
//     * 获取自定义结构体的Observer
//     *
//     * @return 可扩展Observer
//     */
//    public RxDefaultObserver<BaseResponse<Response>> getObserver() {
//        initObserver();
//        return mObserver;
//    }
//
//    /**
//     * 获取List<Data>的通用Observer
//     *
//     * @return 通用Observer
//     */
//    public RxDefaultObserver<BaseListResponse<Data>> getCommonObserver() {
//        initCommonObserver();
//        return mCommonObserver;
//    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        mShowLoading = false;
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
        mCurrPage++;
        mIsLoadMore = true;
        startRequest();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
        doRefresh();
    }

    /**
     * 刷新
     */
    public void doRefresh() {
        mShowLoading = false;
        mSmartRefreshLayout.setEnableLoadmore(mEnableLoadMore);
        mSmartRefreshLayout.setLoadmoreFinished(false);
        mCurrPage = 1;
        mIsLoadMore = false;
        startRequest();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 状态控制，分发状态
     *
     * @param list         列表数据
     */
    public void onComplete(List<Data> list) {

        if (list != null && list.size() > 0) {
            //处理状态-控制状态界面
            if (list.size() < mPageSize) {
                mStateViewManager.onNoLoadMoreState();
            } else {
                mStateViewManager.onSuccessState(mEnableLoadMore);
            }
            //处理数据
            if (isLoadMore()) {
                loadMoreSucc(list);
            } else {
                getListSucc(list);
            }
        } else {
            if (isLoadMore()) {
                mStateViewManager.onNoLoadMoreState();
            } else {
                getListSucc(null);
                mStateViewManager.onNoDataState();
            }
        }
    }

    /**
     * 错误信息
     *
     * @param msg 错误消息
     */
    public void onError(String msg) {
        mStateViewManager.onErrorState(msg);
    }

    /**
     * 刷新成功，填充数据
     *
     * @param list 数据
     */
    private void getListSucc(List<Data> list) {
        mArrayList.clear();
        loadMoreSucc(list);
    }

    /**
     * 加载更多成功，填充数据
     *
     * @param list 数据
     */
    private void loadMoreSucc(List<Data> list) {

        if (mFilterInterface != null && list != null) {
            mArrayList.addAll(mFilterInterface.filterData(list));
        } else if (list != null) {
            mArrayList.addAll(list);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 构造器
     *
     * @param <Data>     列表item
     * @param <Response> BaseResponse的泛型，如果调用{传Object就可以}
     */
    public static class Builder<Data, Response> {
        private Context mContext;
        private ViewGroup mContainer;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private RecyclerView.ItemDecoration mItemDecoration;
        private List<Data> mList;

        private RequestDataListener<Data, Response> mRequestDataListener;
        private FilterInterface<Data> mFilterInterface;
        private OnRefreshListener mOnRefreshListener;
        private OnLoadMoreListener mOnLoadMoreListener;
        private ConvertResponseListener<Data, Response> mConvertResponseListener;
        private IStateListener<Data> mStateListener;

        private boolean mEnableRefresh = Config.DEFAULT_REFRESH;
        private boolean mEnableLoadMore = Config.DEFAULT_LOAD_MORE;
        private int mPageSize = Config.PAGE_SIZE;
        private int mFirstPageSize = Config.INIT_PAGE_SIZE;
        private StateViewBuilder mStateViewBuilder;
        private int mBackgroundColor = -1;
        private int mStateType;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 初始化
         *
         * @param context   上下文
         * @param container pagingView的父布局，需要在自己的activity中定义
         */
        public Builder(Context context, ViewGroup container) {
            mContainer = container;
            mContext = context;
        }

        /**
         * 适配器
         *
         * @param adapter 适配器
         * @return builder
         */
        public Builder<Data, Response> setAdapter(RecyclerView.Adapter adapter) {
            mAdapter = adapter;
            return this;
        }

        /**
         * 设置数据列表
         *
         * @param list 数据列表
         * @return builder
         */
        public Builder<Data, Response> setList(List<Data> list) {
            mList = list;
            return this;
        }

        /**
         * 是否能加载刷新，默认为true
         *
         * @param enable 加载更多
         * @return builder
         */
        public Builder<Data, Response> setRefreshEnable(boolean enable) {
            mEnableRefresh = enable;
            return this;
        }

        /**
         * 是否能加载更多，默认为true
         *
         * @param enable 是否加载更多
         * @return builder
         */
        public Builder<Data, Response> setLoadMoreEnable(boolean enable) {
            mEnableLoadMore = enable;
            return this;
        }

        /**
         * 设置布局方式,默认是{@link LinearLayoutManager}
         *
         * @param layoutManager layoutManager
         * @return builder
         */
        public Builder<Data, Response> setLayoutManager(RecyclerView.LayoutManager layoutManager) {
            mLayoutManager = layoutManager;
            return this;
        }

        /**
         * 设置每页加载数目
         *
         * @param pageSize 每页数
         * @return builder
         */
        public Builder<Data, Response> setPageSize(int pageSize) {
            mPageSize = pageSize;
            return this;
        }

        /**
         * 设置此接口，获取刷新
         *
         * @param refreshListener 下拉刷新监听
         * @return builder
         */
        public Builder<Data, Response> setOnRefreshListener(OnRefreshListener refreshListener) {
            mOnRefreshListener = refreshListener;
            return this;
        }

        /**
         * 设置此接口加载更多的动作
         *
         * @param loadMoreListener 加载更多状态监听
         * @return builder
         */
        public Builder<Data, Response> setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
            mOnLoadMoreListener = loadMoreListener;
            return this;
        }

        /**
         * 需要实现请求数据的接口
         * 必须实现，不然无法获取网络数据
         *
         * @param requestListener 请求数据listener
         * @return builder
         */
        public Builder<Data, Response> setRequestListener(RequestDataListener<Data, Response>
                                                                  requestListener) {
            mRequestDataListener = requestListener;
            return this;
        }

        /**
         * 设置转换response
         * <Desc>如果你的数据类型不是BaseListResponse<Data>,那么需要实现这个接口</Desc>
         *
         * @param convertResponseListener 转换
         * @return builder
         */
        public Builder<Data, Response> setConvertResponseListener(ConvertResponseListener<Data,
                Response>
                                                                          convertResponseListener) {
            mConvertResponseListener = convertResponseListener;
            return this;
        }

        /**
         * 过滤list
         * 如果对服务器返回数据有重组或者特殊需求，可实现此接口
         *
         * @param filterInterface 过滤list
         * @return builder
         */
        public Builder<Data, Response> setFilterInterface(FilterInterface<Data> filterInterface) {
            mFilterInterface = filterInterface;
            return this;
        }

        /**
         * 设置状态监听回调
         * @param stateListener
         * @return
         */
        public Builder<Data, Response> setStateListener(IStateListener<Data> stateListener) {
            mStateListener = stateListener;
            return this;
        }

        /**
         * 设置此布局外层的View
         *
         * @param container 外层的view
         * @return builder
         */
        public Builder<Data, Response> setContainer(ViewGroup container) {
            mContainer = container;
            return this;
        }

        /**
         * 设置第一页加载的数据数量，如不设置，默认为{@link #mPageSize}
         *
         * @param firstPageSize 第一页加载的数量
         * @return
         */
        public Builder<Data, Response> setFirstPageSize(@IntRange int firstPageSize) {
            mFirstPageSize = firstPageSize;
            return this;
        }

        /**
         * 设置分割线
         *
         * @param itemDecoration
         * @return
         */
        public Builder<Data, Response> setItemDecoration(RecyclerView.ItemDecoration
                                                                 itemDecoration) {
            mItemDecoration = itemDecoration;
            return this;
        }

        /**
         * 自定义状态布局
         *
         * @param stateViewBuilder 状态布局builder
         * @return builder
         */
        public Builder<Data, Response> setStateConfig(StateViewBuilder stateViewBuilder) {
            mStateViewBuilder = stateViewBuilder;
            return this;
        }

        /**
         * 设置背景色
         *
         * @param color 背景色
         * @return builder
         */
        public Builder<Data, Response> setBackgroundColor(int color) {
            mBackgroundColor = color;
            return this;
        }

        /**
         * 设置状态布局类型
         * @param type {@link }
         * @return
         */
        public Builder<Data, Response> setStateViewType(int type) {
            mStateType = type;
            return this;
        }

        public PagingView<Data, Response> build() {
            return new PagingView<>(this);
        }
    }

    /**
     * 状态布局构造
     */
    public static class StateViewBuilder {
        /**
         * 布局Id
         */
        public int mStateLayoutId;
        /**
         * 图标Id
         */
        public int mIconId;
        /**
         * 状态字布局Id
         */
        public int mTextViewId;
        /**
         * 资源文件Id
         */
        public int mIcon;
        /**
         * 字string
         */
        public String mStateText;

        /**
         * 设置布局id
         *
         * @param id 布局id
         * @return builder
         */
        public StateViewBuilder setLayoutId(int id) {
            mStateLayoutId = id;
            return this;
        }

        /**
         * 设置图标id
         *
         * @param id 图标
         * @return builder
         */
        public StateViewBuilder setIconId(int id) {
            mIconId = id;
            return this;
        }

        /**
         * 设置文字id
         *
         * @param id 文字id
         * @return builder
         */
        public StateViewBuilder setTextLayoutId(int id) {
            mTextViewId = id;
            return this;
        }

        /**
         * 设置图标 占位图
         *
         * @param iconId
         * @return builder
         */
        public StateViewBuilder setIcon(int iconId) {
            mIcon = iconId;
            return this;
        }

        /**
         * 设置状态文字
         *
         * @param text 文字
         * @return builder
         */
        public StateViewBuilder setStateText(String text) {
            mStateText = text;
            return this;
        }

        public StateViewBuilder builder() {
            return this;
        }
    }
}
