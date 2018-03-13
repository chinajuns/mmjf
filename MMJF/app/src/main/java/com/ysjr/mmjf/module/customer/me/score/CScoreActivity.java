package com.ysjr.mmjf.module.customer.me.score;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.Score;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.CScoreAdapter;
import com.ysjr.mmjf.utils.Api;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CScoreActivity extends TopBarBaseActivity {
  @BindView(R.id.tvTodayScore) TextView tvTodayScore;
  @BindView(R.id.tvMonthScore) TextView tvMonthScore;
  @BindView(R.id.tvTotal) TextView tvTotal;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  private List<Score.ListBean> mScoreList = new ArrayList<>();
  private CScoreAdapter mScoreAdapter;
  @Override protected int getContentView() {
    return R.layout.c_activity_score;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTopRightButton("", R.drawable.ic_score_rule, new OnClickListener() {
      @Override public void onClick() {
        ActivityUtils.startActivity(CScoreRuleActivity.class);
      }
    });
    initRv();
    initData();
  }

  private void initData() {
    OkGo.<HttpResult<Score>>get(Api.MOBILE_CLIENT_MEMBER_POINT)
        .tag(this)
        .execute(new JsonCallback<HttpResult<Score>>() {
          @Override public void onSuccess(Response<HttpResult<Score>> response) {
            Score score = response.body().data;
            tvMonthScore.setText("本月获得："+score.month+"积分");
            tvTodayScore.setText("今日获得："+score.day+"积分");
            tvTotal.setText(""+score.total);
            mScoreList.clear();
            mScoreList.addAll(score.list);
            mScoreAdapter.notifyDataSetChanged();
          }
        });
  }

  private void initRv() {
    mScoreAdapter = new CScoreAdapter(R.layout.c_score_item, mScoreList);
    recyclerView.setAdapter(mScoreAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
  }

  @OnClick(R.id.tvScoreShop) public void onTvScoreShopClicked() {

  }

  @OnClick(R.id.tvScoreDetail) public void onTvScoreDetailClicked() {
    ActivityUtils.startActivity(CScoreDetailActivity.class);
  }
}
