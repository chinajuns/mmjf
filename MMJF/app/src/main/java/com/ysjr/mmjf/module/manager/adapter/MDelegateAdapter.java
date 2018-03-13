package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.ProBean;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MDelegateAdapter extends BaseQuickAdapter<ProBean,BaseViewHolder> {
  private boolean isDelegate;
  public MDelegateAdapter(int layoutResId, @Nullable List<ProBean> data,boolean isDelegate) {
    super(layoutResId, data);
    this.isDelegate = isDelegate;
  }

  @Override protected void convert(BaseViewHolder helper, ProBean item) {
    if (isDelegate) {
      helper.setBackgroundColor(R.id.btnOperator,
          mContext.getResources().getColor(R.color.theme_color));
      helper.setText(R.id.btnOperator, "我要代理");
    } else {
      helper.setBackgroundColor(R.id.btnOperator,
          mContext.getResources().getColor(R.color.bg_3_e6_color));
      helper.setText(R.id.btnOperator, "取消代理");
    }
    helper.addOnClickListener(R.id.btnOperator)
        .setText(R.id.tvLoanType, item.title)
        .setText(R.id.tvLoanNumber, item.loan_number)
        .setText(R.id.tvLoanLimit, item.time_limit)
        .setText(R.id.tvLoanRate, item.rate)
        .setText(R.id.tvApplyPeople, "已代理：" + item.apply_peoples + "人")
        .setText(R.id.tvTime, "发布时间：" + SimpleDateUtils.getNoHours(item.create_time*1000));
  }
}
