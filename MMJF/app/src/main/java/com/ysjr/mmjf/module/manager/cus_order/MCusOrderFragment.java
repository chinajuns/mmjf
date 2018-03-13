package com.ysjr.mmjf.module.manager.cus_order;

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
import com.lzy.okgo.request.PostRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.Customer;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.manager.adapter.MCusOrderAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MCusOrderFragment extends BaseFragment {
  private static final String TYPE_KEY = "type";
  private static final String KEY_REFER = "refer";
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  private List<Customer> mCustomerList = new ArrayList<>();
  private MCusOrderAdapter mOrderAdapter;
  public static final int TYPE_ALL = -1;
  public static final int TYPE_PROCESSING = 0;
  public static final int TYPE_EVALUATING = 1;
  public static final int TYPE_LOAN_RECORDING = 2;
  private static final int REFRESHING = 100;
  private static final int LOAD_MORE = 101;
  private int status = -1;
  private long create_time;
  private String refer;
  public static MCusOrderFragment newInstance(int type,String customer) {
    MCusOrderFragment fg = new MCusOrderFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(TYPE_KEY,type);
    bundle.putString(KEY_REFER,customer);
    fg.setArguments(bundle);
    return fg;
  }
  @Override public int getLayoutId() {
    return R.layout.c_fragment_my_order;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    status = getArguments().getInt(TYPE_KEY, -1);
    refer = getArguments().getString(KEY_REFER);
    initRv();
    initRefresh();
  }

  @Override public void onResume() {
    super.onResume();
    initData();
  }

  private void initRefresh() {
    loadingLayout.setEmptyImage(R.drawable.ic_record_empty).setEmptyText(getString(R.string.no_data));
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        create_time = 0;
        refreshlayout.setLoadmoreFinished(false);
        httpGetData(REFRESHING);
      }
    });
    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        create_time = mCustomerList.get(mCustomerList.size() - 1).create_time;
        httpGetData(LOAD_MORE);
      }
    });
  }

  private void initData() {
    httpGetData(REFRESHING);
  }

  private void httpGetData(final int refreshType) {
    PostRequest<HttpResult<List<Customer>>> request;
    if (refreshType == REFRESHING) {
      request = OkGo.<HttpResult<List<Customer>>>post(Api.MOBILE_BUSINESS_SHOP_ORDER).tag(mContext)
          .params("status", status)
          .params("refer", refer);
    } else {
      request = OkGo.<HttpResult<List<Customer>>>post(Api.MOBILE_BUSINESS_SHOP_ORDER).tag(mContext)
          .params("status", status)
          .params("create_time",create_time)
          .params("refer", refer);
    }
    request.execute(new JsonCallback<HttpResult<List<Customer>>>() {
      @Override public void onSuccess(Response<HttpResult<List<Customer>>> response) {
        loadingLayout.setStatus(LoadingLayout.Success);
        List<Customer> beanList = response.body().data;
        switch (refreshType) {
          case REFRESHING:
            refreshLayout.finishRefresh();
            mCustomerList.clear();
            break;
          case LOAD_MORE:
            refreshLayout.finishLoadmore();
            break;
        }
        mCustomerList.addAll(beanList);
        mOrderAdapter.notifyDataSetChanged();
      }

      @Override public void onError(Response<HttpResult<List<Customer>>> response) {
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
    mOrderAdapter = new MCusOrderAdapter(R.layout.m_cus_order_item, mCustomerList,refer);
    recyclerView.setAdapter(mOrderAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mOrderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, MCusOrderDetailActivity.class);
        intent.putExtra(MCusOrderDetailActivity.KEY_ORDER_ID, mCustomerList.get(position).id);
        intent.putExtra(MCusOrderDetailActivity.KEY_REFER, refer);
        ActivityUtils.startActivity(intent);
      }
    });
    mOrderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, MEvaluateActivity.class);
        intent.putExtra(MCusOrderDetailActivity.KEY_ORDER_ID, mCustomerList.get(position).id);
        ActivityUtils.startActivity(intent);
      }
    });
  }
}
