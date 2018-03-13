package com.ysjr.mmjf.base;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.ysjr.mmjf.R;

/**
 * Created by Administrator on 2017-11-29.
 */

public abstract class TopBarBaseActivity extends BaseActivity {
  Toolbar toolbar;
  FrameLayout viewContent;
  TextView tvTitle;
  LinearLayout rootView;
  private int mMenuResId;
  private String mMenuStr;
  private OnClickListener mOnClickListenerTopLeft;
  private OnClickListener mOnClickListenerTopRight;
  public interface OnClickListener {
    void onClick();
  }

  @Override public int getLayoutId() {
    return R.layout.activity_base_top_bar;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    toolbar = findViewById(R.id.toolbar);
    viewContent = findViewById(R.id.viewContent);
    tvTitle = findViewById(R.id.tvTitle);
    rootView = findViewById(R.id.rootView);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    LayoutInflater.from(mContext).inflate(getContentView(), viewContent);
    ButterKnife.bind(this, rootView);
    init(savedInstanceState);
  }

  protected abstract int getContentView();

  protected abstract void init(Bundle savedInstanceState);


  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      if (mOnClickListenerTopLeft != null) {
        mOnClickListenerTopLeft.onClick();
      } else {
        finish();
      }
    } else if (item.getItemId() == R.id.menu_1) {
      mOnClickListenerTopRight.onClick();
    }
    return true;
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (mMenuResId != 0 || !TextUtils.isEmpty(mMenuStr)) {
      getMenuInflater().inflate(R.menu.menu_activity_base_top_bar,menu);
    }
    return true;
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    if (mMenuResId != 0) {
      menu.findItem(R.id.menu_1).setIcon(mMenuResId);
    }
    if (!TextUtils.isEmpty(mMenuStr)) {
      menu.findItem(R.id.menu_1).setTitle(mMenuStr);
    }
    return super.onPrepareOptionsMenu(menu);
  }
  protected void setTitle(String title) {
    if (!TextUtils.isEmpty(title)) {
      tvTitle.setText(title);
    }
  }

  protected void setTopLeftButton(int iconResId, OnClickListener onClickListener) {
    this.mOnClickListenerTopLeft = onClickListener;
    toolbar.setNavigationIcon(iconResId);
  }
  protected void setTopLeftButton(int iconResId) {
    toolbar.setNavigationIcon(iconResId);
  }

  protected void setTopRightButton(String menuStr, OnClickListener onClickListener) {
    this.mOnClickListenerTopRight = onClickListener;
    this.mMenuStr = menuStr;
  }

  protected void setTopRightButton(String menuStr, int menuResId, OnClickListener onClickListener) {
    this.mMenuStr = menuStr;
    this.mMenuResId = menuResId;
    this.mOnClickListenerTopRight = onClickListener;
  }

  protected void setTopBarBackground(Drawable colorRes) {
    toolbar.setBackground(colorRes);
  }
  protected void setTopBarBackground(int colorRes) {
    toolbar.setBackgroundColor(colorRes);
  }
}
