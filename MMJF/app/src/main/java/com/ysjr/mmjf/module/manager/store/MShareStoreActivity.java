package com.ysjr.mmjf.module.manager.store;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.entity.ProBean;
import com.ysjr.mmjf.entity.Success;
import com.ysjr.mmjf.module.dialogfragment.SuccessDialogFragment;
import com.ysjr.mmjf.module.manager.adapter.MShareStoreAdapter;
import com.ysjr.mmjf.module.popup.SharePopup;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MShareStoreActivity extends BaseActivity {
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.tvTitle) TextView tvTitle;
  private List<ProBean> mProList = new ArrayList<>();
  private MShareStoreAdapter mAdapter;
  private LinearLayoutManager layoutManager;
  SharePopup mSharePopup;
  protected boolean isInstallSina;
  protected boolean isInstallWeixin;
  protected boolean isInstallQQ;
  @Override public int getLayoutId() {
    return R.layout.m_activity_share_store;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    isInstallSina = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.SINA);
    isInstallQQ = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ);
    isInstallWeixin = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN);
    EventBus.getDefault().register(this);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      View decorView = getWindow().getDecorView();
      int option = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
      decorView.setSystemUiVisibility(option);
      BarUtils.setStatusBarColor(mContext, Color.TRANSPARENT, 0, true);
    }
    initRv();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    UMShareAPI.get(this).release();
    EventBus.getDefault().unregister(this);
  }
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
  }
  private void initRv() {
    for (int i = 0; i < 10; i++) {
      mProList.add(new ProBean());
    }
    mAdapter = new MShareStoreAdapter(R.layout.m_share_store_item, mProList);
    View head = LayoutInflater.from(mContext).inflate(R.layout.m_share_store_head, null);
    mAdapter.addHeaderView(head);
    recyclerView.setAdapter(mAdapter);
    layoutManager = new LinearLayoutManager(mContext);
    recyclerView.setLayoutManager(layoutManager);
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

      }
    });
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Log.e("Scrolled", getScrollYDistance() + "");
        if (getScrollYDistance() >= SizeUtils.dp2px(54)) {
          toolbar.setBackgroundColor(getResources().getColor(R.color.theme_color));
          toolbar.setNavigationIcon(R.drawable.btn_black_back);
          tvTitle.setTextColor(getResources().getColor(R.color.title_black_color));
        } else {
          toolbar.setBackgroundColor(Color.TRANSPARENT);
          toolbar.setNavigationIcon(R.drawable.btn_back);
          tvTitle.setTextColor(getResources().getColor(R.color.bg_white_color));
        }
      }
    });
  }

  public int getScrollYDistance() {
    int position = layoutManager.findFirstVisibleItemPosition();
    View firstVisiableChildView = layoutManager.findViewByPosition(position);
    int itemHeight = firstVisiableChildView.getHeight();
    return (position) * itemHeight - firstVisiableChildView.getTop();
  }

  @OnClick(R.id.btnEditStore) public void onBtnEditStoreClicked() {
    ActivityUtils.startActivity(MEditStoreActivity.class);
  }

  @OnClick(R.id.btnShareStore) public void onBtnShareStoreClicked() {
    DelegateDialogFragment fg = new DelegateDialogFragment();
    fg.show(getSupportFragmentManager(), "delegate_dialog");
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onDelegateClicked(Integer integer) {
    mSharePopup = new SharePopup(mContext);
    mSharePopup.showPopupWindow();
    mSharePopup.setListener(new SharePopup.OnItemClickListener() {
      @Override public void onClick(SharePopup.ItemType type) {
        switch (type) {
          case SINA:
            if (isInstallSina) {
              new ShareAction(mContext)
                  .setPlatform(SHARE_MEDIA.SINA)
                  .withText(getString(R.string.sina_weibo))
                  .setCallback(mUmShareListener)
                  .share();
              mSharePopup.dismiss();
            } else {
              ToastUtils.showShort("未安装新浪微博");
            }
            break;
          case QQ:
            if (isInstallQQ) {
              new ShareAction(mContext)
                  .setPlatform(SHARE_MEDIA.QQ)
                  .withMedia(new UMImage(mContext,R.drawable.btn_weixin_login))
                  .setCallback(mUmShareListener)
                  .share();
              mSharePopup.dismiss();
            } else {
              ToastUtils.showShort("未安装QQ");
            }
            break;
          case QQSPACE:
            if (isInstallQQ) {
              new ShareAction(mContext)
                  .setPlatform(SHARE_MEDIA.QZONE)
                  .withText(getString(R.string.qq_space))
                  .setCallback(mUmShareListener)
                  .share();
              mSharePopup.dismiss();
            } else {
              ToastUtils.showShort("未安装QQ");
            }
            break;
          case CIRCLE:
            if (isInstallWeixin) {
              new ShareAction(mContext)
                  .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                  .withText(getString(R.string.friend_circle))
                  .setCallback(mUmShareListener)
                  .share();
              mSharePopup.dismiss();
            } else {
              ToastUtils.showShort("未安装微信");
            }
            break;
          case WEIXIN:
            if (isInstallWeixin) {
              new ShareAction(mContext)
                  .setPlatform(SHARE_MEDIA.WEIXIN)
                  .withText(getString(R.string.weixin))
                  .setCallback(mUmShareListener)
                  .share();
              mSharePopup.dismiss();
            } else {
              ToastUtils.showShort("未安装微信");
            }
            break;
          case COPY:
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText("小冤家你干嘛像个傻瓜");
            mSharePopup.dismiss();
            break;
        }


        SuccessDialogFragment fg = SuccessDialogFragment.newInstance(R.drawable.m_ic_score_success,
            getString(R.string.share_store_get_2_score));
        fg.show(getSupportFragmentManager(),"share_dialog");

      }
    });
  }

  private UMShareListener mUmShareListener = new UMShareListener() {
    @Override public void onStart(SHARE_MEDIA media) {

    }

    @Override public void onResult(SHARE_MEDIA media) {
      ToastUtils.showShort(getString(R.string.share_success));
    }

    @Override public void onError(SHARE_MEDIA media, Throwable throwable) {
      ToastUtils.showShort(getString(R.string.share_fail));
    }

    @Override public void onCancel(SHARE_MEDIA media) {
      ToastUtils.showShort(getString(R.string.share_cacel));
    }
  };

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onSureClicked(Success success) {
    ToastUtils.showShort(R.string.share_success);
  }
}
