package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.COrder;
import com.ysjr.mmjf.entity.Customer;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MCusOrderAdapter extends BaseQuickAdapter<Customer,BaseViewHolder> {
  private String mRefer;//customer=>店铺客户申请的订单,junk=>个人中心抢的订单
  public MCusOrderAdapter(int layoutResId, @Nullable List<Customer> data,String refer) {
    super(layoutResId, data);
    mRefer = refer;
  }

  @Override protected void convert(BaseViewHolder helper, Customer item) {
      if ("customer".equals(mRefer)) {
        helper.setGone(R.id.layoutLabel, false);
      } else {
        helper.setGone(R.id.layoutLabel, true);
      }
      helper.setText(R.id.tvScore, item.price+"积分")
          .setText(R.id.tvCusName,item.customer)
          .setText(R.id.tvTime, SimpleDateUtils.getNoHours(item.create_time*1000))
          .setText(R.id.tvLoanNumber,item.apply_number+"万元")
          .setText(R.id.tvLoanType,item.loan_type)
          .setText(R.id.tvLoanLimit,item.period)
          .setText(R.id.tvAge, "年龄：" + item.age)
          .setText(R.id.tvAddress, "现居：" + item.current_place)
          .setText(R.id.tvMobile, item.mobile)
          .setGone(R.id.ivVip,item.is_vip == 1);
      if (item.info != null) {
        for (int i = 0;i<item.info.size();i++) {
          Customer.InfoBean info = item.info.get(i);
          switch (i) {
            case 0:
              helper.setText(R.id.tvInfo0, info.attr_key + "：" + info.attr_value);
              break;
            case 1:
              helper.setText(R.id.tvInfo1, info.attr_key + "：" + info.attr_value);
              break;
            case 2:
              helper.setText(R.id.tvInfo2, info.attr_key + "：" + info.attr_value);
              break;
            case 3:
              helper.setText(R.id.tvInfo3, info.attr_key + "：" + info.attr_value);
              break;
            case 4:
              helper.setText(R.id.tvInfo4, info.attr_key + "：" + info.attr_value);
              break;
          }
        }
      }
    int[] processList = item.processIds;
    if (processList != null && processList.length > 0) {
      int processIndex = -1;
      int length = processList.length;
      for (int i =0;i<processList.length;i++) {
        int process = processList[i];
        if (process == 38) {
          processIndex = i;
        }
      }
      setProcess(helper,processIndex,length);
    } else {
      resetAllProcess(helper);
    }

    //  显示隐藏
    if (item.process == 37 || item.process == 38) {//成功或者失败，可以评价
      helper.setGone(R.id.tvOrderState, true)
          .setText(R.id.tvOrderState, item.process == 37 ? "订单成功" : "订单失败")
          .setTextColor(R.id.tvOrderState,
              item.process == 37 ? mContext.getResources().getColor(R.color.text_ff3333_color)
                  : mContext.getResources().getColor(R.color.text_33abff_color));
      if (item.is_comment == 2) {//已评价
        helper.setGone(R.id.btnEvaluate, false);
      } else {//未评价
        helper.setGone(R.id.btnEvaluate, true)
            .addOnClickListener(R.id.btnEvaluate);
      }
    } else {//办理中
      helper.setGone(R.id.tvOrderState, false);
      helper.setGone(R.id.btnEvaluate, false);
    }

  }
  private void setProcess(BaseViewHolder helper, int index, int length) {
    switch (index) {
      case -1://办理中或贷款成功
        switch (length) {
          case 1://到第一步
            helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
            helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_default);
            helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_default);
            helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
            helper.setBackgroundRes(R.id.viewLine1, R.color.btn_disabled_color);
            helper.setBackgroundRes(R.id.viewLine2, R.color.btn_disabled_color);
            helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
            break;
          case 2://到第二步
            helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
            helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_success);
            helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_default);
            helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
            helper.setBackgroundRes(R.id.viewLine1, R.color.theme_color);
            helper.setBackgroundRes(R.id.viewLine2, R.color.btn_disabled_color);
            helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
            break;
          case 3://到第三步
            helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
            helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_success);
            helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_success);
            helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
            helper.setBackgroundRes(R.id.viewLine1, R.color.theme_color);
            helper.setBackgroundRes(R.id.viewLine2, R.color.theme_color);
            helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
            break;
          case 4://贷款完成
            helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
            helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_success);
            helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_success);
            helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_success);
            helper.setBackgroundRes(R.id.viewLine1, R.color.theme_color);
            helper.setBackgroundRes(R.id.viewLine2, R.color.theme_color);
            helper.setBackgroundRes(R.id.viewLine3, R.color.theme_color);
            break;
        }
        break;
      case 0://第一步失败
        helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_fail);
        helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_default);
        helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_default);
        helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
        helper.setBackgroundRes(R.id.viewLine1, R.color.btn_disabled_color);
        helper.setBackgroundRes(R.id.viewLine2, R.color.btn_disabled_color);
        helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
        break;
      case 1://第二步失败
        helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
        helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_apply_fail);
        helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_default);
        helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
        helper.setBackgroundRes(R.id.viewLine1, R.color.theme_color);
        helper.setBackgroundRes(R.id.viewLine2, R.color.btn_disabled_color);
        helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
        break;
      case 2://第三步失败
        helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
        helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_apply_success);
        helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_apply_fail);
        helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
        helper.setBackgroundRes(R.id.viewLine1, R.color.theme_color);
        helper.setBackgroundRes(R.id.viewLine2, R.color.theme_color);
        helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
        break;
      case 3://第四步失败
        helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
        helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_success);
        helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_success);
        helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_fail);
        helper.setBackgroundRes(R.id.viewLine1, R.color.theme_color);
        helper.setBackgroundRes(R.id.viewLine2, R.color.theme_color);
        helper.setBackgroundRes(R.id.viewLine3, R.color.theme_color);
        break;
    }
  }

  private void resetAllProcess(BaseViewHolder helper) {
    helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_default);
    helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_default);
    helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_default);
    helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
    helper.setBackgroundRes(R.id.viewLine1, R.color.btn_disabled_color);
    helper.setBackgroundRes(R.id.viewLine2, R.color.btn_disabled_color);
    helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
  }
}
