package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.ConfigBean;
import com.ysjr.mmjf.utils.CommonUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-4.
 */

public class DropDownTabAdapter extends BaseQuickAdapter<ConfigBean.ValuesBean,BaseViewHolder> {
  public DropDownTabAdapter(int layoutResId, @Nullable List<ConfigBean.ValuesBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, ConfigBean.ValuesBean item) {
    if (item.isChecked) {
      helper.setVisible(R.id.imgState, true);
    } else {
      helper.setVisible(R.id.imgState, false);
    }
    helper.setText(R.id.tvValue, item.name);
  }
}
