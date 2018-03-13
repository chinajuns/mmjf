package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.COrder;
import com.ysjr.mmjf.entity.Order;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class OrderDetailAdapter extends BaseQuickAdapter<COrder.DataItem.ProcessingBean,BaseViewHolder> {
  public OrderDetailAdapter(int layoutResId, @Nullable List<COrder.DataItem.ProcessingBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, COrder.DataItem.ProcessingBean item) {
    if (helper.getAdapterPosition() == 0) {
      helper.setImageResource(R.id.ivDot, R.drawable.chx_circle_sel_layer)
          .setTextColor(R.id.tvDate, CommonUtils.getColor(R.color.title_black_color))
          .setTextColor(R.id.tvDescribe, CommonUtils.getColor(R.color.title_black_color));
    } else {
      helper.setImageResource(R.id.ivDot, R.drawable.dot_grey)
          .setTextColor(R.id.tvDate, CommonUtils.getColor(R.color.text_3_b3_color))
          .setTextColor(R.id.tvDescribe, CommonUtils.getColor(R.color.text_3_b3_color));
    }
    helper.setText(R.id.tvDescribe, item.describe)
        .setText(R.id.tvDate, SimpleDateUtils.getNoHours(item.create_time*1000));
  }
}
