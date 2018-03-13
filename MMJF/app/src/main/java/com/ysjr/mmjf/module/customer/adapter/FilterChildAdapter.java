package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import android.widget.RadioButton;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.HomeFilterItem;
import java.util.List;

/**
 * Created by Administrator on 2017-12-18.
 */

public class FilterChildAdapter
    extends BaseQuickAdapter<HomeFilterItem.FilterItem, BaseViewHolder> {
  public FilterChildAdapter(int layoutResId, @Nullable List<HomeFilterItem.FilterItem> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, final HomeFilterItem.FilterItem item) {
    RadioButton rb = helper.getView(R.id.chxItem);
    rb.setText(item.options);
    if (item.isChecked) {
      rb.setChecked(true);
    } else {
      rb.setChecked(false);
    }
  }
}
