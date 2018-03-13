package com.ysjr.mmjf.module.customer.news;

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
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.NewsBean;
import com.ysjr.mmjf.entity.NewsMoreWrapBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.NewsMoreAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-4.
 */

public class NewsMoreActivity extends TopBarBaseActivity {
  public static final String KEY_TITLE = "key_title";
  public static final String KEY_ID = "key_id";
  private static final int REFRESHING = 1;
  private static final int LOAD_MORE = 2;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  private List<NewsBean.ListParent.ListChild> mNewsList = new ArrayList<>();
  private NewsMoreAdapter mAdapter;
  private int id;
  private int mPageCurrent = 1;
  @Override protected int getContentView() {
    return R.layout.c_activity_news_more;
  }

  @Override protected void init(Bundle savedInstanceState) {
    id = getIntent().getIntExtra(KEY_ID, 0);
    initTitle();
    initRefresh();
    initRv();
    initData();
  }

  private void initTitle() {
    String title = getIntent().getStringExtra(KEY_TITLE);
    if (title == null) {
      title = "资讯详情";
    }
    setTitle(title);
  }

  private void initRefresh() {
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

  private void initRv() {
    mAdapter = new NewsMoreAdapter(R.layout.c_news_more_item, mNewsList);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, NewsDetailActivity.class);
        intent.putExtra(NewsDetailActivity.KEY_ARTICLE_BEAN, mNewsList.get(position));
        ActivityUtils.startActivity(intent);
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
    OkGo.<HttpResult<NewsMoreWrapBean>>get(Api.MOBILE_CLIENT_ARTICLE_LIST+id)
        .tag(this)
        .params("page",mPageCurrent)
        .execute(new JsonCallback<HttpResult<NewsMoreWrapBean>>() {
          @Override public void onSuccess(Response<HttpResult<NewsMoreWrapBean>> response) {
            List<NewsBean.ListParent.ListChild> beanList = response.body().data.data;
            loadingLayout.setStatus(LoadingLayout.Success);
            switch (refreshType) {
              case REFRESHING:
                mNewsList.clear();
                refreshLayout.finishRefresh();
                break;
              case LOAD_MORE:
                refreshLayout.finishLoadmore();
                break;
            }
            mNewsList.addAll(beanList);
            mAdapter.notifyDataSetChanged();
          }

          @Override public void onError(Response<HttpResult<NewsMoreWrapBean>> response) {
            super.onError(response);
            String errorStatus = null;
            if (response.getException() != null) {
              errorStatus = response.getException().getMessage();
            }
            if (HttpError.EMPTY.equals(errorStatus)) {
            switch (refreshType) {
              case REFRESHING:
                loadingLayout.setStatus(LoadingLayout.Empty);
                refreshLayout.finishRefresh();
                break;
              case LOAD_MORE:
                refreshLayout.finishLoadmore();
                refreshLayout.setLoadmoreFinished(true);
                break;
            }
          }}
        });
  }


}
