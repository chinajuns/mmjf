package com.ysjr.mmjf.http;

import android.content.Context;
import android.text.TextUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.lzy.okgo.request.base.Request;
import com.ysjr.mmjf.widget.loading.KProgressHUD;

/**
 * Created by Administrator on 2017-11-27.
 */

public abstract class DialogCallback<T> extends JsonCallback<T>{
  private KProgressHUD hud;
  private String mDesc;
  private int mGraceTime;
  public DialogCallback(Context context) {
    initHud(context,mDesc);
  }

  private void initHud(Context context,String desc) {
    hud = KProgressHUD.create(context)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel(TextUtils.isEmpty(desc) ? null : desc)
        .setDimAccount(0.6f)
        .setSize(SizeUtils.px2dp(ScreenUtils.getScreenWidth() / 3),
            SizeUtils.px2dp(ScreenUtils.getScreenWidth() / 3))
        .setCornerRadius(30)
        .setCancelable(false)
        .setGraceTime(mGraceTime);

  }

  public DialogCallback(Context context,String desc) {
    mDesc = desc;
    initHud(context,desc);
  }
  public DialogCallback(Context context,String desc,int graceTime) {
    mDesc = desc;
    mGraceTime = graceTime;
    initHud(context,desc);
  }
  @Override public void onStart(Request<T, ? extends Request> request) {
    super.onStart(request);
    if (hud != null) hud.show();
  }

  @Override public void onFinish() {
    super.onFinish();
    if (hud != null) hud.dismiss();
  }
}
