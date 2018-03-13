package com.ysjr.mmjf.module.manager.delegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.ProBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MDelegateSearchActivity extends BaseActivity {
  //public static final String KEY_SEARCH_TYPE_NAME = "type_name";
  //@BindView(R.id.etLoanType) MaterialEditText etLoanType;
  //@BindView(R.id.recyclerView) RecyclerView recyclerView;
  //@BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  //@BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  //private List<ProBean> mSearchList = new ArrayList<>();
  //private static final int REFRESHING = 1;
  //private static final int LOAD_MORE = 2;
  //private SearchAdapter mAdapter;
  //private long create_time;
  //private static final int SEARCH_MESSAGE = 3;
  //private final int INTERVAL = 500;
  //
  //private Handler mSearchHandler = new Handler(){
  //  @Override public void handleMessage(Message msg) {
  //    httpGetData(REFRESHING);
  //  }
  //};
  @Override public int getLayoutId() {
    return R.layout.m_activity_delegate_search;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    //initRefresh();
    //initRv();
    //setSearchListener();
  }
  //
  //private void setSearchListener() {
  //  etLoanType.addTextChangedListener(new TextWatcher() {
  //    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  //    }
  //
  //    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
  //
  //    }
  //
  //    @Override public void afterTextChanged(Editable s) {
  //      if (mSearchHandler.hasMessages(SEARCH_MESSAGE)) {
  //        mSearchHandler.removeMessages(SEARCH_MESSAGE);
  //      }
  //      if (!TextUtils.isEmpty(etLoanType.getText().toString().trim())) {
  //        mSearchHandler.sendEmptyMessageDelayed(SEARCH_MESSAGE, INTERVAL);
  //      }
  //    }
  //  });
  //}
  //
  //private void initRefresh() {
  //  loadingLayout.setStatus(LoadingLayout.Success);
  //  refreshLayout.setOnRefreshListener(new OnRefreshListener() {
  //    @Override public void onRefresh(RefreshLayout refreshlayout) {
  //      refreshlayout.setLoadmoreFinished(false);
  //      httpGetData(REFRESHING);
  //    }
  //  });
  //  refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
  //    @Override public void onLoadmore(RefreshLayout refreshlayout) {
  //      create_time = mSearchList.get(mSearchList.size() - 1).create_time;
  //      httpGetData(LOAD_MORE);
  //    }
  //  });
  //}
  //
  //
  //private void httpGetData(final int refreshType) {
  //  PostRequest<HttpResult<ProBean>> request = null;
  //  if (refreshType == REFRESHING) {
  //    request = OkGo.<HttpResult<ProBean>>post(Api.MOBILE_BUSINESS_PRODUCT_INDEX).tag(mContext)
  //    .params("keyword",etLoanType.getText().toString());
  //  } else if (refreshType == LOAD_MORE) {
  //    request = OkGo.<HttpResult<ProBean>>post(Api.MOBILE_BUSINESS_PRODUCT_INDEX).tag(mContext)
  //        .params("keyword",etLoanType.getText().toString())
  //        .params("create_time", create_time);
  //  }
  //  request.execute(new JsonCallback<HttpResult<ProBean>>() {
  //    @Override public void onSuccess(Response<HttpResult<ProBean>> response) {
  //      List<ProBean> beanList = response.body().data.product;
  //      switch (refreshType) {
  //        case REFRESHING:
  //          mSearchList.clear();
  //          refreshLayout.finishRefresh();
  //          break;
  //        case LOAD_MORE:
  //          refreshLayout.finishLoadmore();
  //          break;
  //      }
  //      mSearchList.addAll(beanList);
  //      mAdapter.notifyDataSetChanged();
  //    }
  //
  //    @Override public void onError(Response<HttpResult<ProBean>> response) {
  //      super.onError(response);
  //      if (response.getException().getMessage().equals(HttpError.EMPTY)) {
  //        switch (refreshType) {
  //          case REFRESHING:
  //            refreshLayout.finishRefresh();
  //            loadingLayout.setStatus(LoadingLayout.Empty);
  //            break;
  //          case LOAD_MORE:
  //            refreshLayout.finishLoadmore();
  //            refreshLayout.setLoadmoreFinished(true);
  //            break;
  //        }
  //      }
  //    }
  //  });
  //}
  //
  //
  //private void initRv() {
  //  mAdapter = new SearchAdapter(R.layout.m_delegate_search_item, mSearchList);
  //  recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
  //  recyclerView.setAdapter(mAdapter);
  //  mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
  //    @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
  //      ProBean.ProductBean bean = mSearchList.get(position);
  //      Intent intent = new Intent();
  //      intent.putExtra(KEY_SEARCH_TYPE_NAME, bean.cate_name);
  //      setResult(RESULT_OK,intent);
  //      finish();
  //    }
  //  });
  //}
  //
  //@OnClick(R.id.tvCancel) public void onViewClicked() {
  //  finish();
  //}
  //
  //class SearchAdapter extends BaseQuickAdapter<ProBean.ProductBean, BaseViewHolder> {
  //  public SearchAdapter(int layoutResId, @Nullable List<ProBean.ProductBean> data) {
  //    super(layoutResId, data);
  //  }
  //
  //  @Override protected void convert(BaseViewHolder helper, ProBean.ProductBean item) {
  //    helper.setText(R.id.tvLoanType, item.cate_name);
  //  }
  //}

}
