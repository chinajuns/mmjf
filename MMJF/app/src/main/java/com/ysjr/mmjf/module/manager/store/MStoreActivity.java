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
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.entity.Customer;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.MStore;
import com.ysjr.mmjf.entity.MThrowOrderBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.manager.adapter.MStoreAdapter;
import com.ysjr.mmjf.module.manager.cus_order.MCusOrderActivity;
import com.ysjr.mmjf.module.manager.cus_order.MCusOrderDetailActivity;
import com.ysjr.mmjf.module.manager.delegate.MDelegateActivity;
import com.ysjr.mmjf.module.manager.delegate.MDelegatedActivity;
import com.ysjr.mmjf.module.manager.namecard.MNameCardActivity;
import com.ysjr.mmjf.module.popup.SharePopup;
import com.ysjr.mmjf.utils.Api;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-12.
 */

public class MStoreActivity extends BaseActivity {
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.tvTitle) TextView tvTitle;
  private TextView tvCusOrder;
  private TextView tvDelegatePro;
  private TextView tvDelegatedPro;
  private TextView tvShareStore;
  private TextView tvShareCard;
  private TextView tvName;
  private TextView tvScore;
  private TextView tvLookNum;
  private TextView tvAddress;
  private ImageView ivAvatar;
  private List<MThrowOrderBean> mOrderList = new ArrayList<>();
  private MStoreAdapter mAdapter;
  private LinearLayoutManager layoutManager;
  protected boolean isInstallSina;
  protected boolean isInstallWeixin;
  protected boolean isInstallQQ;
  private SharePopup mSharePopup;
  private int loaner_id;
  private MStore.ShopInfo mShopInfo;
  @Override public int getLayoutId() {
    return R.layout.m_activity_store;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    isInstallSina = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.SINA);
    isInstallQQ = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ);
    isInstallWeixin = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN);
    ButterKnife.bind(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ActivityUtils.finishToActivity(MainActivity.class, false);
      }
    });
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      View decorView = getWindow().getDecorView();
      int option = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
      decorView.setSystemUiVisibility(option);
      BarUtils.setStatusBarColor(mContext, Color.TRANSPARENT, 0, true);
    }
    initData();
  }

  private void initData() {
    httpGetStoreInfo();
  }

  private void httpGetStoreInfo() {
    OkGo.<HttpResult<MStore>>post(Api.MOBILE_BUSINESS_SHOP_INDEX).tag(mContext)
        .execute(new JsonCallback<HttpResult<MStore>>() {
          @Override public void onSuccess(Response<HttpResult<MStore>> response) {
            final MStore store = response.body().data;
            loaner_id = store.shop_info.loaner_id;
            mShopInfo = store.shop_info;
            List<MThrowOrderBean> beanList = store.order;
            if (beanList == null || beanList.size() == 0) {
              MThrowOrderBean bean = new MThrowOrderBean();
              bean.isEmpty = true;
              mOrderList.add(bean);
            } else {
              mOrderList.addAll(beanList);
            }
            mAdapter = new MStoreAdapter(R.layout.m_store_item, mOrderList);
            View head = LayoutInflater.from(mContext).inflate(R.layout.m_store_head, null);
            tvCusOrder = head.findViewById(R.id.tvCusOrder);
            tvDelegatedPro = head.findViewById(R.id.tvDelegatedPro);
            tvDelegatePro = head.findViewById(R.id.tvDelegatePro);
            tvShareStore = head.findViewById(R.id.tvShareStore);
            tvShareCard = head.findViewById(R.id.tvShareCard);
            tvName = head.findViewById(R.id.tvName);
            tvAddress = head.findViewById(R.id.tvAddress);
            tvScore = head.findViewById(R.id.tvScore);
            tvLookNum = head.findViewById(R.id.tvLookNum);
            ivAvatar = head.findViewById(R.id.ivAvatar);
            tvName.setText(store.shop_info.username);
            tvAddress.setText(store.shop_info.service_city);
            tvScore.setText(store.shop_info.score+"积分");
            tvLookNum.setText(store.shop_info.pageviews+"");
            Glide.with(mContext)
                .load(Api.IMAGE_ROOT_URL+store.shop_info.header_img)
                .error(R.drawable.img_default_avatar)
                .into(ivAvatar);
            tvCusOrder.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                Intent intent = new Intent(mContext,MCusOrderActivity.class);
                intent.putExtra(MCusOrderActivity.KEY_REFER, "customer");
                ActivityUtils.startActivity(intent);
              }
            });
            tvDelegatePro.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                ActivityUtils.startActivity(MDelegateActivity.class);
              }
            });
            tvDelegatedPro.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                ActivityUtils.startActivity(MDelegatedActivity.class);
              }
            });
            tvShareStore.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                mSharePopup = new SharePopup(mContext);
                mSharePopup.setListener(mShareListener);
                mSharePopup.showPopupWindow();
              }
            });
            tvShareCard.setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                Intent intent = new Intent(mContext, MNameCardActivity.class);
                intent.putExtra(MNameCardActivity.KEY_LOANER_ID, loaner_id);
                ActivityUtils.startActivity(intent);
              }
            });
            mAdapter.addHeaderView(head);
            recyclerView.setAdapter(mAdapter);
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
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
            if (beanList != null && beanList.size() > 0) {
              mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                  MThrowOrderBean bean = mOrderList.get(position);
                  Intent intent = new Intent(mContext, MCusOrderDetailActivity.class);
                  intent.putExtra(MCusOrderDetailActivity.KEY_ORDER_ID, bean.id);
                  intent.putExtra(MCusOrderDetailActivity.KEY_REFER, "customer");
                  ActivityUtils.startActivity(intent);
                }
              });
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

  @Override public void onBackPressed() {
    ActivityUtils.finishToActivity(MainActivity.class, false);
  }
  private SharePopup.OnItemClickListener mShareListener = new SharePopup.OnItemClickListener() {
    @Override public void onClick(SharePopup.ItemType type) {
      switch (type) {
        case SINA:
          if (isInstallSina) {
            shareUrl(SHARE_MEDIA.SINA);
          } else {
            ToastUtils.showShort("未安装新浪微博");
          }
          break;
        case QQ:
          if (isInstallQQ) {
            shareUrl(SHARE_MEDIA.QQ);
          } else {
            ToastUtils.showShort("未安装QQ");
          }
          break;
        case QQSPACE:
          if (isInstallQQ) {
            shareUrl(SHARE_MEDIA.QZONE);
          } else {
            ToastUtils.showShort("未安装QQ");
          }
          break;
        case CIRCLE:
          if (isInstallWeixin) {
            shareUrl(SHARE_MEDIA.WEIXIN_CIRCLE);
          } else {
            ToastUtils.showShort("未安装微信");
          }
          break;
        case WEIXIN:
          if (isInstallWeixin) {
            shareUrl(SHARE_MEDIA.WEIXIN);
          } else {
            ToastUtils.showShort("未安装微信");
          }
          break;
        case COPY:
          String url = "http://h5.kuanjiedai.com/indexShare.html?art_id=" + loaner_id;
          ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
          cm.setText(url);
          mSharePopup.dismiss();
          break;
      }
    }
  };

  private void shareUrl(SHARE_MEDIA share_media) {
    String url = "http://h5.kuanjiedai.com/indexShare.html?art_id=" + loaner_id;
    UMWeb umWeb = new UMWeb(url);
    umWeb.setTitle(mShopInfo.username);
    String thumbUrl = "";
    if (mShopInfo.header_img != null) {
      if (mShopInfo.header_img.contains("http")) {
        thumbUrl = mShopInfo.header_img;
      } else {
        thumbUrl = Api.IMAGE_ROOT_URL + mShopInfo.header_img;
      }
    }
    UMImage umImage = new UMImage(mContext, thumbUrl);
    umWeb.setThumb(umImage);
    umWeb.setDescription(mShopInfo.introduce);
    new ShareAction(mContext)
        .setPlatform(share_media)
        .withMedia(umWeb)
        .setCallback(mUmShareListener)
        .share();
    mSharePopup.dismiss();
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
}
