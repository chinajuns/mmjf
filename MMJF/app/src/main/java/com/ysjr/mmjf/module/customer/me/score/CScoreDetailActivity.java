package com.ysjr.mmjf.module.customer.me.score;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.Score;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.CScoreAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.GsonConvert;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CScoreDetailActivity extends TopBarBaseActivity {
  public static final int REFRESHING = 0;
  public static final int LOAD_MORE = 1;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  private List<Score.ListBean> mScoreList = new ArrayList<>();
  private CScoreAdapter mScoreAdapter;
  private int mPageCurrent = 1;
  @Override protected int getContentView() {
    return R.layout.c_activity_score_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.score_detail));
    initRv();
    initRefreshLayout();
    initData();
  }

  private void initRefreshLayout() {
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        httpGetScoreData(REFRESHING);
      }
    });
    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        httpGetScoreData(LOAD_MORE);
      }
    });
  }

  private void initData() {
    httpGetScoreData(REFRESHING);
  }

  private void httpGetScoreData(final int refreshType) {
    if (refreshType == REFRESHING) {
      mPageCurrent = 1;
    } else {
      mPageCurrent++;
    }
    OkGo.<JSONObject>get(Api.MOBILE_CLIENT_MEMBER_POINTLIST)
        .tag(this)
        .params("page",mPageCurrent)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            JSONObject body = response.body();
            try {
              List<Score.ListBean> beanList = new Gson().fromJson(body.getJSONObject("data").getString("data"), new TypeToken<List<Score.ListBean>>() {
              }.getType());
              switch (refreshType) {
                case REFRESHING:
                  refreshLayout.finishRefresh();
                  mScoreList.clear();
                  break;
                case LOAD_MORE:
                  refreshLayout.finishLoadmore();
                  break;
              }
              mScoreList.addAll(beanList);
              mScoreAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }

          @Override public void onError(Response<JSONObject> response) {
            super.onError(response);
            String errorStatus = null;
            if (response.getException() != null) {
              errorStatus = response.getException().getMessage();
            }
            if (HttpError.EMPTY.equals(errorStatus)) {
              switch (refreshType) {
                case REFRESHING:
                  mScoreList.clear();
                  mScoreAdapter.notifyDataSetChanged();
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
    mScoreAdapter = new CScoreAdapter(R.layout.c_score_item, mScoreList);
    recyclerView.setAdapter(mScoreAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
  }
}
