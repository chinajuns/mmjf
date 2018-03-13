package com.ysjr.mmjf.module.customer.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.NewsBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-11-28.
 */

public class NewsFragment extends BaseFragment {
  private static final String KEY_TYPE = "type";
  public static final int M = 2;
  public static final int C= 1;
  @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
  @BindView(R.id.viewBanner) Banner viewBanner;
  @BindView(R.id.layoutFinance) LinearLayout layoutFinance;
  @BindView(R.id.ivMiddle1) ImageView ivMiddle1;
  @BindView(R.id.layoutLoanRate) LinearLayout layoutLoanRate;
  @BindView(R.id.layoutInfo) LinearLayout layoutInfo;
  @BindView(R.id.ivMiddle2) ImageView ivMiddle2;
  @BindView(R.id.toolbar) Toolbar toolbar;
  public static NewsFragment newInstance(int type) {
    NewsFragment fg = new NewsFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TYPE,type);
    fg.setArguments(bundle);
    return fg;
  }
  @Override public int getLayoutId() {
    return R.layout.c_fragment_news;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    int type = getArguments().getInt(KEY_TYPE);
    if (type == M) {
      toolbar.setVisibility(View.GONE);
    }
    refreshLayout.setEnableLoadmore(false);
  }

  @Override public void onResume() {
    super.onResume();
    initData();
  }

  private void initData() {
    httpGetDat();
  }

  private void httpGetDat() {
    OkGo.<HttpResult<NewsBean>>get(Api.MOBILE_CLIENT_ARTICLE)
        .tag(mContext)
        .execute(new JsonCallback<HttpResult<NewsBean>>() {
          @Override public void onSuccess(Response<HttpResult<NewsBean>> response) {
            if (layoutFinance != null)layoutFinance.removeAllViews();
            if (layoutLoanRate != null)layoutLoanRate.removeAllViews();
            if (layoutInfo != null) layoutInfo.removeAllViews();
            //轮播图
            List<NewsBean.AdverBean.ImageBean> bannerList = response.body().data.adver.top;
            initBanner(bannerList);
            //中间的两张广告图
            List<NewsBean.AdverBean.ImageBean> middleList = response.body().data.adver.middle;
            displayMiddleImage(middleList);

            //内容
            List<NewsBean.ListParent> listParent = response.body().data.list;
            for (NewsBean.ListParent parent : listParent) {
              if (parent.id == 1) {//金融政策
                initLayout(parent.list,layoutFinance);
              } else if (parent.id == 2) {//贷款利率
                initLayout(parent.list,layoutLoanRate);
              } else if (parent.id == 3) {//资料讲解
                initLayout(parent.list,layoutInfo);
              }
            }
          }
        });
  }

  private void initLayout(List<NewsBean.ListParent.ListChild> list,LinearLayout layoutFinance) {
    for (int i = 0; i < list.size(); i++) {
      final NewsBean.ListParent.ListChild child = list.get(i);
      LinearLayout.LayoutParams parentParams =
          new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(120));
      RelativeLayout parentRl = new RelativeLayout(mContext);
      parentRl.setPadding(SizeUtils.dp2px(14),0,SizeUtils.dp2px(14),0);
      ImageView img = new ImageView(mContext);
      RelativeLayout.LayoutParams imgParams =
          new RelativeLayout.LayoutParams(SizeUtils.dp2px(60),
              SizeUtils.dp2px(60));
      imgParams.addRule(RelativeLayout.CENTER_VERTICAL);
      img.setId(R.id.img);
      parentRl.addView(img,0,imgParams);
      RelativeLayout.LayoutParams titleParams =
          new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      titleParams.addRule(RelativeLayout.RIGHT_OF,R.id.img);
      titleParams.addRule(RelativeLayout.ALIGN_TOP,R.id.img);
      titleParams.setMargins(SizeUtils.dp2px(12),0,0,0);
      TextView tvTitle = new TextView(mContext);
      tvTitle.setMaxLines(1);
      tvTitle.setTextColor(getResources().getColor(R.color.title_black_color));
      tvTitle.setTextSize(16);
      parentRl.addView(tvTitle,1,titleParams);

      RelativeLayout.LayoutParams numParams =
          new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      numParams.addRule(RelativeLayout.RIGHT_OF,R.id.img);
      numParams.addRule(RelativeLayout.ALIGN_BOTTOM,R.id.img);
      numParams.setMargins(SizeUtils.dp2px(12),0,0,0);
      TextView tvNumAndDate = new TextView(mContext);
      tvNumAndDate.setMaxLines(1);
      tvNumAndDate.setTextColor(getResources().getColor(R.color.text_3_b3_color));
      tvNumAndDate.setTextSize(11);
      parentRl.addView(tvNumAndDate,2,numParams);

      Glide.with(mContext).load(Api.IMAGE_ROOT_URL + child.picture).placeholder(R.drawable.suotu1).into(img);
      tvTitle.setText(child.title);
      String dateAndNum = SimpleDateUtils.getNoHours(child.create_time*1000) + "/" + child.views + "阅读量";
      tvNumAndDate.setText(dateAndNum);
      layoutFinance.addView(parentRl,parentParams);
      if (i != list.size() - 1) {
        View view = new View(mContext);
        LinearLayout.LayoutParams lineParams =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(1));
        lineParams.setMargins(SizeUtils.dp2px(15),0,SizeUtils.dp2px(15),0);
        view.setBackgroundColor(getResources().getColor(R.color.line_color));
        layoutFinance.addView(view,lineParams);
      }

      parentRl.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Intent intent = new Intent(mContext, NewsDetailActivity.class);
          intent.putExtra(NewsDetailActivity.KEY_ARTICLE_BEAN, child);
          ActivityUtils.startActivity(intent);
        }
      });
    }
  }

  private void displayMiddleImage(List<NewsBean.AdverBean.ImageBean> middleList) {
    for (int i = 0; i < middleList.size(); i++) {
      String url = Api.IMAGE_ROOT_URL + middleList.get(i).image;
      if (i == 0) {
        Glide.with(mContext).load(url).placeholder(R.drawable.banner2).error(R.drawable.banner2).into(ivMiddle1);
      } else {
        Glide.with(mContext).load(url).placeholder(R.drawable.banner3).error(R.drawable.banner3).into(ivMiddle2);
      }
    }
  }
  private void initBanner(List<NewsBean.AdverBean.ImageBean> bannerList) {
    List<String> mBannerList = new ArrayList<>();
    for (int i = 0; i < bannerList.size(); i++) {
      String url = bannerList.get(i).image;
      if (url != null && !url.contains("http")) {
        url = Api.IMAGE_ROOT_URL + url;
      }
      mBannerList.add(url);
    }
    viewBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
    viewBanner.setImageLoader(new CNewsBannerImageLoader());
    viewBanner.isAutoPlay(true);
    viewBanner.setDelayTime(1500);
    viewBanner.setIndicatorGravity(BannerConfig.CENTER);
    viewBanner.setImages(mBannerList);
    viewBanner.start();
  }

  @OnClick(R.id.ivFinance) public void onLayoutIvFinanceClicked() {
    Intent intent = new Intent(mContext, NewsMoreActivity.class);
    intent.putExtra(NewsMoreActivity.KEY_ID, 1);
    intent.putExtra(NewsMoreActivity.KEY_TITLE, getString(R.string.finance));
    ActivityUtils.startActivity(intent);
  }
  @OnClick(R.id.layoutFinanceHeader) public void onLayoutFinanceHeaderClicked() {
    Intent intent = new Intent(mContext, NewsMoreActivity.class);
    intent.putExtra(NewsMoreActivity.KEY_ID, 1);
    intent.putExtra(NewsMoreActivity.KEY_TITLE, getString(R.string.finance));
    ActivityUtils.startActivity(intent);
  }
  @OnClick(R.id.ivLoanRate) public void onivLoanRateClicked() {
    Intent intent = new Intent(mContext, NewsMoreActivity.class);
    intent.putExtra(NewsMoreActivity.KEY_ID, 2);
    intent.putExtra(NewsMoreActivity.KEY_TITLE, getString(R.string.loan_rate));
    ActivityUtils.startActivity(intent);
  }
  @OnClick(R.id.layoutRateHeader) public void onLayoutRateHeaderClicked() {
    Intent intent = new Intent(mContext, NewsMoreActivity.class);
    intent.putExtra(NewsMoreActivity.KEY_ID, 2);
    intent.putExtra(NewsMoreActivity.KEY_TITLE, getString(R.string.loan_rate));
    ActivityUtils.startActivity(intent);
  }
  @OnClick(R.id.ivInfoTeach) public void onivInfoTeachClicked() {
    Intent intent = new Intent(mContext, NewsMoreActivity.class);
    intent.putExtra(NewsMoreActivity.KEY_ID, 3);
    intent.putExtra(NewsMoreActivity.KEY_TITLE, getString(R.string.teach_info));
    ActivityUtils.startActivity(intent);
  }
  @OnClick(R.id.layoutInfoHeader) public void onLayoutInfoHeaderClicked() {
    Intent intent = new Intent(mContext, NewsMoreActivity.class);
    intent.putExtra(NewsMoreActivity.KEY_ID, 3);
    intent.putExtra(NewsMoreActivity.KEY_TITLE, getString(R.string.teach_info));
    ActivityUtils.startActivity(intent);
  }
}
