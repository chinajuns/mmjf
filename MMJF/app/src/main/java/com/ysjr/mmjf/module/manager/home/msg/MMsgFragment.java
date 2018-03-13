package com.ysjr.mmjf.module.manager.home.msg;

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
import com.ysjr.mmjf.entity.CUpdateMsgState;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.MsgWrapBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.CMsgAdapter;
import com.ysjr.mmjf.module.customer.msg.CMsgChildFragment;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MMsgFragment extends BaseFragment {
  public static final int REFRESHING = 0;
  public static final int LOAD_MORE = 1;
  public static final String KEY_TYPE = "key_type";
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  @BindView(R.id.loadingLayout) LoadingLayout loadingLayout;
  private List<MsgWrapBean.DataBean> mMsgList = new ArrayList<>();
  private CMsgAdapter mAdapter;
  private int mType;
  private int mPageCurrent = 1;
  private boolean isFirstIn  =true;
  public static MMsgFragment newInstance(int type) {
    MMsgFragment fg = new MMsgFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TYPE, type);
    fg.setArguments(bundle);
    return fg;
  }

  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isFirstIn && isVisibleToUser) {
      isFirstIn = false;
      modifyHomeMsgState();
    } else if (isVisibleToUser){
      for (MsgWrapBean.DataBean bean : mMsgList) {
        bean.status = 2;
      }
      if (mAdapter != null) {
        mAdapter.notifyDataSetChanged();
      }
    }
  }

  @Override public int getLayoutId() {
    return R.layout.m_fragment_msg;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    mType = getArguments().getInt(KEY_TYPE);
    initRefreshLayout();
    initRv();
    initData();
  }

  private void initData() {
    loadingLayout.setEmptyImage(R.drawable.ic_msg_empty).setEmptyText(getString(R.string.no_msg));
    httpGetMsg(REFRESHING);
  }

  private void httpGetMsg(final int refreshType) {
    if (refreshType == REFRESHING) {
      mPageCurrent = 1;
    } else {
      mPageCurrent++;
    }
    OkGo.<HttpResult<MsgWrapBean>>get(Api.MOBILE_CLIENT_MESSAGE_TYPE + "/" + mType).tag(mContext)
        .params("page", mPageCurrent)
        .execute(new JsonCallback<HttpResult<MsgWrapBean>>() {
          @Override public void onSuccess(Response<HttpResult<MsgWrapBean>> response) {
            loadingLayout.setStatus(LoadingLayout.Success);
            switch (refreshType) {
              case REFRESHING:
                mMsgList.clear();
                refreshLayout.finishRefresh();
                break;
              case LOAD_MORE:
                refreshLayout.finishLoadmore();
                break;
            }
            mMsgList.addAll(response.body().data.data);
            mAdapter.notifyDataSetChanged();
          }

          @Override public void onError(Response<HttpResult<MsgWrapBean>> response) {
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
    mAdapter = new CMsgAdapter(R.layout.c_msg_item, mMsgList);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
  }

  private void initRefreshLayout() {

    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.setLoadmoreFinished(false);
        httpGetMsg(REFRESHING);
      }
    });

    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        httpGetMsg(LOAD_MORE);
      }
    });
  }

  private void modifyHomeMsgState() {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_MESSAGE_SET_READ)
        .tag(mContext)
        .params("type",mType)
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            EventBus.getDefault().post(new CUpdateMsgState());
          }
        });
  }
}
