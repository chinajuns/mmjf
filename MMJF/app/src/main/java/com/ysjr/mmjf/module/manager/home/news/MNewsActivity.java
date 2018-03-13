package com.ysjr.mmjf.module.manager.home.news;

import android.os.Bundle;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.module.customer.news.NewsFragment;

/**
 * Created by Administrator on 2017-12-11.
 */

public class MNewsActivity extends TopBarBaseActivity {
  NewsFragment newsFragment;
  @Override protected int getContentView() {
    return R.layout.m_activity_news;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.news));
    newsFragment = NewsFragment.newInstance(NewsFragment.M);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.frameContent, newsFragment)
        .commit();
  }
}
