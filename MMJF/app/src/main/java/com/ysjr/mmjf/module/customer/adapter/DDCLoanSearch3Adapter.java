package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.CLoanSearchBean;
import java.util.List;

/**
 * Created by Administrator on 2017-12-4.
 */

public class DDCLoanSearch3Adapter extends BaseQuickAdapter<CLoanSearchBean.FocusBean.ValuesBeanX,BaseViewHolder> {
  List<CLoanSearchBean.FocusBean.ValuesBeanX> data;
  public DDCLoanSearch3Adapter(int layoutResId, @Nullable List<CLoanSearchBean.FocusBean.ValuesBeanX> data) {
    super(layoutResId, data);
    this.data = data;
  }

  @Override protected void convert(BaseViewHolder helper, CLoanSearchBean.FocusBean.ValuesBeanX item) {
    helper.setText(R.id.tvValue, item.attr_value);
    if (item.isChecked) {
      helper.setVisible(R.id.imgState, true);
    } else {
      helper.setVisible(R.id.imgState, false);
    }
    if (helper.getAdapterPosition() == data.size() - 1) {
      helper.setGone(R.id.line, false);
    } else {
      helper.setGone(R.id.line, true);
    }
  }

  public void clearState() {
    if (data != null) {
      for (CLoanSearchBean.FocusBean.ValuesBeanX bean : data) {
        bean.isChecked = false;
      }
      notifyDataSetChanged();
    }
  }
}
