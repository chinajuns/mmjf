package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.CLoanSearchBean;
import com.ysjr.mmjf.entity.JsonBean;
import java.util.List;

/**
 * Created by Administrator on 2017-12-4.
 */

public class DDCLoanSearch2Adapter extends BaseQuickAdapter<JsonBean.CityBean.DistrictBean,BaseViewHolder> {
  List<JsonBean.CityBean.DistrictBean> data;
  public DDCLoanSearch2Adapter(int layoutResId, @Nullable List<JsonBean.CityBean.DistrictBean> data) {
    super(layoutResId, data);
    this.data = data;
  }

  @Override protected void convert(BaseViewHolder helper, JsonBean.CityBean.DistrictBean item) {
    helper.setText(R.id.tvValue, item.getName());
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
      for (JsonBean.CityBean.DistrictBean bean : data) {
        bean.isChecked = false;
      }
      notifyDataSetChanged();
    }
  }
}
