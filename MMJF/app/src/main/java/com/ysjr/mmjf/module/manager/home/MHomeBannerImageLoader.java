package com.ysjr.mmjf.module.manager.home;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;
import com.ysjr.mmjf.R;

/**
 * Created by Administrator on 2017-12-8.
 */

public class MHomeBannerImageLoader extends ImageLoader {
  @Override public void displayImage(Context context, Object path, ImageView imageView) {
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      Glide.with(context).load((String) path).placeholder(R.drawable.banner1).into(imageView);
  }
}
