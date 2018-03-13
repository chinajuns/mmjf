package com.ysjr.mmjf.module.customer.me.set;

import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.widget.loading.KProgressHUD;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CAboutUsActivity extends TopBarBaseActivity {
  @BindView(R.id.webView) WebView webView;
  private KProgressHUD hud;
  @Override protected int getContentView() {
    return R.layout.c_activity_about_us;
  }

  @Override protected void init(Bundle savedInstanceState) {
    initDialog();
    setTitle(getString(R.string.about_us));
    webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    webView.loadUrl("http://h5.kuanjiedai.com/aboutus.html");
    webView.setWebViewClient(new WebViewClient(){
      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (hud !=null )hud.dismiss();
      }

      @Override public void onReceivedError(WebView view, WebResourceRequest request,
          WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (hud != null) hud.dismiss();
      }
    });
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
