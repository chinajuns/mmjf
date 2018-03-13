package com.ysjr.mmjf.module.customer.store;

import android.support.annotation.Nullable;
import android.widget.RatingBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.Evaluate;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-1.
 */

public class EvaluateAdapter extends BaseQuickAdapter<Evaluate.DataItem,BaseViewHolder> {
  public EvaluateAdapter(int layoutResId, @Nullable List<Evaluate.DataItem> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, Evaluate.DataItem item) {
    helper.setText(R.id.tvName, item.username)
        .setText(R.id.tvDate, SimpleDateUtils.getHasHours(item.create_time*1000))
        .setText(R.id.tvScore, item.score_avg + "")
        .setText(R.id.tvContent, item.describe);
    ScaleRatingBar ratingBar = helper.getView(R.id.ratingBar);
    ratingBar.setRating(item.score_avg);
  }
}
