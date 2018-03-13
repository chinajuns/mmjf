package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.UserScoreType;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class ManagerTagAdapter extends BaseQuickAdapter<UserScoreType.TypeBean,BaseViewHolder> {
  public ManagerTagAdapter(int layoutResId, @Nullable List<UserScoreType.TypeBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, UserScoreType.TypeBean item) {
    helper.setText(R.id.chxItem, item.attr_value);
    if (item.isChecked) {
      helper.setChecked(R.id.chxItem, true);
    } else {
      helper.setChecked(R.id.chxItem, false);
    }
  }

}
