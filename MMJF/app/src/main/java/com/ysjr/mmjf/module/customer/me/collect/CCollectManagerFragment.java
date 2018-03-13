package com.ysjr.mmjf.module.customer.me.collect;

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
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.entity.ManagerWrapBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.CollectManagerAdapter;
import com.ysjr.mmjf.module.customer.store.CStoreActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CCollectManagerFragment extends BaseFragment {
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  private List<Manager> mManagerList = new ArrayList<>();
  private CollectManagerAdapter mAdapter;
  private static final int REFRESHING = 0;
  private static final int LOAD_MORE = 1;
  private int mPageCurrent = 1;

  public static CCollectManagerFragment newInstance() {
    CCollectManagerFragment fg = new CCollectManagerFragment();
    return fg;
  }

  @Override public int getLayoutId() {
    return R.layout.c_fragment_collect;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    initRv();
    initRefresh();
    initData();
  }

  private void initRefresh() {
    loadingLayout.setEmptyText(getString(R.string.no_collect));
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.setLoadmoreFinished(false);
        httpGetData(REFRESHING);
      }
    });
    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        httpGetData(LOAD_MORE);
      }
    });
  }

  private void initData() {
    httpGetData(REFRESHING);
  }

  private void httpGetData(final int refreshType) {
    if (refreshType == REFRESHING) {
      mPageCurrent = 1;
    } else {
      mPageCurrent++;
    }
    OkGo.<HttpResult<ManagerWrapBean>>post(Api.MOBILE_CLIENT_MEMBER_SET_FAVORITE_LIST).tag(mContext)
        .params("type", 1)
        .params("page",mPageCurrent)
        .execute(new JsonCallback<HttpResult<ManagerWrapBean>>() {
          @Override public void onSuccess(Response<HttpResult<ManagerWrapBean>> response) {
            loadingLayout.setStatus(LoadingLayout.Success);
            switch (refreshType) {
              case REFRESHING:
                mManagerList.clear();
                refreshLayout.finishRefresh();
                break;
              case LOAD_MORE:
                refreshLayout.finishLoadmore();
                break;
            }
            mManagerList.addAll(response.body().data.data);
            mAdapter.notifyDataSetChanged();
          }

          @Override public void onError(Response<HttpResult<ManagerWrapBean>> response) {
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
    mAdapter = new CollectManagerAdapter(R.layout.c_collect_manager_item, mManagerList);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
          case R.id.right:
            httpCancelCollect(position);
            break;
          case R.id.content:
            Intent intent = new Intent(mContext, CStoreActivity.class);
            intent.putExtra(CStoreActivity.KEY_MANAGER_ID, mManagerList.get(position).object_id);
            intent.putExtra(CStoreActivity.KEY_HEADER_IMAGE, mManagerList.get(position).header_img);
            intent.putExtra(CStoreActivity.KEY_MANAGER_NAME, mManagerList.get(position).name);
            intent.putExtra(CStoreActivity.KEY_MANAGER_TAG, mManagerList.get(position).tag);
            ActivityUtils.startActivity(intent);
            break;
        }
      }
    });
  }

  private void httpCancelCollect(final int position) {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_MEMBER_SET_FAVORITE)
        .tag(mContext)
        .params("action","cancel")
        .params("type",1)
        .params("id",mManagerList.get(position).object_id)
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            mManagerList.remove(position);
            mAdapter.notifyItemRemoved(position);
          }
        });

  }
}
