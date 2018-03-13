package com.ysjr.mmjf.module.customer.me.set;

import android.os.Bundle;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.widget.SwitchButton;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CSettingActivity extends TopBarBaseActivity {
  @BindView(R.id.switchButton) SwitchButton switchButton;

  @Override protected int getContentView() {
    return R.layout.c_activity_setting;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.setting));
    switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        if (isChecked) {
          httpSetPushStatus("on");
        } else {
          httpSetPushStatus("off");
        }
      }
    });
    httpCheckPushStatus();
  }

  private void httpSetPushStatus(String action) {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_MEMBER_SET_PUSH_STATUS)
        .tag(mContext)
        .params("action",action)
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            httpCheckPushStatus();
          }
        });
  }

  private void httpCheckPushStatus() {
    OkGo.<JSONObject>get(Api.MOBILE_CLIENT_MEMBER_GET_PUSH_STATUS)
        .tag(mContext)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              int status = response.body().getJSONObject("data").getInt("push_status");
              if (status == 1) {
                switchButton.setChecked(true);
              } else {
                switchButton.setChecked(false);
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

  @OnClick(R.id.layoutFeedback) public void onLayoutFeedbackClicked() {
    ActivityUtils.startActivity(CFeedbackActivity.class);
  }

  @OnClick(R.id.layoutModifyPsw) public void onLayoutModifyPswClicked() {
    ActivityUtils.startActivity(CModifyPswActivity.class);
  }

  @OnClick(R.id.layoutAboutUs) public void onLayoutAboutUsClicked() {
    ActivityUtils.startActivity(CAboutUsActivity.class);
  }

  @OnClick(R.id.layoutExitLogin) public void onLayoutExitLoginClicked() {
    OkGo.<HttpResult<Void>>get(Api.LOGOUT)
        .tag(mContext)
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            DataSupport.deleteAll(User.class);
            ActivityUtils.finishAllActivities();
            ActivityUtils.startActivity(NavigationActivity.class);
          }
        });
  }
}
