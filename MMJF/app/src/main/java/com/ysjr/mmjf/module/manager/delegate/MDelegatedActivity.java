package com.ysjr.mmjf.module.manager.delegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
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
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.ProBean;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.dialogfragment.SureOrCancelFragment;
import com.ysjr.mmjf.module.manager.adapter.MDelegateAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MDelegatedActivity extends TopBarBaseActivity {
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  private List<ProBean> mProList = new ArrayList<>();
  private MDelegateAdapter mAdapter;
  private int mPosition = -1;
  private static final int REFRESHING = 1;
  private static final int LOAD_MORE = 2;
  private long create_time;

  @Override protected int getContentView() {
    return R.layout.m_activity_delegated;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.delegated_pro));
    initRefresh();
    initRv();
    EventBus.getDefault().register(this);
  }

  @Override protected void onResume() {
    super.onResume();
    initData();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  private void initData() {
    httpGetData(REFRESHING);
  }

  private void initRefresh() {
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        create_time = 0;
        refreshlayout.setLoadmoreFinished(false);
        httpGetData(REFRESHING);
      }
    });
    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        create_time = mProList.get(mProList.size() - 1).create_time;
        httpGetData(LOAD_MORE);
      }
    });
  }

  private void httpGetData(final int refreshType) {
    OkGo.<HttpResult<List<ProBean>>>post(Api.MOBILE_BUSINESS_PRODUCT_MY_PRODUCT)
        .tag(mContext)
        .params("type","mine")
        .params("create_time",create_time)
        .execute(new JsonCallback<HttpResult<List<ProBean>>>() {
      @Override public void onSuccess(Response<HttpResult<List<ProBean>>> response) {
        loadingLayout.setStatus(LoadingLayout.Success);
        List<ProBean> beanList = response.body().data;
        switch (refreshType) {
          case REFRESHING:
            mProList.clear();
            refreshLayout.finishRefresh();
            break;
          case LOAD_MORE:
            refreshLayout.finishLoadmore();
            break;
        }
        mProList.addAll(beanList);
        mAdapter.notifyDataSetChanged();
      }

      @Override public void onError(Response<HttpResult<List<ProBean>>> response) {
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
    mAdapter = new MDelegateAdapter(R.layout.m_delegate_item, mProList,false);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        SureOrCancelFragment fg =
            SureOrCancelFragment.newInstance(getString(R.string.sure_cancel_delegate));
        fg.show(getSupportFragmentManager(),"dialog");
        mPosition = position;
      }
    });
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, MDelegateDetailActivity.class);
        intent.putExtra(MDelegateDetailActivity.KEY_IS_AGENTED, 1);
        intent.putExtra(MDelegateDetailActivity.KEY_ID, mProList.get(position).id);
        ActivityUtils.startActivity(intent);
      }
    });
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onSureClicked(String desc) {
    ProBean bean = mProList.get(mPosition);
    OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_PRODUCT_SET_AGENT)
        .tag(mContext)
        .params("id",bean.id)
        .params("action","cancel")
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            mProList.remove(mPosition);
            mAdapter.notifyItemRemoved(mPosition);
          }
        });
  }

}
