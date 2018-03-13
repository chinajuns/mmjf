package com.ysjr.mmjf.module.customer.me.prize;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
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
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.PrizeBean;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.popup.SharePopup;
import com.ysjr.mmjf.utils.Api;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-12-6.
 */

public class CPrizeActivity extends BaseActivity {
  @BindView(R.id.tvInviteNumber) TextView tvInviteNumber;
  @BindView(R.id.tvGetScore) TextView tvGetScore;
  @BindView(R.id.tvCode) TextView tvCode;
  @BindView(R.id.ivCode) ImageView ivCode;
  @BindView(R.id.frameCode) FrameLayout frameCode;
  private PrizeBean prizeBean;
  protected boolean isInstallSina;
  protected boolean isInstallWeixin;
  protected boolean isInstallQQ;
  private SharePopup mSharePopup;
  @Override public int getLayoutId() {
    return R.layout.c_activity_prize;
  }

  @Override public void initialize(Bundle savedInstanceState) {
    isInstallSina = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.SINA);
    isInstallQQ = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.QQ);
    isInstallWeixin = UMShareAPI.get(this).isInstall(this, SHARE_MEDIA.WEIXIN);
    ButterKnife.bind(this);
    httpGetInfo();
  }

  private void httpGetInfo() {
    OkGo.<HttpResult<PrizeBean>>get(Api.MOBILE_CLIENT_MEMBER_INVITE_INFO)
        .tag(mContext)
        .execute(new JsonCallback<HttpResult<PrizeBean>>() {
          @Override public void onSuccess(Response<HttpResult<PrizeBean>> response) {
            prizeBean = response.body().data;
            tvGetScore.setText(String.valueOf(prizeBean.number));
            tvInviteNumber.setText(String.valueOf(prizeBean.count));
            tvCode.setText(prizeBean.mobile);
          }
        });

  }

  @OnClick(R.id.btnBack) public void onBtnBackClicked() {
    finish();
  }

  @OnClick(R.id.btnRule) public void onBtnRuleClicked() {
    ActivityUtils.startActivity(CPrizeRuleActivity.class);
  }

  @OnClick(R.id.btnShare) public void onBtnShareClicked() {
    mSharePopup = new SharePopup(mContext);
    mSharePopup.setListener(mShareListener);
    mSharePopup.showPopupWindow();
  }

  @OnClick(R.id.btnCode) public void onBtnCodeClicked() {
    OkGo.<JSONObject>post(Api.WEB_GET_QRCODE)
        .tag(mContext)
        .params("url","http://h5.kuanjiedai.com/share.html?mobile="+prizeBean.mobile)
        .execute(new DialogCallback<JSONObject>(mContext) {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              String imgUrl = response.body().getJSONObject("data").getString("qrcode");
              frameCode.setVisibility(View.VISIBLE);
              if (!imgUrl.contains("http")) {
                imgUrl = Api.IMAGE_ROOT_URL + imgUrl;
              }
              Glide.with(mContext).load(imgUrl).into(ivCode);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

  @OnClick(R.id.frameCode) public void onCodeMaskClicked() {
    frameCode.setVisibility(View.GONE);
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
          String url = "http://h5.kuanjiedai.com/share.html?mobile=" + prizeBean.mobile;
          ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
          cm.setText(url);
          mSharePopup.dismiss();
          break;
      }
    }
  };

  private void shareUrl(SHARE_MEDIA share_media) {
    String url = "http://h5.kuanjiedai.com/share.html?mobile=" + prizeBean.mobile;
    UMWeb umWeb = new UMWeb(url);
    umWeb.setTitle("推荐有奖");
    UMImage umImage = new UMImage(mContext, R.mipmap.icon);
    umWeb.setThumb(umImage);
    umWeb.setDescription("邀请有奖");
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
