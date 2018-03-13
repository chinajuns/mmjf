package com.ysjr.mmjf.module.manager.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.entity.ProBean;
import java.util.List;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MShareStoreAdapter extends BaseQuickAdapter<ProBean,BaseViewHolder> {
  public MShareStoreAdapter(int layoutResId, @Nullable List<ProBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, ProBean item) {

  }
}
