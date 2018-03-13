package com.ysjr.mmjf.module.customer.store;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.apply.ApplyLoanActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.module.popup.SharePopup;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.widget.loading.KProgressHUD;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-11-29.
 */

public class CStoreActivity extends TopBarBaseActivity {
  public static final String KEY_MANAGER_ID = "key_manager";
  public static final String KEY_HEADER_IMAGE = "header_img";
  public static final String KEY_MANAGER_NAME = "manager_name";
  public static final String KEY_MANAGER_TAG = "manager_tag";
  private static final String TAG = CStoreActivity.class.getSimpleName();
  protected boolean isInstallSina;
  protected boolean isInstallWeixin;
  protected boolean isInstallQQ;
  private SharePopup mSharePopup;
  private boolean isCollect;
  private int id;
  private String header_img;
  private String manager_name;
  private String manager_tag;
  @BindView(R.id.webView) WebView webView;
  MorePopup mMorePopup;
  private KProgressHUD hud;
  @Override protected int getContentView() {
    return R.layout.c_activity_store;
  }

  @Override protected void init(Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    initDialog();
    id = getIntent().getIntExtra(KEY_MANAGER_ID,0);
    header_img = getIntent().getStringExtra(KEY_HEADER_IMAGE);
    manager_name = getIntent().getStringExtra(KEY_MANAGER_NAME);
    manager_tag = getIntent().getStringExtra(KEY_MANAGER_TAG);
    isInstallSina = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.SINA);
    isInstallQQ = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ);
    isInstallWeixin = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN);

    setTitle(getString(R.string.store));
    setTopLeftButton(R.drawable.btn_black_back);
    setTopRightButton("", R.drawable.btn_dot_more, new OnClickListener() {
      @Override public void onClick() {
        showMorePopup();
      }
    });

    if (isLogin()) {
      checkIfCollect();
    }

    loadWebView();
  }


  @Override protected void onDestroy() {
    UMShareAPI.get(this).release();
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
  }
  private void initDialog() {
    hud = KProgressHUD.create(mContext)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setDimAccount(0.6f)
        .setSize(SizeUtils.px2dp(ScreenUtils.getScreenWidth() / 3),
            SizeUtils.px2dp(ScreenUtils.getScreenWidth() / 3))
        .setCornerRadius(30)
        .setGraceTime(500);
    hud.show();
  }
  @OnClick(R.id.btnWantLoan) public void onViewClicked() {
    if (isLogin()) {
      Intent intent = new Intent(mContext, ApplyLoanActivity.class);
      intent.putExtra(ApplyLoanActivity.KEY_MANAGER_ID, id);
      ActivityUtils.startActivity(intent);
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  private void checkIfCollect() {
    OkGo.<JSONObject>post(Api.MOBILE_CLIENT_CHECK_FAVOURITE).tag(mContext)
        .params("type", 1)
        .params("id", id)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              int isFavorite = response.body().getJSONObject("data").getInt("is_favorite");
              if (isFavorite == 1) {
                isCollect = true;
              } else {
                isCollect = false;
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

  private void loadWebView() {
    webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    WebSettings webSettings = webView.getSettings();
    // 设置与Js交互的权限
    webSettings.setJavaScriptEnabled(true);
    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    webSettings.setDomStorageEnabled(true);
    webView.addJavascriptInterface(new CStoreJsInterface(mContext, id), "Android");
    webView.loadUrl("http://h5.kuanjiedai.com/");
    webView.setWebViewClient(new WebViewClient() {
      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        hud.dismiss();
        String token = SPUtils.getInstance().getString(Constant.KEY_TOKEN_SP);
        String deviceid = SPUtils.getInstance().getString(Constant.KEY_IMEI_SP);
        final String jsMethod = "javascript:tokenResult(\"" + token + "\",\"" + deviceid + "\"," + id + ")";
        webView.post(new Runnable() {
          @Override public void run() {
            webView.loadUrl(jsMethod);
          }
        });
      }

      @Override public void onReceivedError(WebView view, WebResourceRequest request,
          WebResourceError error) {
        super.onReceivedError(view, request, error);
        hud.dismiss();
      }
    });
  }

  private void showMorePopup() {
    mMorePopup = new MorePopup(mContext, isCollect);
    mMorePopup.showPopupWindow(R.id.menu_1);
    mMorePopup.setListener(new MorePopup.OnItemClickListener() {
      @Override public void onClick(MorePopup.ItemType type) {
        switch (type) {
          case SHARE:
            mSharePopup = new SharePopup(mContext);
            mSharePopup.setListener(mShareListener);
            mSharePopup.showPopupWindow();
            break;
          case COLLECT:
            if (isLogin()) {
              httpCollect();
            } else {
              ActivityUtils.startActivity(NavigationActivity.class);
            }
            break;
        }
      }
    });
  }

  private void httpCollect() {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_MEMBER_SET_FAVORITE).tag(mContext)
        .params("id", id)
        .params("type", 1)
        .params("action", isCollect ? "cancel" : "add")
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            if (isCollect) {
              ToastUtils.setBgColor(Color.BLACK);
              ToastUtils.showShort(getString(R.string.cancel_collect_success));
            } else {
              ToastUtils.setBgColor(Color.BLACK);
              ToastUtils.showShort(getString(R.string.collect_success));
            }
            isCollect = !isCollect;
          }
        });
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void onLogin(User user) {
    checkIfCollect();
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
          String url = "http://h5.kuanjiedai.com/indexShare.html?art_id=" + id;
          ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
          cm.setText(url);
          mSharePopup.dismiss();
          break;
      }
    }
  };

  private void shareUrl(SHARE_MEDIA share_media) {
    String url = "http://h5.kuanjiedai.com/indexShare.html?art_id=" + id;
    UMWeb umWeb = new UMWeb(url);
    umWeb.setTitle(manager_name);
    String thumbUrl = "";
    if (header_img != null) {
      if (header_img.contains("http")) {
        thumbUrl = header_img;
      } else {
        thumbUrl = Api.IMAGE_ROOT_URL + header_img;
      }
    }
    UMImage umImage = new UMImage(mContext, thumbUrl);
    umWeb.setThumb(umImage);
    umWeb.setDescription(manager_tag);
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
