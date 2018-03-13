package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import android.widget.RatingBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.Manager;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CollectManagerAdapter extends BaseQuickAdapter<Manager, BaseViewHolder> {
  public CollectManagerAdapter(int layoutResId, @Nullable List<Manager> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, Manager item) {
    helper.addOnClickListener(R.id.right)
        .addOnClickListener(R.id.content)
        .setText(R.id.tvName, item.name)
        .setText(R.id.tvLoanType, item.tag)
        .setText(R.id.tvLoanDay, item.loan_day + "天")
        .setText(R.id.tvScore, item.score + "")
        .setImageResource(R.id.ivVerify,
            item.is_auth == 3 ? R.drawable.ic_verify : R.drawable.ic_unverify);
    ScaleRatingBar ratingBar = helper.getView(R.id.ratingBar);
    ratingBar.setRating(item.score);
  }
}
