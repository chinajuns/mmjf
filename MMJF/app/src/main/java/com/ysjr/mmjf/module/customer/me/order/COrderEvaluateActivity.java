package com.ysjr.mmjf.module.customer.me.order;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
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
import com.ysjr.mmjf.entity.COrder;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.UserScoreType;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.ManagerTagAdapter;
import com.ysjr.mmjf.utils.Api;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-12-5.
 */

public class COrderEvaluateActivity extends TopBarBaseActivity {
  private static final float DEFAULT_SCORE = 5f;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.etContent) EditText etContent;
  @BindView(R.id.tvNum) TextView tvNum;
  @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;
  @BindView(R.id.ratingBar1) ScaleRatingBar ratingBar1;
  @BindView(R.id.ratingBar2) ScaleRatingBar ratingBar2;
  @BindView(R.id.ratingBar3) ScaleRatingBar ratingBar3;
  @BindView(R.id.btnCommit) Button btnCommit;
  private List<UserScoreType.TypeBean> mTagList = new ArrayList<>();
  private ManagerTagAdapter mTagAdapter;
  private float mScore1 = DEFAULT_SCORE;
  private float mScore2= DEFAULT_SCORE;
  private float mScore3 = DEFAULT_SCORE;
  private List<UserScoreType.TypeBean> mTypeList;
  private COrder.DataItem mDataItem;
  @Override protected int getContentView() {
    return R.layout.c_activity_order_evluate;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mDataItem =
        (COrder.DataItem) getIntent().getSerializableExtra(COrderDetailActivity.KEY_ORDER_DATA_ITEM);
    setTitle(getString(R.string.evaluate));
    initRv();
    setListener();
    initData();
  }

  private void initData() {
    OkGo.<HttpResult<UserScoreType>>get(Api.MOBILE_CLIENT_USER_SCORE_TYPE).tag(this)
        .execute(new JsonCallback<HttpResult<UserScoreType>>() {
          @Override public void onSuccess(Response<HttpResult<UserScoreType>> response) {
            mTypeList = response.body().data.type;
            mTagList.clear();
            mTagList.addAll(response.body().data.focus);
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
            if (keyBoardHeight > 150 && etContent.hasFocus()) {
              nestedScrollView.smoothScrollTo(0, nestedScrollView.getScrollY() + keyBoardHeight);
            }
          }
        });

    ratingBar1.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
      @Override public void onRatingChange(BaseRatingBar bar, float v) {
        mScore1 = v;
      }
    });
    ratingBar2.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
      @Override public void onRatingChange(BaseRatingBar bar, float v) {
        mScore2 = v;
      }
    });
    ratingBar3.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
      @Override public void onRatingChange(BaseRatingBar bar, float v) {
        mScore3 = v;
      }
    });
  }

  private void initRv() {
    mTagAdapter = new ManagerTagAdapter(R.layout.c_manager_tag_item, mTagList);
    recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
    recyclerView.setAdapter(mTagAdapter);
    recyclerView.setHasFixedSize(true);
    mTagAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        UserScoreType.TypeBean bean = mTagList.get(position);
        bean.isChecked = !bean.isChecked;
        boolean isSelectedTag = false;
        for (UserScoreType.TypeBean tag : mTagList) {
          if (tag.isChecked) {
            isSelectedTag = true;
          }
        }
        if (isSelectedTag) {
          btnCommit.setEnabled(true);
        } else {
          btnCommit.setEnabled(false);
        }
      }
    });
  }

  @OnClick(R.id.btnCommit) public void onViewClicked() {
    JSONObject scoreJson = new JSONObject();
    try {
      if (mTypeList != null) {
        for (UserScoreType.TypeBean type : mTypeList) {
          if (type.id == 16) {//描述相符
            scoreJson.put(type.id+"", mScore1);
          } else if (type.id == 14) {//服务态度
            scoreJson.put(type.id + "", mScore2);
          } else if (type.id == 15) {//专业程度
            scoreJson.put(type.id + "", mScore3);
          }
        }
      }
      StringBuilder focusSb = new StringBuilder();
      for (UserScoreType.TypeBean bean : mTagList) {
        if (bean.isChecked) {
          focusSb.append(bean.id + ",");
        }
      }
      focusSb.deleteCharAt(focusSb.length() - 1);
      OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_USER_ADD_SCORE)
          .tag(this)
          .params("id",mDataItem.id)
          .params("comment",etContent.getText().toString())
          .params("focus",focusSb.toString())
          .params("score",scoreJson.toString())
          .execute(new DialogCallback<HttpResult<Void>>(mContext) {
            @Override public void onSuccess(Response<HttpResult<Void>> response) {
              ToastUtils.setBgColor(Color.BLACK);
              ToastUtils.showShort("评价成功");
              finish();
            }
          });
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }
}
