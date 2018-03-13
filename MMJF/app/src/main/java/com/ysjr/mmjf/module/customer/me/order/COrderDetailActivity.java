package com.ysjr.mmjf.module.customer.me.order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.COrder;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.module.customer.adapter.OrderDetailAdapter;
import com.ysjr.mmjf.module.popup.ComplaintPopup;
import com.ysjr.mmjf.module.popup.OrderPopup;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.HttpError;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class COrderDetailActivity extends TopBarBaseActivity {
  public static final String KEY_ORDER_DATA_ITEM = "order_data_mDataItem";
  @BindView(R.id.tvLoanAccount) TextView tvLoanAccount;
  @BindView(R.id.tvLoanTime) TextView tvLoanTime;
  @BindView(R.id.imgCard) ImageView imgCard;
  @BindView(R.id.tvLoanType) TextView tvLoanType;
  @BindView(R.id.tvName) TextView tvName;
  @BindView(R.id.ivVerify) ImageView ivVerify;
  @BindView(R.id.tvLoanState) TextView tvLoanState;
  @BindView(R.id.tvMaxLoan) TextView tvMaxLoan;
  @BindView(R.id.tvLoanNumber) TextView tvLoanNumber;
  @BindView(R.id.tv30Days) TextView tv30Days;
  @BindView(R.id.tvLoanDay) TextView tvLoanDay;
  @BindView(R.id.ratingBar) ScaleRatingBar ratingBar;
  @BindView(R.id.tvScore) TextView tvScore;
  @BindView(R.id.tvFocus) TextView tvFocus;
  @BindView(R.id.tvHot) TextView tvHot;
  @BindView(R.id.imgApply) ImageView imgApply;
  @BindView(R.id.viewLine1) View viewLine1;
  @BindView(R.id.imgInfoCommit) ImageView imgInfoCommit;
  @BindView(R.id.viewLine2) View viewLine2;
  @BindView(R.id.imgAuditInfo) ImageView imgAuditInfo;
  @BindView(R.id.viewLine3) View viewLine3;
  @BindView(R.id.imgAuditLoan) ImageView imgAuditLoan;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  private List<COrder.DataItem.ProcessingBean> mOrderList = new ArrayList<>();
  private OrderDetailAdapter mAdapter;
  private COrder.DataItem mDataItem;

  @Override protected int getContentView() {
    return R.layout.c_activity_order_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mDataItem = (COrder.DataItem) getIntent().getSerializableExtra(KEY_ORDER_DATA_ITEM);
    setTitle(getString(R.string.order_detail));
    setTopRightButton("", R.drawable.btn_dot_more, new OnClickListener() {
      @Override public void onClick() {
        showPopup();
      }
    });
    initView();
    initRv();
  }

  private void initView() {
    tvLoanAccount.setText("订单编号："+mDataItem.loan_account);
    tvLoanTime.setText("创建时间："+SimpleDateUtils.getNoHours(mDataItem.loaner.create_time*1000));
    Manager manager = mDataItem.loaner;
    bindManagerInfo(manager);
    //  显示隐藏
    if (mDataItem.process == 37 || mDataItem.process == 38) {//成功或者失败，可以评价
      tvLoanState.setVisibility(View.VISIBLE);
      tvLoanState.setText(mDataItem.process == 37 ? "贷款成功" : "贷款失败");
      tvLoanState.setTextColor(
          mDataItem.process == 37 ? mContext.getResources().getColor(R.color.text_ff3333_color)
              : mContext.getResources().getColor(R.color.text_33abff_color));
    }
    List<COrder.DataItem.ProcessingBean> processingBeanList = mDataItem.processing;
    setProcess(processingBeanList);
    Collections.reverse(mDataItem.processing);
    mOrderList.addAll(mDataItem.processing);
  }

  private void setProcess(List<COrder.DataItem.ProcessingBean> processList) {
    if (processList != null && processList.size() > 0) {
      int processIndex = -1;
      int length = processList.size();
      for (int i = 0; i < processList.size(); i++) {
        COrder.DataItem.ProcessingBean process = processList.get(i);
        if (process.id == 38) {
          processIndex = i;
        }
      }
      switch (processIndex) {
        case -1://办理中或贷款成功
          switch (length) {
            case 1://到第一步
              imgApply.setImageResource(R.drawable.ic_order_apply_success);
              imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_default);
              imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_default);
              imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
              viewLine1.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
              viewLine2.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
              viewLine3.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
              break;
            case 2://到第二步
              imgApply.setImageResource(R.drawable.ic_order_apply_success);
              imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_success);
              imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_default);
              imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
              viewLine1.setBackgroundColor(getResources().getColor(R.color.theme_color));
              viewLine2.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
              viewLine3.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
              break;
            case 3://到第三步
              imgApply.setImageResource(R.drawable.ic_order_apply_success);
              imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_success);
              imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_success);
              imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
              viewLine1.setBackgroundColor(getResources().getColor(R.color.theme_color));
              viewLine2.setBackgroundColor(getResources().getColor(R.color.theme_color));
              viewLine3.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
              break;
            case 4://贷款完成
              imgApply.setImageResource(R.drawable.ic_order_apply_success);
              imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_success);
              imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_success);
              imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_success);
              viewLine1.setBackgroundColor(getResources().getColor(R.color.theme_color));
              viewLine2.setBackgroundColor(getResources().getColor(R.color.theme_color));
              viewLine3.setBackgroundColor(getResources().getColor(R.color.theme_color));
              break;
          }
          break;
        case 0://第一步失败
          imgApply.setImageResource(R.drawable.ic_order_apply_fail);
          imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_default);
          imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_default);
          imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
          viewLine1.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
          viewLine2.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
          viewLine3.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
          break;
        case 1://第二步失败
          imgApply.setImageResource(R.drawable.ic_order_apply_success);
          imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_fail);
          imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_default);
          imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
          viewLine1.setBackgroundColor(getResources().getColor(R.color.theme_color));
          viewLine2.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
          viewLine3.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
          break;
        case 2://第三步失败
          imgApply.setImageResource(R.drawable.ic_order_apply_success);
          imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_success);
          imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_fail);
          imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_default);
          viewLine1.setBackgroundColor(getResources().getColor(R.color.theme_color));
          viewLine2.setBackgroundColor(getResources().getColor(R.color.theme_color));
          viewLine3.setBackgroundColor(getResources().getColor(R.color.btn_disabled_color));
          break;
        case 3://第四步失败
          imgApply.setImageResource(R.drawable.ic_order_apply_success);
          imgInfoCommit.setImageResource(R.drawable.ic_order_info_commit_success);
          imgAuditInfo.setImageResource(R.drawable.ic_order_audit_info_success);
          imgAuditLoan.setImageResource(R.drawable.ic_order_audit_loan_fail);
          viewLine1.setBackgroundColor(getResources().getColor(R.color.theme_color));
          viewLine2.setBackgroundColor(getResources().getColor(R.color.theme_color));
          viewLine3.setBackgroundColor(getResources().getColor(R.color.theme_color));
          break;
      }
    }
  }

  private void bindManagerInfo(Manager manager) {
    Glide.with(mContext).load(manager.header_img).placeholder(R.drawable.touxiang).into(imgCard);
    tvLoanType.setText(manager.tag);
    tvName.setText(manager.name);
    //需要格式化金额
    tvLoanNumber.setText("" + manager.all_number);
    tvMaxLoan.setText("" + manager.max_loan);
    tv30Days.setText("" + manager.loan_number);
    if (manager.is_auth == 3) {
      ivVerify.setImageResource(R.drawable.ic_verify);
    } else {
      ivVerify.setImageResource(R.drawable.ic_unverify);
    }
    tvLoanDay.setText(manager.loan_day + "天");
    ratingBar.setRating(manager.score);
    tvScore.setText("" + manager.score);
    if (!TextUtils.isEmpty(manager.tags)) {
      if (manager.tags.contains(",")) {
        String[] split = manager.tags.split(",");
        tvFocus.setText(split[0]);
        tvHot.setText(split[1]);
        tvFocus.setVisibility(View.VISIBLE);
        tvHot.setVisibility(View.VISIBLE);
      } else {
        tvFocus.setText(manager.tags);
        tvFocus.setVisibility(View.VISIBLE);
      }
    }
  }

  private void showPopup() {
    OrderPopup popup = new OrderPopup(mContext);
    popup.setListener(new OrderPopup.OnItemClickListener() {
      @Override public void onClick(OrderPopup.ItemType type) {
        switch (type) {
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
          case COMPLAINT:
            showComplaintPopup();
            break;
        }
      }
    });
    popup.showPopupWindow(R.id.menu_1);
  }

  private void showComplaintPopup() {
    ComplaintPopup popup = new ComplaintPopup(mContext);
    popup.showPopupWindow();
    popup.setListener(new ComplaintPopup.OnItemClickListener() {
      @Override public void onClick(ComplaintPopup.ItemType type) {
        String content = "";
        switch (type) {
          case ONE:
            content = getString(R.string.complaint_cause_1);
            break;
          case TWO:
            content = getString(R.string.complaint_cause_2);
            break;
          case THREE:
            content = getString(R.string.complaint_cause_3);
            break;
        }
        OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_REPORT)
            .tag(this)
            .params("to_uid",mDataItem.loaner_id)
            .params("loan_id",mDataItem.id)
            .params("comment",content)
            .execute(new DialogCallback<HttpResult<Void>>(mContext) {
              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                ToastUtils.setBgColor(Color.BLACK);
                ToastUtils.showShort("举报成功");
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

  private void initRv() {
    mAdapter = new OrderDetailAdapter(R.layout.c_order_vertical_line_item, mOrderList);
    recyclerView.setAdapter(mAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
  }

}
