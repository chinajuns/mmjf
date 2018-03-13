package com.ysjr.mmjf.module.manager.me;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.me.prize.CPrizeActivity;
import com.ysjr.mmjf.module.customer.me.score.CScoreActivity;
import com.ysjr.mmjf.module.customer.me.set.CSettingActivity;
import com.ysjr.mmjf.module.manager.cus_order.MCusOrderActivity;
import com.ysjr.mmjf.module.manager.me.verify.MVerify4Activity;
import com.ysjr.mmjf.module.manager.throw_order.MThrowOrderListActivity;
import com.ysjr.mmjf.module.manager.wallet.MWalletActivity;
import com.ysjr.mmjf.module.manager.me.verify.MVerify1Activity;
import com.ysjr.mmjf.utils.Api;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017-12-8.
 */

public class MMeFragment extends BaseFragment {
  private static final int REQUEST_CODE_CHOOSE = 1;
  @BindView(R.id.ivAvatar) CircleImageView ivAvatar;
  @BindView(R.id.tvName) TextView tvName;
  @BindView(R.id.tvAccount) TextView tvAccount;
  private User user;
  private String mAvatarPath;

  public static MMeFragment newInstance() {
    MMeFragment fg = new MMeFragment();
    return fg;
  }

  @Override public int getLayoutId() {
    return R.layout.m_fragment_me;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    user = DataSupport.findFirst(User.class);
    Glide.with(mContext).load(Api.IMAGE_ROOT_URL + user.header_img).error(R.drawable.img_default_avatar).into(ivAvatar);
    tvName.setText(user.username);
    tvAccount.setText(user.mobile);
  }
  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
      List<String> result = Matisse.obtainPathResult(data);
      if (result != null && result.size() > 0) {
        mAvatarPath = result.get(0);
        httpUploadAvatar();
      }
    }}
  @OnClick(R.id.layoutMyScore) public void onLayoutMyScoreClicked() {
    ActivityUtils.startActivity(CScoreActivity.class);
  }

  @OnClick(R.id.layoutMyWallet) public void onLayoutMyWalletClicked() {
    ActivityUtils.startActivity(MWalletActivity.class);
  }

  @OnClick(R.id.layoutMyOrder) public void onLayoutMyOrderClicked() {
    Intent intent = new Intent(mContext,MCusOrderActivity.class);
    intent.putExtra(MCusOrderActivity.KEY_REFER, "junk");
    ActivityUtils.startActivity(intent);
  }

  @OnClick(R.id.layoutRecommendPrize) public void onLayoutRecommendPrizeClicked() {
    ActivityUtils.startActivity(CPrizeActivity.class);
  }

  @OnClick(R.id.layoutSetting) public void onLayoutSettingClicked() {
    ActivityUtils.startActivity(CSettingActivity.class);
  }

  @OnClick(R.id.layoutThrowOrder) public void onLayoutThrowOrderClicked() {
    ActivityUtils.startActivity(MThrowOrderListActivity.class);
  }
  @OnClick(R.id.layoutVerifyName) public void onLayoutVerifyNameClicked() {
    user = DataSupport.findFirst(User.class);
    if (user != null && user.is_auth == 1) {
      ActivityUtils.startActivity(MVerify1Activity.class);
    } else {
      ActivityUtils.startActivity(MVerify4Activity.class);
    }
  }

  @OnClick(R.id.ivAvatar) public void onIvAvatarClicked() {
    Matisse.from(this)
        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
        .countable(true)
        .capture(true)
        .captureStrategy(new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .thumbnailScale(0.85f)
        .imageEngine(new GlideEngine())
        .forResult(REQUEST_CODE_CHOOSE);
  }

  private void httpUploadAvatar() {
    OkGo.<JSONObject>post(Api.UPLOAD_IMAGE_URL)
        .tag(mContext)
        .params("image",new File(mAvatarPath))
        .execute(new DialogCallback<JSONObject>(mContext) {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              final String url = response.body().getJSONObject("data").getString("src");
              OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_USER_AVATAR)
                  .tag(mContext)
                  .params("url",url)
                  .execute(new JsonCallback<HttpResult<Void>>() {
                    @Override public void onSuccess(Response<HttpResult<Void>> response) {
                      Glide.with(mContext).load(mAvatarPath).into(ivAvatar);
                      User user = DataSupport.findFirst(User.class);
                      user.header_img = url;
                      user.save();
                    }
                  });
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

}
