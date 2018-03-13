package com.ysjr.mmjf.module.manager.store;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.me.order.COrderEvaluateActivity;
import com.ysjr.mmjf.utils.Api;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MEditStoreActivity extends TopBarBaseActivity {
  @BindView(R.id.ivAvatar) ImageView ivAvatar;
  @BindView(R.id.tvName) TextView tvName;
  @BindView(R.id.tvCity) TextView tvCity;
  @BindView(R.id.tvCompany) TextView tvCompany;
  @BindView(R.id.etCareerTime) TextView etCareerTime;
  @BindView(R.id.etCusDesc) EditText etCusDesc;
  @BindView(R.id.tvNum) TextView tvNum;
  @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;

  @Override protected int getContentView() {
    return R.layout.m_activity_edit_store;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.edit_store));
    setListener();
    httpGetCusInfo();
  }

  private void setListener() {
    etCusDesc.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        String num = s.toString().trim().length() + "/50";
        tvNum.setText(num);
      }
    });
    nestedScrollView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            Log.e(COrderEvaluateActivity.class.getSimpleName(), "onGlobalLayout()");
            Rect rect = new Rect();
            nestedScrollView.getWindowVisibleDisplayFrame(rect);
            int keyBoardHeight = ScreenUtils.getScreenHeight() - rect.bottom;
            Log.e(COrderEvaluateActivity.class.getName(), "keyBoardHeight:" + keyBoardHeight);
            if (keyBoardHeight > 150 && etCusDesc.hasFocus()) {
              nestedScrollView.smoothScrollTo(0, nestedScrollView.getScrollY() + keyBoardHeight);
            }
          }
        });
  }

  @OnClick(R.id.ivAvatar) public void onIvAvatarClicked() {

  }

  @OnClick(R.id.layoutCity) public void onLayoutCityClicked() {
  }

  @OnClick(R.id.layoutCompany) public void onLayoutCompanyClicked() {
  }

  @OnClick(R.id.layoutCareerTime) public void onLayoutCareerTimeClicked() {
  }

  @OnClick(R.id.btnSave) public void onBtnSaveClicked() {
  }

  private void httpGetCusInfo() {
    OkGo.<JSONObject>post(Api.MOBILE_BUSINESS_SHOP_SHOW_CREATE).tag(mContext)
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              String header_img = response.body().getJSONObject("data").getString("header_img");
              String username = response.body().getJSONObject("data").getString("username");
              String service_city = response.body().getJSONObject("data").getString("service_city");
              String agent_name = response.body().getJSONObject("data").getString("agent_name");
              Glide.with(mContext)
                  .load(Api.IMAGE_ROOT_URL + header_img)
                  .placeholder(R.drawable.img_default_avatar)
                  .into(ivAvatar);
              tvName.setText(username);
              tvCity.setText(service_city);
              tvCompany.setText(agent_name);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }
}
