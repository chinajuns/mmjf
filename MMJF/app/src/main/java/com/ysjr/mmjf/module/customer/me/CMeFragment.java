package com.ysjr.mmjf.module.customer.me;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.ysjr.mmjf.module.calculator.CalculatorActivity;
import com.ysjr.mmjf.module.customer.me.bind.Bind3PlatActivity;
import com.ysjr.mmjf.module.customer.me.collect.CMyCollectActivity;
import com.ysjr.mmjf.module.customer.me.order.CMyOrderActivity;
import com.ysjr.mmjf.module.customer.me.prize.CPrizeActivity;
import com.ysjr.mmjf.module.customer.me.score.CScoreActivity;
import com.ysjr.mmjf.module.customer.me.set.CSettingActivity;
import com.ysjr.mmjf.module.customer.me.verify.CVerify1StepActivity;
import com.ysjr.mmjf.module.customer.me.verify.CVerify3StepActivity;
import com.ysjr.mmjf.module.icanloan.ICanLoanActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.utils.Api;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017-11-28.
 */

public class CMeFragment extends BaseFragment {
  private static final int REQUEST_CODE_CHOOSE = 1;
  @BindView(R.id.ivAvatar) CircleImageView ivAvatar;
  @BindView(R.id.tvName) TextView tvName;
  @BindView(R.id.tvPhone) TextView tvPhone;
  private String mAvatarPath;

  public static CMeFragment newInstance() {
    CMeFragment fg = new CMeFragment();
    return fg;
  }

  @Override public int getLayoutId() {
    return R.layout.c_fragment_me;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    if (isLogin()) {
      User user = DataSupport.findFirst(User.class);
      bindUserInfo(user);
    }
  }
  @Override public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
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

  @OnClick(R.id.ivAvatar) public void onIvAvatarClicked() {
    if (isLogin()) {
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
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  @OnClick(R.id.ivICanLoan) public void onIvICanLoanClicked() {
    ActivityUtils.startActivity(ICanLoanActivity.class);
  }

  @OnClick(R.id.ivCalculator) public void onIvCalculatorClicked() {
    ActivityUtils.startActivity(CalculatorActivity.class);
  }

  @OnClick(R.id.layoutMyScore) public void onLayoutMyScoreClicked() {
    if (isLogin()) {
      ActivityUtils.startActivity(CScoreActivity.class);
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  @OnClick(R.id.layoutMyOrder) public void onLayoutMyOrderClicked() {
    if (isLogin()) {
      ActivityUtils.startActivity(CMyOrderActivity.class);
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  @OnClick(R.id.layoutMyCollect) public void onLayoutMyCollectClicked() {
    if (isLogin()) {
      ActivityUtils.startActivity(CMyCollectActivity.class);
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  @OnClick(R.id.layoutRecommendPrize) public void onLayoutRecommendPrizeClicked() {
    if (isLogin()) {
      ActivityUtils.startActivity(CPrizeActivity.class);
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  @OnClick(R.id.layoutBecomeManager) public void onLayoutBecomeManagerClicked() {
    ActivityUtils.startActivity(NavigationActivity.class);
  }

  @OnClick(R.id.layoutBindAccount) public void onLayoutBindAccountClicked() {
    if (isLogin()) {
      ActivityUtils.startActivity(Bind3PlatActivity.class);
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  @OnClick(R.id.layoutSetting) public void onLayoutSettingClicked() {
    if (isLogin()) {
      ActivityUtils.startActivity(CSettingActivity.class);
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  @OnClick(R.id.layoutVerifyName) public void onLayoutVerifyNameClicked() {
    if (isLogin()) {
      User user = DataSupport.findFirst(User.class);
      if (user.is_auth == 1) {//未认证
        ActivityUtils.startActivity(CVerify1StepActivity.class);
      } else {
        ActivityUtils.startActivity(CVerify3StepActivity.class);
      }
    } else {
      ActivityUtils.startActivity(NavigationActivity.class);
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onUserLogin(User user) {
   bindUserInfo(user);
  }

  private void bindUserInfo(User user) {
    if (user.id != 0) {
      tvName.setText(user.username);
      tvPhone.setText(user.mobile);
      if (!TextUtils.isEmpty(user.header_img)) {
        String avatarPath;
        if (!user.header_img.contains("http")) {
          avatarPath = Api.IMAGE_ROOT_URL + user.header_img;
        } else {
          avatarPath = user.header_img;
        }
        Glide.with(mContext).load(avatarPath).into(ivAvatar);
      }
    } else {
      tvName.setText("");
      tvPhone.setText("");
      ivAvatar.setImageResource(R.drawable.img_default_avatar);
    }
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
                    }
                  });
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }
}
