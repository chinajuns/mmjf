package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.UserScoreType;
import java.util.List;

/**
 * Created by Administrator on 2018-1-31.
 */

public class CustomerTagAdapter extends BaseQuickAdapter<UserScoreType.TypeBean,BaseViewHolder> {
  public CustomerTagAdapter(int layoutResId, @Nullable List<UserScoreType.TypeBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, UserScoreType.TypeBean item) {
    helper.setText(R.id.chxItem, item.label);
    if (item.isChecked) {
      helper.setChecked(R.id.chxItem, true);
    } else {
      helper.setChecked(R.id.chxItem, false);
    }
  }
}
