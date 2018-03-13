package com.ysjr.mmjf.module.customer.news;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;
import com.ysjr.mmjf.R;

/**
 * Created by Administrator on 2017-12-4.
 */

public class CNewsBannerImageLoader extends ImageLoader {
  @Override public void displayImage(Context context, Object path, ImageView imageView) {
    Glide.with(context).load((String) path).placeholder(R.drawable.banner1).into(imageView);
  }
}
