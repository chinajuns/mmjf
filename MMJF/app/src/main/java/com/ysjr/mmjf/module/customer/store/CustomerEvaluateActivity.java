package com.ysjr.mmjf.module.customer.store;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.BindView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.CManagerTag;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-1.
 */

public class CustomerEvaluateActivity extends TopBarBaseActivity {
  public static final String KEY_MANAGER_ID = "manager_id";
  @BindView(R.id.gridViewLabel) GridView gridViewLabel;
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.viewPager) ViewPager viewPager;
  @BindView(R.id.tvScore) TextView tvScore;
  @BindView(R.id.ratingBar) ScaleRatingBar ratingBar;
  private List<String> mLabelList = new ArrayList<>();
  private ArrayList<Fragment> mEvaluateFragments = new ArrayList<>();
  private EvaluateFragment allFragment;
  private EvaluateFragment goodFragment;
  private EvaluateFragment mediumFragment;
  private EvaluateFragment negativeFragment;
  private List<String> mEvaluateTagList = new ArrayList<>();
  private EvaluatePageAdapter mPageAdapter;
  private int mManagerId;
  @Override protected int getContentView() {
    return R.layout.c_activity_customer_evaluate;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mManagerId = getIntent().getIntExtra(KEY_MANAGER_ID,0);
    setTitle(getString(R.string.all_evaluate));
    setTopLeftButton(R.drawable.btn_black_back);
    initGridView();
    initVp();
    initData();
  }

  private void initData() {
    httpGetData();
  }

  private void httpGetData() {
    OkGo.<HttpResult<CManagerTag>>get(Api.MOBILE_CLIENT_AVERAGE+"/"+mManagerId)
        .tag(this)
        .execute(new JsonCallback<HttpResult<CManagerTag>>() {
          @Override public void onSuccess(Response<HttpResult<CManagerTag>> response) {
            List<CManagerTag.TagBean> tagList = response.body().data.tag;
            for (CManagerTag.TagBean bean : tagList) {
              mLabelList.add(bean.tag + "(" + bean.times + ")");
            }
            tvScore.setText(response.body().data.average+"");
            ratingBar.setRating(response.body().data.average);
            mTagAdapter.notifyDataSetChanged();
          }
        });
  }

  private void initVp() {
    allFragment = EvaluateFragment.newInstance(0,mManagerId);
    mediumFragment = EvaluateFragment.newInstance(4,mManagerId);
    negativeFragment = EvaluateFragment.newInstance(5,mManagerId);
    goodFragment= EvaluateFragment.newInstance(3,mManagerId);
    mEvaluateFragments.add(allFragment);
    mEvaluateFragments.add(goodFragment);
    mEvaluateFragments.add(mediumFragment);
    mEvaluateFragments.add(negativeFragment);
    mEvaluateTagList.add(getString(R.string.all));
    mEvaluateTagList.add(getString(R.string.good_evaluate));
    mEvaluateTagList.add(getString(R.string.medium_evaluate));
    mEvaluateTagList.add(getString(R.string.negative_evaluate));
    mPageAdapter =
        new EvaluatePageAdapter(getSupportFragmentManager(), mEvaluateFragments, mEvaluateTagList);
    viewPager.setAdapter(mPageAdapter);
    tabLayout.setupWithViewPager(viewPager);
    CommonUtils.reflex(tabLayout);
  }

  ArrayAdapter<String> mTagAdapter;
  private void initGridView() {
    mTagAdapter = new ArrayAdapter<String>(mContext, R.layout.c_manager_label_item, mLabelList);
    gridViewLabel.setAdapter(mTagAdapter);
  }

}
