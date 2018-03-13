package com.ysjr.mmjf.module.customer.me.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.COrder;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.MyOrderAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CMyOrderFragment extends BaseFragment {
  private static final String TYPE_KEY = "type";
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  private List<COrder.DataItem> mManagerList = new ArrayList<>();
  private MyOrderAdapter mOrderAdapter;
  public static final int TYPE_ALL = 0;
  public static final int TYPE_PROCESSING = 1;
  public static final int TYPE_EVALUATING = 2;
  public static final int TYPE_LOAN_RECORDING = 3;
  public static final int REFRESHING = 100;
  public static final int LOAD_MORE = 101;
  private int mType;//是哪个页面
  private int mPageCurrent = 1;

  public static CMyOrderFragment newInstance(int type) {
    CMyOrderFragment fg = new CMyOrderFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(TYPE_KEY,type);
    fg.setArguments(bundle);
    return fg;
  }
  @Override public int getLayoutId() {
    return R.layout.c_fragment_my_order;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    mType = getArguments().getInt(TYPE_KEY);
    initRv();
    initRefreshLayout();
  }

  @Override public void onResume() {
    super.onResume();
    initData();
  }

  private void initRefreshLayout() {
    loadingLayout.setEmptyText(getString(R.string.no_data));
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        httpGetOrderData(REFRESHING);
        refreshlayout.setLoadmoreFinished(false);
      }
    });

    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        httpGetOrderData(LOAD_MORE);
      }
    });
  }

  private void initData() {
    httpGetOrderData(REFRESHING);
  }

  private void httpGetOrderData(final int refreshType) {
    if (refreshType == REFRESHING) {
      mPageCurrent = 1;
    } else {
      mPageCurrent++;
    }
    OkGo.<HttpResult<COrder>>post(Api.MOBILE_CLIENT_MEMBER_HISTORY)
        .tag(this)
        .params("type",mType)
        .params("page",mPageCurrent)
        .execute(new JsonCallback<HttpResult<COrder>>() {
          @Override public void onSuccess(Response<HttpResult<COrder>> response) {
            loadingLayout.setStatus(LoadingLayout.Success);
            List<COrder.DataItem> beanList = response.body().data.data;
            switch (refreshType) {
              case REFRESHING:
                mManagerList.clear();
                refreshLayout.finishRefresh();
                break;
              case LOAD_MORE:
                refreshLayout.finishLoadmore();
                break;
            }
            mManagerList.addAll(beanList);
            mOrderAdapter.notifyDataSetChanged();
          }

          @Override public void onError(Response<HttpResult<COrder>> response) {
            super.onError(response);
            String errorStatus = null;
            if (response.getException() != null) {
              errorStatus = response.getException().getMessage();
            }
            if (HttpError.EMPTY.equals(errorStatus)) {
              switch (refreshType) {
                case REFRESHING:
                  refreshLayout.finishRefresh();
                  loadingLayout.setStatus(LoadingLayout.Empty);
                  break;
                case LOAD_MORE:
                  refreshLayout.finishLoadmore();
                  refreshLayout.setLoadmoreFinished(true);
                  break;
              }
            }
          }
        });
  }

  private void initRv() {
    mOrderAdapter = new MyOrderAdapter(R.layout.c_my_order_item, mManagerList);
    recyclerView.setAdapter(mOrderAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mOrderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, COrderDetailActivity.class);
        intent.putExtra(COrderDetailActivity.KEY_ORDER_DATA_ITEM, mManagerList.get(position));
        ActivityUtils.startActivity(intent);
      }
    });
    mOrderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, COrderEvaluateActivity.class);
        intent.putExtra(COrderDetailActivity.KEY_ORDER_DATA_ITEM, mManagerList.get(position));
        ActivityUtils.startActivity(intent);
      }
    });
  }
}
