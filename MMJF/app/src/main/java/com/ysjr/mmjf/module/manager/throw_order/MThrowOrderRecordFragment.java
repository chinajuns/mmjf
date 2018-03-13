package com.ysjr.mmjf.module.manager.throw_order;

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
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.MThrowOrderBean;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.manager.adapter.ThrowOrderAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-28.
 */

public class MThrowOrderRecordFragment extends BaseFragment {
  private static final String KEY_ORDER_STATUS = "order_status";
  private static final int REFRESHING = 1;
  private static final int LOAD_MORE = 2;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  private long mCreateTime = -1;
  private List<MThrowOrderBean> mOrderList = new ArrayList<>();
  private ThrowOrderAdapter mAdapter;
  private int status;
  public static MThrowOrderRecordFragment newInstance(int status) {
    MThrowOrderRecordFragment fg = new MThrowOrderRecordFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_ORDER_STATUS, status);
    fg.setArguments(bundle);
    return fg;
  }
  @Override public int getLayoutId() {
    return R.layout.m_fragment_throw_order_record;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    status = getArguments().getInt(KEY_ORDER_STATUS);
    initRv();
    initRefresh();
    httpGetData(REFRESHING);
  }

  private void initRv() {
    mAdapter = new ThrowOrderAdapter(R.layout.m_throw_order_item, mOrderList);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_ORDER_JUNK_AGAIN)
            .tag(mContext)
            .params("id",mOrderList.get(position).id)
            .execute(new DialogCallback<HttpResult<Void>>(mContext) {
              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                mAdapter.notifyItemRemoved(position);
              }
            });
      }
    });
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, MThrowDetailActivity.class);
        intent.putExtra(MThrowDetailActivity.KEY_THROW_ID, mOrderList.get(position).id);
        ActivityUtils.startActivity(intent);
      }
    });
  }

  private void initRefresh() {
    loadingLayout.setEmptyImage(R.drawable.ic_filter_empty).setEmptyText(getString(R.string.no_data));
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        httpGetData(REFRESHING);
        refreshlayout.setLoadmoreFinished(false);
      }
    });
    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        httpGetData(LOAD_MORE);
      }
    });
  }

  private void httpGetData(final int refreshType) {
    PostRequest<HttpResult<List<MThrowOrderBean>>> request =
        OkGo.<HttpResult<List<MThrowOrderBean>>>post(Api.MOBILE_BUSINESS_ORDER_JUNK_LIST).tag(
            mContext).params("status", status);
    if (refreshType == LOAD_MORE) {
      request.params("create_time", mCreateTime);
    }
    request.execute(new JsonCallback<HttpResult<List<MThrowOrderBean>>>() {
      @Override public void onSuccess(Response<HttpResult<List<MThrowOrderBean>>> response) {
        loadingLayout.setStatus(LoadingLayout.Success);
        List<MThrowOrderBean> beanList = response.body().data;
        switch (refreshType) {
          case REFRESHING:
            mOrderList.clear();
            refreshLayout.finishRefresh();
            break;
          case LOAD_MORE:
            refreshLayout.finishLoadmore();
            break;
        }
        mOrderList.addAll(beanList);
        mAdapter.notifyDataSetChanged();
      }

      @Override public void onError(Response<HttpResult<List<MThrowOrderBean>>> response) {
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
}
