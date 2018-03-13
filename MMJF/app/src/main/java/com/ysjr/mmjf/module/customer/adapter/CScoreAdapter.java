package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.Score;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CScoreAdapter extends BaseQuickAdapter<Score.ListBean,BaseViewHolder> {
  public CScoreAdapter(int layoutResId, @Nullable List<Score.ListBean> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, Score.ListBean item) {
    if (item.number<0) {
      helper.setImageResource(R.id.imgScoreState, R.drawable.dot_blue_shape);
    } else {
      helper.setImageResource(R.id.imgScoreState, R.drawable.dot_red_shape);
    }
    helper.setText(R.id.tvState, item.description)
        .setText(R.id.tvTime, SimpleDateUtils.getHasHours(item.create_time*1000))
        .setText(R.id.tvScoreNumber, item.number > 0 ? "+" + item.number + "分" : item.number + "分");
  }
}
