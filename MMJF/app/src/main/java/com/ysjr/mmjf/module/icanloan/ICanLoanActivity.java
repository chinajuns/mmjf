package com.ysjr.mmjf.module.icanloan;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.widget.loading.KProgressHUD;

/**
 * Created by Administrator on 2018-1-12.
 */

public class ICanLoanActivity extends TopBarBaseActivity {
  private static final String TAG = ICanLoanActivity.class.getSimpleName();
  @BindView(R.id.webView) WebView webView;
  private KProgressHUD hud;
  @Override protected int getContentView() {
    return R.layout.activity_i_can_loan;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle("我能贷多少");
    initDialog();
    loadWebView();
  }
  private void loadWebView() {
    WebSettings webSettings = webView.getSettings();
    // 设置与Js交互的权限
    webSettings.setJavaScriptEnabled(true);
    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    webSettings.setAllowContentAccess(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setAllowFileAccessFromFileURLs(true);
    webSettings.setAllowUniversalAccessFromFileURLs(true);
    webView.setWebViewClient(new WebViewClient(){
      @RequiresApi(api = Build.VERSION_CODES.M) @Override public void onReceivedError(WebView view, WebResourceRequest request,
          WebResourceError error) {
        error.getDescription();
        super.onReceivedError(view, request, error);
        hud.dismiss();
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        hud.dismiss();
        webView.post(new Runnable() {
          @Override public void run() {
            String token = SPUtils.getInstance().getString(Constant.KEY_TOKEN_SP);
            String deviceid = SPUtils.getInstance().getString(Constant.KEY_IMEI_SP);
            String url = "javascript:showResult(\""
                + deviceid
                + "\",\""
                + token
                + "\")";
            webView.loadUrl(url);
          }
        });
      }
    });
    webView.loadUrl("http://h5.kuanjiedai.com/How.html");
    webView.addJavascriptInterface(new ICanLoanJsInterface(mContext),"Android");
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
