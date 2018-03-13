package com.ysjr.mmjf.module.manager.namecard;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
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
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.module.customer.store.CStoreJsInterface;
import com.ysjr.mmjf.module.popup.SharePopup;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.widget.loading.KProgressHUD;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MNameCardActivity extends TopBarBaseActivity {
  public static final String KEY_LOANER_ID = "loaner_id";

  @BindView(R.id.webView) WebView webView;
  protected boolean isInstallSina;
  protected boolean isInstallWeixin;
  protected boolean isInstallQQ;
  private SharePopup mSharePopup;
  private int loaner_id;
  private KProgressHUD hud;
  @Override protected void setUIBeforeSetContentView() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      android.webkit.WebView.enableSlowWholeDocumentDraw();
    }
  }

  @Override protected int getContentView() {
    return R.layout.m_activity_name_card;
  }

  @Override protected void init(Bundle savedInstanceState) {
    initDialog();
    loaner_id = getIntent().getIntExtra(KEY_LOANER_ID, 0);
    isInstallSina = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.SINA);
    isInstallQQ = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ);
    isInstallWeixin = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN);
    setTitle(getString(R.string.small_name_card));
    setTopRightButton("", R.drawable.ic_card_share, new OnClickListener() {
      @Override public void onClick() {
        mSharePopup = new SharePopup(mContext);
        mSharePopup.setListener(mShareListener);
        mSharePopup.showPopupWindow();
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();
    loadWebView();
  }

  private void loadWebView() {
    webView.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    WebSettings webSettings = webView.getSettings();
    // 设置与Js交互的权限
    webSettings.setJavaScriptEnabled(true);
    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    webSettings.setDomStorageEnabled(true);
    webView.loadUrl("http://h5.kuanjiedai.com/sharecard.html");
    webView.setWebViewClient(new WebViewClient() {
      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        hud.dismiss();
        String token = SPUtils.getInstance().getString(Constant.KEY_TOKEN_SP);
        String deviceid = SPUtils.getInstance().getString(Constant.KEY_IMEI_SP);
        final String jsMethod = "javascript:getCard(\"" + deviceid + "\",\"" + token + "\"," + loaner_id + ")";
        //final String jsMethod = "javascript:tokenResult('" + token + "','" + deviceid + "','" + id + "')";
        webView.post(new Runnable() {
          @Override public void run() {
            webView.loadUrl(jsMethod);
          }
        });
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    UMShareAPI.get(this).release();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
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
    Bitmap bitmap = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      bitmap = captureWebViewLollipop(webView);
    } else {
      bitmap = captureWebViewKitKat(webView);
    }
    UMImage image = new UMImage(mContext, bitmap);
    new ShareAction(mContext)
        .setPlatform(share_media)
        .withMedia(image)
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
  //5.0下
  private static Bitmap captureWebViewKitKat(WebView webView) {
    Picture picture = webView.capturePicture();
    int width = picture.getWidth();
    int height = picture.getHeight();
    if (width > 0 && height > 0) {
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
      Canvas canvas = new Canvas(bitmap);
      picture.draw(canvas);
      return bitmap;
    }
    return null;
  }

  //5.0上
  private Bitmap captureWebViewLollipop(WebView webView) {
    float scale = webView.getScale();
    int width = webView.getWidth();
    int height = (int) (webView.getContentHeight() * scale + 0.5);
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    Canvas canvas = new Canvas(bitmap);
    webView.draw(canvas);
    return bitmap;
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
}
