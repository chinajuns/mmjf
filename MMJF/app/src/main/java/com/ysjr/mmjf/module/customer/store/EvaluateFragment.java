package com.ysjr.mmjf.module.customer.store;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.Evaluate;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-1.
 */

public class EvaluateFragment extends BaseFragment {
  private static final String KEY_TYPE = "type";
  private static final String KEY_MANAGER_ID = "id";
  public static final int REFRESING = 1;
  public static final int LOAD_MORE = 2;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  private ArrayList<Evaluate.DataItem> evaluateList = new ArrayList<>();
  private EvaluateAdapter mAdapter;
  private int type = -1;
  private int mId;
  private int mPageCurrent = 1;
  public static EvaluateFragment newInstance(int type,int id) {
    EvaluateFragment fg = new EvaluateFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TYPE,type);
    bundle.putInt(KEY_MANAGER_ID,id);
    fg.setArguments(bundle);
    return fg;
  }
  @Override public int getLayoutId() {
    return R.layout.c_fragment_evaluate;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    type = getArguments().getInt(KEY_TYPE,-1);
    mId = getArguments().getInt(KEY_MANAGER_ID, -1);
    initRv();
    initRefresh();
    initData();
  }

  private void initRefresh() {
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        httpGetData(REFRESING);
        refreshlayout.setLoadmoreFinished(false);
      }
    });
    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        httpGetData(LOAD_MORE);
      }
    });
  }

  private void initData() {
    loadingLayout.setEmptyText(getString(R.string.no_data));
    httpGetData(REFRESING);
  }

  private void httpGetData(final int refreshType) {
    if (refreshType == REFRESING) {
      mPageCurrent = 1;
    } else {
      mPageCurrent++;
    }
    OkGo.<HttpResult<Evaluate>>post(Api.MOBILE_CLIENT_EVALUATE)
        .tag(mContext)
        .params("id",mId)
        .params("type",type)
        .params("page",mPageCurrent)
        .execute(new JsonCallback<HttpResult<Evaluate>>() {
          @Override public void onSuccess(Response<HttpResult<Evaluate>> response) {
            loadingLayout.setStatus(LoadingLayout.Success);
            switch (refreshType) {
              case REFRESING:
                evaluateList.clear();
                refreshLayout.finishRefresh();
                break;
              case LOAD_MORE:
                refreshLayout.finishLoadmore();
                break;
            }
            evaluateList.addAll(response.body().data.data);
            mAdapter.notifyDataSetChanged();
          }

          @Override public void onError(Response<HttpResult<Evaluate>> response) {
            super.onError(response);
            String errorStatus = null;
            if (response.getException() != null) {
              errorStatus = response.getException().getMessage();
            }
            if (HttpError.EMPTY.equals(errorStatus)) {
              switch (refreshType) {
                case REFRESING:
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
    mAdapter = new EvaluateAdapter(R.layout.c_evaluate_item, evaluateList);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
  }
}
