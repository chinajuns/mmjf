package com.ysjr.mmjf.module.customer.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.News;
import com.ysjr.mmjf.entity.NewsBean;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.SimpleDateUtils;
import java.util.List;

/**
 * Created by Administrator on 2017-12-4.
 */

public class NewsMoreAdapter extends BaseQuickAdapter<NewsBean.ListParent.ListChild,BaseViewHolder> {
  public NewsMoreAdapter(int layoutResId, @Nullable List<NewsBean.ListParent.ListChild> data) {
    super(layoutResId, data);
  }

  @Override protected void convert(BaseViewHolder helper, NewsBean.ListParent.ListChild item) {
    Glide.with(mContext)
        .load(Api.IMAGE_ROOT_URL + item.picture)
        .placeholder(R.drawable.suotu1)
        .into((ImageView) helper.getView(R.id.imgPic));
    String dateAndNum = SimpleDateUtils.getNoHours(item.create_time*1000) + "/" + item.views + "阅读量";
    helper.setText(R.id.tvTitle, item.title).setText(R.id.tvDateAndNum, dateAndNum);
  }
}
