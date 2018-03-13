package com.ysjr.mmjf.module.manager.cus_order;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.UserScoreType;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.ManagerTagAdapter;
import com.ysjr.mmjf.module.customer.me.order.COrderEvaluateActivity;
import com.ysjr.mmjf.module.manager.adapter.CustomerTagAdapter;
import com.ysjr.mmjf.utils.Api;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MEvaluateActivity extends TopBarBaseActivity {
  @BindView(R.id.ratingBar) ScaleRatingBar ratingBar;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.etContent) EditText etContent;
  @BindView(R.id.tvNum) TextView tvNum;
  @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;
  @BindView(R.id.btnCommit) Button btnCommit;
  private List<UserScoreType.TypeBean> mTagList = new ArrayList<>();
  private CustomerTagAdapter mTagAdapter;
  private float score =0f;
  private int mOrderId;

  @Override protected int getContentView() {
    return R.layout.m_activity_evaluate;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mOrderId = getIntent().getIntExtra(MCusOrderDetailActivity.KEY_ORDER_ID, 0);
    setTitle(getString(R.string.evaluate));
    initRv();
    setListener();
    initData();
  }

  private void initData() {
    OkGo.<HttpResult<List<UserScoreType.TypeBean>>>post(Api.MOBILE_BUSINESS_SHOP_RORDER_COMMENT_LABEL)
        .tag(mContext)
        .execute(new JsonCallback<HttpResult<List<UserScoreType.TypeBean>>>() {
          @Override
          public void onSuccess(Response<HttpResult<List<UserScoreType.TypeBean>>> response) {
            mTagList .addAll(response.body().data);
            mTagAdapter.notifyDataSetChanged();
          }
        });
  }

  private void setListener() {
    etContent.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        int num = s.toString().trim().length();
        tvNum.setText(num + "/200");
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
            if (keyBoardHeight > 150) {
              nestedScrollView.smoothScrollTo(0, nestedScrollView.getScrollY() + keyBoardHeight);
            }
          }
        });
    ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
      @Override public void onRatingChange(BaseRatingBar bar, float v) {
        score = v;
      }
    });
  }

  private void initRv() {
    mTagAdapter = new CustomerTagAdapter(R.layout.c_manager_tag_item, mTagList);
    recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
    recyclerView.setAdapter(mTagAdapter);
    recyclerView.setHasFixedSize(true);
    mTagAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        UserScoreType.TypeBean bean = mTagList.get(position);
        bean.isChecked = !bean.isChecked;
        mTagAdapter.notifyDataSetChanged();
        boolean isSelectTag = false;
        for (UserScoreType.TypeBean tag : mTagList) {
          if (tag.isChecked) {
            isSelectTag = true;
          }
        }
        if (isSelectTag) {
          btnCommit.setEnabled(true);
        } else {
          btnCommit.setEnabled(false);
        }
      }
    });
  }

  @OnClick(R.id.btnCommit) public void onViewClicked() {
    StringBuilder labelSb = new StringBuilder();
    for (UserScoreType.TypeBean tag : mTagList) {
      if (tag.isChecked) {
        labelSb.append(tag.id + ",");
      }
    }
    labelSb.deleteCharAt(labelSb.length() - 1);
    OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_SHOP_RORDER_COMMENT)
        .tag(mContext)
        .params("id",mOrderId)
        .params("score",score)
        .params("label_ids",labelSb.toString())
        .params("describe",etContent.getText().toString())
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            ToastUtils.setBgColor(Color.BLACK);
            ToastUtils.showShort("评价成功");
            finish();
          }
        });
  }

}
