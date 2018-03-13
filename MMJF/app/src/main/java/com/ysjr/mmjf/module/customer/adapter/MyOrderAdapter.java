package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.COrder;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.utils.Api;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class MyOrderAdapter extends BaseQuickAdapter<COrder.DataItem,BaseViewHolder> {
  public MyOrderAdapter(int layoutResId, @Nullable List<COrder.DataItem> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, COrder.DataItem item) {
    Manager manager = item.loaner;
    Glide.with(mContext)
        .load(Api.IMAGE_ROOT_URL + manager.header_img)
        .placeholder(R.drawable.touxiang)
        .into((ImageView) helper.getView(R.id.imgCard));
    helper.setText(R.id.tvName, manager.name)
        .setText(R.id.tvLoanType,manager.tag)
        .setText(R.id.tvLoanNumber,manager.all_number+"万元")
        .setText(R.id.tvMaxLoan,manager.max_loan+"万元")
        .setText(R.id.tv30Days,manager.loan_number+"万元")
        .setText(R.id.tvLoanDay,manager.loan_day+"天")
        .setText(R.id.tvScore,manager.score+"")
        .setImageResource(R.id.ivVerify,manager.is_auth == 3?R.drawable.ic_verify:R.drawable.ic_unverify);
    ScaleRatingBar ratingBar = helper.getView(R.id.ratingBar);
    ratingBar.setRating(manager.score);
    if (!TextUtils.isEmpty(manager.tags)) {
      if (manager.tags.contains(",")) {
        String[] split = manager.tags.split(",");
        helper.setText(R.id.tvFocus, split[0])
            .setText(R.id.tvHot, split[1])
            .setVisible(R.id.tvFocus, true)
            .setVisible(R.id.tvHot, true);
      } else {
        helper.setText(R.id.tvFocus, manager.tags)
            .setVisible(R.id.tvFocus, true)
            .setVisible(R.id.tvHot, false);
      }
    } else {
      helper.setVisible(R.id.tvFocus, false).setVisible(R.id.tvHot, false);
    }
    List<COrder.DataItem.ProcessingBean> processList = item.processing;
    if (processList != null && processList.size() > 0) {
      int processIndex = -1;
      int length = processList.size();
      for (int i =0;i<processList.size();i++) {
        COrder.DataItem.ProcessingBean process = processList.get(i);
        if (process.id == 38) {
          processIndex = i;
        }
      }
      setProcess(helper,item,processIndex,length);
    } else {
      resetAllProcess(helper,item);
    }

  //  显示隐藏
    if (item.process == 37 || item.process == 38) {//成功或者失败，可以评价
      helper.setVisible(R.id.tvLoanState, true)
          .setText(R.id.tvLoanState, item.process == 37 ? "贷款成功" : "贷款失败")
          .setTextColor(R.id.tvLoanState,
              item.process == 37 ? mContext.getResources().getColor(R.color.text_ff3333_color)
                  : mContext.getResources().getColor(R.color.text_33abff_color));
      if (item.is_comment == 2) {//已评价
        helper.setGone(R.id.btnEvaluate, false);
      } else {//未评价
        helper.setGone(R.id.btnEvaluate, true)
        .addOnClickListener(R.id.btnEvaluate);
      }
    } else {//办理中
      helper.setVisible(R.id.tvLoanState, false);
      helper.setGone(R.id.btnEvaluate, false);
    }


  }

  private void setProcess(BaseViewHolder helper, COrder.DataItem item, int index, int length) {
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
        helper.setBackgroundRes(R.id.viewLine1, R.color.btn_disabled_color);
        helper.setBackgroundRes(R.id.viewLine2, R.color.btn_disabled_color);
        helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
        break;
      case 2://第三步失败
        helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
        helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_apply_success);
        helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_apply_fail);
        helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
        helper.setBackgroundRes(R.id.viewLine1, R.color.theme_color);
        helper.setBackgroundRes(R.id.viewLine2, R.color.btn_disabled_color);
        helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
        break;
      case 3://第四步失败
        helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_success);
        helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_success);
        helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_success);
        helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_fail);
        helper.setBackgroundRes(R.id.viewLine1, R.color.theme_color);
        helper.setBackgroundRes(R.id.viewLine2, R.color.theme_color);
        helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
        break;
    }
  }

  private void resetAllProcess(BaseViewHolder helper, COrder.DataItem item) {
    helper.setImageResource(R.id.imgApply,R.drawable.ic_order_apply_default);
    helper.setImageResource(R.id.imgInfoCommit, R.drawable.ic_order_info_commit_default);
    helper.setImageResource(R.id.imgAuditInfo, R.drawable.ic_order_audit_info_default);
    helper.setImageResource(R.id.imgAuditLoan, R.drawable.ic_order_audit_loan_default);
    helper.setBackgroundRes(R.id.viewLine1, R.color.btn_disabled_color);
    helper.setBackgroundRes(R.id.viewLine2, R.color.btn_disabled_color);
    helper.setBackgroundRes(R.id.viewLine3, R.color.btn_disabled_color);
  }
}
