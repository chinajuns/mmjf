package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.MThrowOrderBean;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2017-12-11.
 */

public class ThrowOrderAdapter extends BaseQuickAdapter<MThrowOrderBean,BaseViewHolder> {
  public ThrowOrderAdapter(int layoutResId, @Nullable List<MThrowOrderBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, MThrowOrderBean item) {
    if (item.order_status == 4) {
      helper.setGone(R.id.btnThrowOrderAgain, true);
      helper.addOnClickListener(R.id.btnThrowOrderAgain);
    } else {
      helper.setGone(R.id.btnThrowOrderAgain, false);
    }
      helper.setText(R.id.tvPrice, item.score + "积分")
          .setText(R.id.tvCusName, item.customer)
          .setText(R.id.tvTime, SimpleDateUtils.getNoHours(item.create_time*1000))
          .setText(R.id.tvState, item.label)
          .setText(R.id.tvLoanNumber, item.apply_number + "万元")
          .setText(R.id.tvLoanType, item.loan_type)
          .setText(R.id.tvLoanLimit, item.period)
          .setText(R.id.tvAge, "年龄：" + item.age)
          .setText(R.id.tvCity, "现居：" + item.current_place)
          .setText(R.id.tvMobile, item.mobile)
          .setTextColor(R.id.tvState,getTextColor(item.order_status));
    List<MThrowOrderBean.InfoBean> infos = item.info;
    if (infos != null) {
      for (int i = 0; i < infos.size(); i++) {
        switch (i) {
          case 0:
            helper.setText(R.id.tvInfo0key, item.info.get(0).attr_key + "：");
            helper.setText(R.id.tvInfo0Value, item.info.get(0).attr_value);
            break;
          case 1:
            helper.setText(R.id.tvInfo1key, item.info.get(1).attr_key + "：");
            helper.setText(R.id.tvInfo1Value, item.info.get(1).attr_value);
            break;
          case 2:
            helper.setText(R.id.tvInfo2key, item.info.get(2).attr_key + "：");
            helper.setText(R.id.tvInfo2Value, item.info.get(2).attr_value);
            break;
          case 3:
            helper.setText(R.id.tvInfo3key, item.info.get(3).attr_key + "：");
            helper.setText(R.id.tvInfo3Value, item.info.get(3).attr_value);
            break;
          case 4:
            helper.setText(R.id.tvInfo4key, item.info.get(4).attr_key + "：");
            helper.setText(R.id.tvInfo4Value, item.info.get(4).attr_value);
            break;
        }
      }
    }
  }

  private int getTextColor(int status) {
    switch (status) {
      case 1:
      case 2:
        return mContext.getResources().getColor(R.color.theme_color);
      case 3:
        return mContext.getResources().getColor(R.color.text_ff3333_color);
      case 4:
        return mContext.getResources().getColor(R.color.btn_disabled_color);
    }
    return mContext.getResources().getColor(R.color.title_black_color);
  }
}
