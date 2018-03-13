package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.Customer;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-11.
 */

public class RubOrderAdapter extends BaseQuickAdapter<Customer,BaseViewHolder> {
  public RubOrderAdapter(int layoutResId, @Nullable List<Customer> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, Customer item) {
    helper.addOnClickListener(R.id.btnRub);
    helper.setText(R.id.tvCusName,item.customer)
        .setText(R.id.tvCreateTime, SimpleDateUtils.getNoHours(item.create_time*1000))
        .setText(R.id.tvApplyNumber,item.apply_number+"万元")
        .setText(R.id.tvLoanType,item.loan_type)
        .setText(R.id.tvPeriod,item.period)
    .setText(R.id.btnRub,item.score+"积分抢单")
    .setGone(R.id.ivVip,item.is_vip == 1)
    .setImageResource(R.id.ivVip,R.drawable.small_vip);
    for (int i = 0;i<item.info.size();i++) {
      Customer.InfoBean info = item.info.get(i);
      String phone = info.attr_key + ": " + info.attr_value;
      switch (i) {
        case 0:
          setInfo(helper, info, phone,R.id.tvInfo1);
          break;
        case 1:
          setInfo(helper, info, phone,R.id.tvInfo2);
          break;
        case 2:
          setInfo(helper, info, phone,R.id.tvInfo3);
          break;
        case 3:
          setInfo(helper, info, phone,R.id.tvInfo4);
          break;
        case 4:
          setInfo(helper, info, phone,R.id.tvInfo5);
          break;
        case 5:
          setInfo(helper, info, phone,R.id.tvInfo6);
          break;
        case 6:
          setInfo(helper, info, phone,R.id.tvInfo7);
          break;
        case 7:
          setInfo(helper, info, phone,R.id.tvInfo8);
          break;
      }
    }
  }

  private void setInfo(BaseViewHolder helper, Customer.InfoBean info, String phone,int id) {
    if (!TextUtils.isEmpty(info.attr_key)) {
      if (info.attr_key.contains("手机")) {
        SpannableString phoneSpan = new SpannableString(phone);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(
            mContext.getResources().getColor(R.color.text_33abff_color));
        phoneSpan.setSpan(colorSpan, phone.indexOf(":")+1, phone.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        helper.setText(id, phoneSpan);
      } else {
        helper.setText(id, phone);
      }
    }
  }
}
