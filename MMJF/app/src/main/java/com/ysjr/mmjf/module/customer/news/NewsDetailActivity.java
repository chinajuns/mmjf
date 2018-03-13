package com.ysjr.mmjf.module.customer.news;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
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
import com.ysjr.mmjf.entity.NewsBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.module.popup.SharePopup;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.widget.loading.KProgressHUD;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-12-4.
 */

public class NewsDetailActivity extends TopBarBaseActivity {
  public static final String KEY_ARTICLE_BEAN = "article_bean";
  public static final String KEY_IS_FROM_COLLECT = "is_from_collect";
  private boolean isCollect;
  @BindView(R.id.webView) WebView webView;
  private String TAG = NewsDetailActivity.class.getSimpleName();
  protected boolean isInstallSina;
  protected boolean isInstallWeixin;
  protected boolean isInstallQQ;
  private SharePopup mSharePopup;
  private NewsBean.ListParent.ListChild newsBean;
  private boolean isFromCollect;
  private KProgressHUD hud;
  private boolean isFristIn = true;
  @Override protected int getContentView() {
    return R.layout.c_activity_news_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    initDialog();
    isFromCollect = getIntent().getBooleanExtra(KEY_IS_FROM_COLLECT, false);
    newsBean = (NewsBean.ListParent.ListChild) getIntent().getSerializableExtra(KEY_ARTICLE_BEAN);
    isInstallSina = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.SINA);
    isInstallQQ = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ);
    isInstallWeixin = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN);
    setTitle(getString(R.string.news_detail));
    setTopRightButton("", R.drawable.btn_dot_more, new OnClickListener() {
      @Override public void onClick() {
        showMorePopup();
      }
    });
    loadWebView();
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
  private void loadWebView() {
    final WebSettings webSettings = webView.getSettings();
    // 设置与Js交互的权限
    webSettings.setJavaScriptEnabled(true);
    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    webSettings.setAllowContentAccess(true);
    webSettings.setLoadsImagesAutomatically(false);
    webView.setWebViewClient(new WebViewClient(){
      @RequiresApi(api = Build.VERSION_CODES.M) @Override public void onReceivedError(WebView view, WebResourceRequest request,
          WebResourceError error) {
        super.onReceivedError(view, request, error);
        hud.dismiss();
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (!isFristIn) {
          return;
        }
        isFristIn = false;
        hud.dismiss();
        String token = SPUtils.getInstance().getString(Constant.KEY_TOKEN_SP);
        String deviceid = SPUtils.getInstance().getString(Constant.KEY_IMEI_SP);
        String method = "javascript:atokenResult(\""
            + token
            + "\",\""
            + deviceid
            + "\","
            + (isFromCollect?newsBean.object_id:newsBean.id)
            + ")";
        webView.evaluateJavascript(method, new ValueCallback<String>() {
          @Override public void onReceiveValue(String value) {

          }
        });
        webSettings.setLoadsImagesAutomatically(true);
      }
    });
    webView.loadUrl(Api.NEWS_DETAIL_H5);
  }

  @Override protected void onResume() {
    super.onResume();
    if (isLogin()) {
      OkGo.<JSONObject>post(Api.MOBILE_CLIENT_CHECK_FAVOURITE)
          .tag(mContext)
          .params("id",(isFromCollect?newsBean.object_id:newsBean.id))
          .params("type",2)
          .execute(new JsonCallback<JSONObject>() {
            @Override public void onSuccess(Response<JSONObject> response) {
              try {
                int favorite = response.body().getJSONObject("data").getInt("is_favorite");
                if (favorite == 1) {
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
  }

  private void showMorePopup() {
    NewsPopup popup = new NewsPopup(mContext, isCollect);
    popup.setListener(new NewsPopup.OnItemClickListener() {
      @Override public void onClick(NewsPopup.ItemType type) {
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
    popup.showPopupWindow(R.id.menu_1);
  }

  private void httpCollect() {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_MEMBER_SET_FAVORITE)
        .tag(mContext)
        .params("id",(isFromCollect?newsBean.object_id:newsBean.id))
        .params("type",2)
        .params("action",isCollect?"cancel":"add")
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            ToastUtils.setBgColor(Color.BLACK);
            if (isCollect) {
              ToastUtils.showShort(R.string.cancel_collect_success);
            } else {
              ToastUtils.showShort(R.string.collect_success);
            }
            isCollect = !isCollect;
          }
        });

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
          String url = "http://h5.kuanjiedai.com/Article-details-shar.html?art_id=" + (isFromCollect?newsBean.object_id:newsBean.id);
          ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
          cm.setText(url);
          mSharePopup.dismiss();
          break;
      }
    }
  };

  private void shareUrl(SHARE_MEDIA share_media) {
    String url = "http://h5.kuanjiedai.com/Article-details-shar.html?art_id=" + (isFromCollect?newsBean.object_id:newsBean.id);
    UMWeb umWeb = new UMWeb(url);
    umWeb.setTitle(newsBean.title);
    String thumbUrl = "";
    if (newsBean != null) {
      if (newsBean.picture.contains("http")) {
        thumbUrl = newsBean.picture;
      } else {
        thumbUrl = Api.IMAGE_ROOT_URL + newsBean.picture;
      }
    }
    UMImage umImage = new UMImage(mContext, thumbUrl);
    umWeb.setThumb(umImage);
    umWeb.setDescription(newsBean.introduce);
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
