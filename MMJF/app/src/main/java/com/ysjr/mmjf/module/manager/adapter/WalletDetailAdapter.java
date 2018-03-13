package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.entity.Pay;
import java.util.List;

/**
 * Created by Administrator on 2017-12-11.
 */

public class WalletDetailAdapter extends BaseQuickAdapter<Pay,BaseViewHolder> {
  public WalletDetailAdapter(int layoutResId, @Nullable List<Pay> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, Pay item) {

  }
}
