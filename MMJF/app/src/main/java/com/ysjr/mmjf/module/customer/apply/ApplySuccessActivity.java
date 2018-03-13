package com.ysjr.mmjf.module.customer.apply;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.customer.adapter.MultiApplyAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-1.
 */

public class ApplySuccessActivity extends TopBarBaseActivity {
  public static final String KEY_MANAGER_PHONE = "key_phone";
  public static final String KEY_ID = "key_id";
  @BindView(R.id.tvManagerPhone) TextView tvManagerPhone;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.btnApply) Button btnApply;
  @BindView(R.id.tvTitle) TextView tvTitle;
  private MultiApplyAdapter mAdapter;
  private List<Manager> mManagerList = new ArrayList<>();
  private List<Manager> mResultList = new ArrayList<>();
  private String mobile;
  private int id;

  @Override protected int getContentView() {
    return R.layout.c_activity_apply_success;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.apply_success));
    setTopLeftButton(R.drawable.btn_black_back, new OnClickListener() {
      @Override public void onClick() {
        ActivityUtils.finishToActivity(ApplyLoanActivity.class, true);
      }
    });
    initRv();
    handleIntent();
    setPhoneText();
    initData();
  }

  @OnClick(R.id.btnComplete) public void onBtnCompleteClicked() {
    ActivityUtils.finishToActivity(MainActivity.class, false);
  }

  @OnClick(R.id.btnApply) public void onBtnApplyClicked() {
    if (mResultList.size() == 0) {
      ToastUtils.setBgColor(Color.BLACK);
      ToastUtils.showShort("至少选择一个贷款产品");
      return;
    }
    StringBuilder sb = new StringBuilder();
    for (Manager manager : mResultList) {
      sb.append(manager.id + ",");
    }
    sb.deleteCharAt(sb.length() - 1);
    OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_QUICK_APPLY).tag(this)
        .params("id", id)
        .params("ids", sb.toString())
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            ToastUtils.showShort("申请成功");
            ActivityUtils.finishToActivity(MainActivity.class, false);
          }
        });
  }

  private void handleIntent() {
    mobile = getIntent().getStringExtra(KEY_MANAGER_PHONE);
    id = getIntent().getIntExtra(KEY_ID, 0);
  }

  private void initData() {
    httpGetRecommendManager();
  }

  private void httpGetRecommendManager() {
    OkGo.<HttpResult<List<Manager>>>post(Api.APPLY_SUCCESS_RECOMMEND_MANAGER).tag(this)
        .params("id", id)
        .execute(new JsonCallback<HttpResult<List<Manager>>>() {
          @Override public void onSuccess(Response<HttpResult<List<Manager>>> response) {
            mManagerList.clear();
            mManagerList.addAll(response.body().data);
            mAdapter.notifyDataSetChanged();
          }

          @Override public void onError(Response<HttpResult<List<Manager>>> response) {
            super.onError(response);
            String errorStatus = null;
            if (response.getException() != null) {
              errorStatus = response.getException().getMessage();
            }
            if (HttpError.EMPTY.equals(errorStatus)) {
              btnApply.setVisibility(View.GONE);
              btnApply.setBackgroundColor(getResources().getColor(R.color.theme_color));
              btnApply.setTextColor(getResources().getColor(R.color.title_black_color));
              tvTitle.setVisibility(View.GONE);
            }
          }
        });
  }

  private void setPhoneText() {
    String text = "请注意接听来自" + mobile + "的电话";
    SpannableString spannableString = new SpannableString(text);
    ForegroundColorSpan span =
        new ForegroundColorSpan(getResources().getColor(R.color.text_33abff_color));
    spannableString.setSpan(span, 7, 7 + mobile.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvManagerPhone.setText(spannableString);
  }

  private void initRv() {
    mAdapter = new MultiApplyAdapter(R.layout.recommend_manager_item, mManagerList);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Manager manager = mManagerList.get(position);
        if (manager.isChecked) {
          mResultList.remove(manager);
        } else {
          mResultList.add(manager);
        }
        manager.isChecked = !manager.isChecked;
        mAdapter.notifyItemChanged(position);
      }
    });
  }
}
