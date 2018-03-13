package com.ysjr.mmjf.module.manager.delegate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import com.ysjr.mmjf.entity.ProBean;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.manager.adapter.MDelegateAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MDelegateActivity extends TopBarBaseActivity {
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  private List<ProBean> mProList = new ArrayList<>();
  private MDelegateAdapter mAdapter;
  public static final int REFRESHING = 1;
  public static final int LOAD_MORE = 2;
  private String cate_id = "0";
  private long create_time;
  private String TAG = MDelegateActivity.class.getSimpleName();
  public List<CateProduct> cateList = new ArrayList<>();

  @Override protected int getContentView() {
    return R.layout.m_activity_delegate;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.delegate_pro));
    initRefresh();
    initRv();
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override public void onTabSelected(TabLayout.Tab tab) {
        cate_id = cateList.get(tab.getPosition()).id + "";
        httpGetData(REFRESHING);
      }

      @Override public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override public void onTabReselected(TabLayout.Tab tab) {

      }
    });
    httpGetCateProduct();
  }

  @Override protected void onResume() {
    super.onResume();
    initData();
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

  private void initData() {
    httpGetData(REFRESHING);
  }

  private void httpGetCateProduct() {
    OkGo.<HttpResult<List<CateProduct>>>get(Api.MOBILE_BUSINESS_PRODUCT_OTHER_TYPE).execute(
        new JsonCallback<HttpResult<List<CateProduct>>>() {
          @Override public void onSuccess(Response<HttpResult<List<CateProduct>>> response) {
            cateList = response.body().data;
            CateProduct cateProduct = new CateProduct();
            cateProduct.cate_name = "全部";
            cateProduct.id = 0;
            cateList.add(0, cateProduct);
            for (CateProduct cate : cateList) {
              tabLayout.addTab(tabLayout.newTab().setText(cate.cate_name));
            }
          }
        });
  }

  private void httpGetData(final int refreshType) {
    OkGo.<HttpResult<List<ProBean>>>post(Api.MOBILE_BUSINESS_PRODUCT_MY_PRODUCT).tag(mContext)
        .params("type", "all")
        .params("cate_id", cate_id)
        .params("create_time", create_time)
        .execute(new JsonCallback<HttpResult<List<ProBean>>>() {
          @Override public void onSuccess(Response<HttpResult<List<ProBean>>> response) {
            List<ProBean> beanList = response.body().data;
            loadingLayout.setStatus(LoadingLayout.Success);
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
                  loadingLayout.setStatus(LoadingLayout.Empty);
                  refreshLayout.finishRefresh();
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
    mAdapter = new MDelegateAdapter(R.layout.m_delegate_item, mProList, true);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, MDelegateDetailActivity.class);
        intent.putExtra(MDelegateDetailActivity.KEY_IS_AGENTED, 0);
        intent.putExtra(MDelegateDetailActivity.KEY_ID, mProList.get(position).id);
        ActivityUtils.startActivity(intent);
      }
    });
    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override
      public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        ProBean bean = mProList.get(position);
        OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_PRODUCT_SET_AGENT).tag(mContext)
            .params("id", bean.id)
            .params("action", "add")
            .execute(new DialogCallback<HttpResult<Void>>(mContext) {
              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                mProList.remove(position);
                mAdapter.notifyItemRemoved(position);
              }
            });
      }
    });
  }

  class CateProduct {
    public String cate_name;
    public int id;
  }
}

