package com.ysjr.mmjf.module.customer.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseApplication;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.utils.Api;
import java.util.List;

/**
 * Created by Administrator on 2017-12-4.
 */

public class LoanContentAdapter extends BaseQuickAdapter<Manager,BaseViewHolder> {
  public LoanContentAdapter(int layoutResId, @Nullable List<Manager> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, Manager item) {
    String url;
    if (item.header_img != null && item.header_img.contains("http")) {
      url = item.header_img;
    } else {
      url = Api.IMAGE_ROOT_URL + item.header_img;
    }
    Glide.with(mContext).load(url)
        .placeholder(R.drawable.touxiang)
        .into((ImageView) helper.getView(R.id.imgCard));
    helper.setText(R.id.tvLoanType, item.tag);
    helper.setText(R.id.tvName, item.loanername);
    //需要格式化金额
    helper.setText(R.id.tvLoanNumber, item.all_number + "万");
    helper.setText(R.id.tvMaxLoan, item.max_loan + "万");
    helper.setText(R.id.tv30Days, item.loan_number + "");
    if (item.is_auth == 3) {
      helper.setImageResource(R.id.ivVerify,R.drawable.ic_verify);
    } else {
      helper.setImageResource(R.id.ivVerify,R.drawable.ic_unverify);
    }
    helper.setText(R.id.tvLoanDay, item.loan_day + "天");
    ((ScaleRatingBar)helper.getView(R.id.ratingBar)).setRating(item.score);
    helper.setText(R.id.tvScore, item.score+"");
    TextView tvFocus = helper.getView(R.id.tvFocus);
    TextView tvHot = helper.getView(R.id.tvHot);
    if (!TextUtils.isEmpty(item.tags)) {
      if (item.tags.contains(",")) {
        String[] split = item.tags.split(",");
        helper.setText(R.id.tvFocus, split[0]);
        helper.setText(R.id.tvHot, split[1]);
        tvFocus.setVisibility(View.VISIBLE);
        tvHot.setVisibility(View.VISIBLE);
      } else {
        tvFocus.setText(item.tags);
        tvFocus.setVisibility(View.VISIBLE);
        tvHot.setVisibility(View.INVISIBLE);
      }
    } else {
      tvFocus.setVisibility(View.INVISIBLE);
      tvHot.setVisibility(View.INVISIBLE);
    }
  }
}
