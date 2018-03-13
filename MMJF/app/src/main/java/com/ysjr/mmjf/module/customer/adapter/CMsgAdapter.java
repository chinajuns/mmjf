package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.MsgWrapBean;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-4.
 */

public class CMsgAdapter extends BaseQuickAdapter<MsgWrapBean.DataBean,BaseViewHolder> {
  public CMsgAdapter(int layoutResId, @Nullable List<MsgWrapBean.DataBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, MsgWrapBean.DataBean item) {
    if (item.status == 1) {//未读
      helper.setVisible(R.id.imgMsgState, true);
    } else {
      helper.setVisible(R.id.imgMsgState, false);
    }
    helper.setText(R.id.tvTitle, item.title)
        .setText(R.id.tvContent, item.content)
        .setText(R.id.tvTime, SimpleDateUtils.getHasHours(item.create_time*1000));
  }
}
