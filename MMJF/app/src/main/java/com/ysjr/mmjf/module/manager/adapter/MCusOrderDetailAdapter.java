package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import android.util.Log;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.Customer;
import com.ysjr.mmjf.entity.Order;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MCusOrderDetailAdapter extends BaseQuickAdapter<Customer.ProcessHistory,BaseViewHolder> {
  public MCusOrderDetailAdapter(int layoutResId, @Nullable List<Customer.ProcessHistory> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, Customer.ProcessHistory item) {
    Log.e("Position", helper.getAdapterPosition() + "");
    if (helper.getAdapterPosition() == 1) {
      helper.setImageResource(R.id.ivState, R.drawable.chx_circle_sel_layer);
    } else {
      helper.setImageResource(R.id.ivState, R.drawable.dot_grey);
    }
    helper.setText(R.id.tvDescribe, item.describe)
        .setText(R.id.tvTime, SimpleDateUtils.getNoHours(item.create_time*1000));
  }
}
