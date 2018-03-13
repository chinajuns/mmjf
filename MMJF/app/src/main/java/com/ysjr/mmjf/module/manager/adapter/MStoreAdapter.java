package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.MThrowOrderBean;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MStoreAdapter extends BaseQuickAdapter<MThrowOrderBean, BaseViewHolder> {
  public MStoreAdapter(int layoutResId, @Nullable List<MThrowOrderBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, MThrowOrderBean item) {
    if (item.isEmpty) {
      helper.setVisible(R.id.layoutEmpty, true);
      helper.setVisible(R.id.layoutContent, false);
    } else {
      helper.setVisible(R.id.layoutEmpty, false);
      helper.setVisible(R.id.layoutContent, true);
      helper.setText(R.id.tvScore, item.score + "积分")
          .setText(R.id.tvCusName, item.customer)
          .setText(R.id.tvTime, SimpleDateUtils.getNoHours(item.create_time*1000))
          .setImageResource(R.id.ivVip,
              item.is_vip == 0 ? R.drawable.m_ic_common_vip : R.drawable.m_ic_vip)
          .setText(R.id.tvLoanNumber, item.apply_number + "万元")
          .setText(R.id.tvLoanType, item.loan_type)
          .setText(R.id.tvLoanLimit, item.period)
          .setText(R.id.tvAge, "年龄："+item.age)
          .setText(R.id.tvAddress, "现居："+item.current_place)
          .setText(R.id.tvMobile, item.mobile);
      if (item.info != null && item.info.size() > 0) {
        for (int i = 0; i < item.info.size(); i++) {
          switch (i) {
            case 0:
              helper.setText(R.id.tvInfo0,
                  item.info.get(0).attr_key + "：" + item.info.get(0).attr_value);
              break;
            case 1:
              helper.setText(R.id.tvInfo1,
                  item.info.get(1).attr_key + "：" + item.info.get(1).attr_value);
              break;
            case 2:
              helper.setText(R.id.tvInfo2,
                  item.info.get(2).attr_key + "：" + item.info.get(2).attr_value);
              break;
            case 3:
              helper.setText(R.id.tvInfo3,
                  item.info.get(3).attr_key + "：" + item.info.get(3).attr_value);
              break;
            case 4:
              helper.setText(R.id.tvInfo4,
                  item.info.get(4).attr_key + "：" + item.info.get(4).attr_value);
              break;
          }
        }
      }
    }
  }
}
