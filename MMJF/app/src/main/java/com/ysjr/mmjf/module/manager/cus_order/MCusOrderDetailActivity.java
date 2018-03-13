package com.ysjr.mmjf.module.manager.cus_order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.ConfigBean;
import com.ysjr.mmjf.entity.Customer;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.Order;
import com.ysjr.mmjf.entity.Success;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.dialogfragment.SuccessDialogFragment;
import com.ysjr.mmjf.module.manager.adapter.MCusOrderDetailAdapter;
import com.ysjr.mmjf.module.manager.throw_order.MThrowOrderListActivity;
import com.ysjr.mmjf.module.popup.ComplaintPopup;
import com.ysjr.mmjf.module.popup.OrderPopup;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.HttpError;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.security.KeyRep;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MCusOrderDetailActivity extends TopBarBaseActivity {
  public static final String KEY_ORDER_ID = "order_id";
  public static final String KEY_REFER = "refer";
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  @BindView(R.id.btnFail) Button btnFail;
  @BindView(R.id.btnNext) Button btnNext;
  @BindView(R.id.btnThrowOrder) Button btnThrowOrder;
  @BindView(R.id.layoutBottom) LinearLayout layoutBottom;
  private List<Customer.ProcessHistory> mProcessList = new ArrayList<>();
  private MCusOrderDetailAdapter mAdapter;
  private int mOrderId;
  private String mRefer;//customer=>店铺客户申请的订单,junk=>个人中心抢的订单
  private TextView tvOrderNum;
  private TextView tvOrderTime;
  private TextView tvScore;
  private TextView tvCusName;
  private TextView tvTime;
  private ImageView ivVip;
  private TextView tvLoanNumber;
  private TextView tvLoanType;
  private TextView tvLoanLimit;
  private TextView tvAge;
  private TextView tvAddress;
  private TextView tvMobile;
  private TextView tvInfo1;
  private TextView tvInfo2;
  private TextView tvInfo3;
  private TextView tvInfo4;
  private TextView tvInfo0;
  private ImageView imgApply;
  private ImageView imgInfoCommit;
  private ImageView imgAuditInfo;
  private ImageView imgAuditLoan;
  private View viewLine1;
  private View viewLine2;
  private View viewLine3;
  private Step mStep = Step.ONE;

  private enum Step {
    ONE, TWO, THREE
  }

  @Override protected int getContentView() {
    return R.layout.m_activity_cus_order_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mOrderId = getIntent().getIntExtra(KEY_ORDER_ID, 0);
    mRefer = getIntent().getStringExtra(KEY_REFER);
    setTitle(getString(R.string.order_detail));
    setTopRightButton("", R.drawable.btn_dot_more, new OnClickListener() {
      @Override public void onClick() {
        OrderPopup popup = new OrderPopup(mContext);
        popup.setListener(new OrderPopup.OnItemClickListener() {
          @Override public void onClick(OrderPopup.ItemType type) {
            switch (type) {
              case COMPLAINT:
                showComplaintPopup();
                break;
              case CONTACT:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:10086"));
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                  // TODO: Consider calling
                  //    ActivityCompat#requestPermissions
                  // here to request the missing permissions, and then overriding
                  //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                  //                                          int[] grantResults)
                  // to handle the case where the user grants the permission. See the documentation
                  // for ActivityCompat#requestPermissions for more details.
                  return;
                }
                startActivity(intent);
                break;
            }
          }
        });
        popup.showPopupWindow(R.id.menu_1);
      }
    });
    EventBus.getDefault().register(this);
    initData();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  private void initData() {
    OkGo.<HttpResult<Customer>>post(Api.MOBILE_BUSINESS_SHOP_ORDER_DETAIL).tag(mContext)
        .params("id", mOrderId)
        .execute(new JsonCallback<HttpResult<Customer>>() {
          @Override public void onSuccess(Response<HttpResult<Customer>> response) {
            Customer bean = response.body().data;
            mProcessList.clear();
            mProcessList.addAll(bean.processHistory);
            Collections.reverse(mProcessList);
            View headView =
                LayoutInflater.from(mContext).inflate(R.layout.m_cus_order_detail_head, null);
            FrameLayout layoutLabel = headView.findViewById(R.id.layoutLabel);
            if ("customer".equals(mRefer)) {
              layoutLabel.setVisibility(View.GONE);
            }
            tvOrderNum = headView.findViewById(R.id.tvOrderNum);
            tvOrderTime = headView.findViewById(R.id.tvOrderTime);
            tvScore = headView.findViewById(R.id.tvScore);
            tvCusName = headView.findViewById(R.id.tvCusName);
            tvTime = headView.findViewById(R.id.tvTime);
            ivVip = headView.findViewById(R.id.ivVip);
            tvLoanNumber = headView.findViewById(R.id.tvLoanNumber);
            tvLoanType = headView.findViewById(R.id.tvLoanType);
            tvLoanLimit = headView.findViewById(R.id.tvLoanLimit);
            tvAge = headView.findViewById(R.id.tvAge);
            tvAddress = headView.findViewById(R.id.tvAddress);
            tvMobile = headView.findViewById(R.id.tvMobile);
            tvInfo0 = headView.findViewById(R.id.tvInfo0);
            tvInfo1 = headView.findViewById(R.id.tvInfo1);
            tvInfo2 = headView.findViewById(R.id.tvInfo2);
            tvInfo3 = headView.findViewById(R.id.tvInfo3);
            tvInfo4 = headView.findViewById(R.id.tvInfo4);
            imgApply = headView.findViewById(R.id.imgApply);
            imgInfoCommit = headView.findViewById(R.id.imgInfoCommit);
            imgAuditInfo = headView.findViewById(R.id.imgAuditInfo);
            imgAuditLoan = headView.findViewById(R.id.imgAuditLoan);
            viewLine1 = headView.findViewById(R.id.viewLine1);
            viewLine2 = headView.findViewById(R.id.viewLine2);
            viewLine3 = headView.findViewById(R.id.viewLine3);
            tvOrderNum.setText("订单编号：" + bean.order_num);
            tvOrderTime.setText("创建时间：" + SimpleDateUtils.getNoHours(bean.create_time * 1000));
            tvScore.setText(bean.price + "积分");
            tvCusName.setText(bean.customer);
            tvTime.setText(SimpleDateUtils.getNoHours(bean.create_time * 1000));
            ivVip.setVisibility(
                bean.is_vip == 0 ? View.GONE : View.VISIBLE);
            tvLoanNumber.setText(bean.apply_number + "万元");
            tvLoanType.setText(bean.loan_type);
            tvLoanLimit.setText(bean.period);
            tvAge.setText("年龄：" + bean.age);
            tvAddress.setText("现居：" + bean.current_place);
            tvMobile.setText(bean.mobile);
            List<Customer.InfoBean> infoBeans = bean.info;
            for (int i = 0; i < infoBeans.size(); i++) {
              Customer.InfoBean infoBean = infoBeans.get(i);
              switch (i) {
                case 0:
                  tvInfo0.setText(infoBean.attr_key + "：" + infoBean.attr_value);
                  break;
                case 1:
                  tvInfo1.setText(infoBean.attr_key + "：" + infoBean.attr_value);
                  break;
                case 2:
                  tvInfo2.setText(infoBean.attr_key + "：" + infoBean.attr_value);
                  break;
                case 3:
                  tvInfo3.setText(infoBean.attr_key + "：" + infoBean.attr_value);
                  break;
                case 4:
                  tvInfo4.setText(infoBean.attr_key + "：" + infoBean.attr_value);
                  break;
              }
            }
            List<Customer.ProcessHistory> history = bean.processHistory;
            if (history != null && history.size() > 0) {
              int processIndex = -1;
              int length = history.size();
              for (int i = 0; i < history.size(); i++) {
                int process = history.get(i).process;
                if (process == 38) {
                  processIndex = i;
                }
              }
              setProcess(processIndex, length);
            }
            mAdapter =
                new MCusOrderDetailAdapter(R.layout.m_cus_order_vertical_line_item, mProcessList);
            mAdapter.addHeaderView(headView);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
          }
        });
  }

  private void setProcess(int index, int length) {
    switch (index) {
      case -1://办理中或贷款成功
        switch (length) {
          case 1://到第一步
            imgApply.setImageResource(R.drawable.ic_order_apply_success);
            imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_default);
            imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_default);
            imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
            viewLine1.setBackgroundResource(R.color.btn_disabled_color);
            viewLine2.setBackgroundResource(R.color.btn_disabled_color);
            viewLine3.setBackgroundResource(R.color.btn_disabled_color);
            mStep = Step.ONE;
            layoutBottom.setVisibility(View.VISIBLE);
            break;
          case 2://到第二步
            imgApply.setImageResource(R.drawable.ic_order_apply_success);
            imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_success);
            imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_default);
            imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
            viewLine1.setBackgroundResource(R.color.theme_color);
            viewLine2.setBackgroundResource(R.color.btn_disabled_color);
            viewLine3.setBackgroundResource(R.color.btn_disabled_color);
            mStep = Step.TWO;
            layoutBottom.setVisibility(View.VISIBLE);
            btnThrowOrder.setVisibility(View.GONE);
            btnNext.setBackgroundResource(R.color.theme_color);
            break;
          case 3://到第三步
            imgApply.setImageResource(R.drawable.ic_order_apply_success);
            imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_success);
            imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_success);
            imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
            viewLine1.setBackgroundResource(R.color.theme_color);
            viewLine2.setBackgroundResource(R.color.theme_color);
            viewLine3.setBackgroundResource(R.color.btn_disabled_color);
            mStep = Step.THREE;
            layoutBottom.setVisibility(View.VISIBLE);
            btnThrowOrder.setVisibility(View.GONE);
            btnNext.setBackgroundResource(R.color.theme_color);
            break;
          case 4://贷款完成
            imgApply.setImageResource(R.drawable.ic_order_apply_success);
            imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_success);
            imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_success);
            imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_success);
            viewLine1.setBackgroundResource(R.color.theme_color);
            viewLine2.setBackgroundResource(R.color.theme_color);
            viewLine3.setBackgroundResource(R.color.theme_color);
            layoutBottom.setVisibility(View.GONE);
            break;
        }
        break;
      case 0://第一步失败
        imgApply.setImageResource(R.drawable.ic_order_apply_fail);
        imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_default);
        imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_default);
        imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
        viewLine1.setBackgroundResource(R.color.btn_disabled_color);
        viewLine2.setBackgroundResource(R.color.btn_disabled_color);
        viewLine3.setBackgroundResource(R.color.btn_disabled_color);
        layoutBottom.setVisibility(View.GONE);
        break;
      case 1://第二步失败
        imgApply.setImageResource(R.drawable.ic_order_apply_success);
        imgInfoCommit.setImageResource(R.drawable.ic_order_apply_fail);
        imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_default);
        imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
        viewLine1.setBackgroundResource(R.color.btn_disabled_color);
        viewLine2.setBackgroundResource(R.color.btn_disabled_color);
        viewLine3.setBackgroundResource(R.color.btn_disabled_color);
        layoutBottom.setVisibility(View.GONE);
        break;
      case 2://第三步失败
        imgApply.setImageResource(R.drawable.ic_order_apply_success);
        imgInfoCommit.setImageResource(R.drawable.ic_order_apply_success);
        imgAuditInfo.setImageResource(R.drawable.ic_order_apply_fail);
        imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
        viewLine1.setBackgroundResource(R.color.theme_color);
        viewLine2.setBackgroundResource(R.color.btn_disabled_color);
        viewLine3.setBackgroundResource(R.color.btn_disabled_color);
        layoutBottom.setVisibility(View.GONE);
        break;
      case 3://第四步失败
        imgApply.setImageResource(R.drawable.ic_order_apply_success);
        imgInfoCommit.setImageResource(R.drawable.ic_order_apply_success);
        imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_success);
        imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_fail);
        viewLine1.setBackgroundResource(R.color.theme_color);
        viewLine2.setBackgroundResource(R.color.theme_color);
        viewLine3.setBackgroundResource(R.color.btn_disabled_color);
        layoutBottom.setVisibility(View.GONE);
        break;
    }
  }

  private void showComplaintPopup() {
    ComplaintPopup popup = new ComplaintPopup(mContext);
    popup.showPopupWindow();
    popup.setListener(new ComplaintPopup.OnItemClickListener() {
      String reason = "";

      @Override public void onClick(ComplaintPopup.ItemType type) {
        switch (type) {
          case ONE:
            reason = getString(R.string.complaint_cause_1);
            break;
          case TWO:
            reason = getString(R.string.complaint_cause_2);
            break;
          case THREE:
            reason = getString(R.string.complaint_cause_3);
            break;
        }
        OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_SHOP_REPORT).tag(mContext)
            .params("id", mOrderId)
            .params("reason", reason)
            .execute(new DialogCallback<HttpResult<Void>>(mContext) {
              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                ToastUtils.setBgColor(Color.BLACK);
                ToastUtils.showShort(R.string.complaint_success);
              }

              @Override public void onError(Response<HttpResult<Void>> response) {
                super.onError(response);
                String errorStatus = null;
                if (response.getException() != null) {
                  errorStatus = response.getException().getMessage();
                }
                if (HttpError.REPETITION_ERROR.equals(errorStatus)) {
                  ToastUtils.showShort(R.string.complaint_repetition);
                }
              }
            });
      }
    });
  }

  @OnClick(R.id.btnFail) public void onBtnFailClicked() {
    MCusOrderDetailFailDialogFragment fg = MCusOrderDetailFailDialogFragment.newInstance();
    fg.show(getSupportFragmentManager(), "fail_dialog");
  }

  @OnClick(R.id.btnNext) public void onBtnNextClicked() {
    MCusOrderDetailNextDialogFragment fg = null;
    switch (mStep) {
      case ONE:
        fg = MCusOrderDetailNextDialogFragment.newInstance(
            MCusOrderDetailNextDialogFragment.TYPE_SIGN);
        break;
      case TWO:
        OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_SHOP_ORDER_PROCESS).tag(mContext)
            .params("id", mOrderId)
            .params("status", 2)
            .execute(new DialogCallback<HttpResult<Void>>(mContext) {
              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                initData();
              }
            });
        break;
      case THREE:
        fg = MCusOrderDetailNextDialogFragment.newInstance(
            MCusOrderDetailNextDialogFragment.TYPE_SEND);
        break;
    }
    if (fg != null) {
      fg.show(getSupportFragmentManager(), "next_dialog");
    }
  }

  @OnClick(R.id.btnThrowOrder) public void onBtnThrowOrderClicked() {
    MCusOrderDetailNextDialogFragment fg = MCusOrderDetailNextDialogFragment.newInstance(
        MCusOrderDetailNextDialogFragment.TYPE_THROW_ORDER);
    fg.show(getSupportFragmentManager(), "throw_dialog");
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onFailCommitClicked(ConfigBean.ValuesBean configBean) {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_SHOP_ORDER_PROCESS).tag(mContext)
        .params("id", mOrderId)
        .params("reason", configBean.name)
        .params("status", 4)
        .execute(new JsonCallback<HttpResult<Void>>() {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            initData();
          }
        });
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void onNextCommitClicked(Order order) {
    switch (order.type) {
      case MCusOrderDetailNextDialogFragment.TYPE_SIGN:
        OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_SHOP_ORDER_PROCESS).tag(mContext)
            .params("id", mOrderId)
            .params("status", 1)
            .params("money", order.money)
            .execute(new DialogCallback<HttpResult<Void>>(mContext) {
              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                initData();
              }
            });
        break;
      case MCusOrderDetailNextDialogFragment.TYPE_SEND:
        OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_SHOP_ORDER_PROCESS).tag(mContext)
            .params("id", mOrderId)
            .params("status", 3)
            .params("money", order.money)
            .execute(new DialogCallback<HttpResult<Void>>(mContext) {
              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                initData();
              }
            });
        break;
      case MCusOrderDetailNextDialogFragment.TYPE_THROW_ORDER:
        OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_SHOP_ORDER_JUNK).tag(mContext)
            .params("id", mOrderId)
            .params("score", order.money)
            .execute(new DialogCallback<HttpResult<Void>>(mContext) {
              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                ActivityUtils.startActivity(MThrowOrderListActivity.class);
                finish();
              }
            });
        break;
    }
  }
}
