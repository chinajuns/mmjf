package com.ysjr.mmjf.module.customer.news;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ysjr.mmjf.R;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Administrator on 2017-12-4.
 */

public class NewsPopup extends BasePopupWindow implements View.OnClickListener {
  private LinearLayout layoutCollect;
  private LinearLayout layoutShare;
  private ImageView imgCollect;
  private OnItemClickListener mListener;

  public enum ItemType {
    COLLECT,SHARE
  }

  public void setListener(OnItemClickListener listener) {
    this.mListener = listener;
  }

  public NewsPopup(Activity context, boolean isCollect) {
    super(context);
    layoutCollect = (LinearLayout) findViewById(R.id.layoutCollect);
    layoutShare = (LinearLayout) findViewById(R.id.layoutShare);
    imgCollect = (ImageView) findViewById(R.id.imgCollect);
    if (isCollect) {
      imgCollect.setImageResource(R.drawable.btn_collect);
    } else {
      imgCollect.setImageResource(R.drawable.btn_un_collect);
    }

    layoutCollect.setOnClickListener(this);
    layoutShare.setOnClickListener(this);
  }

  @Override
  protected Animation initShowAnimation() {
    return null;
  }

  @Override
  public View getClickToDismissView() {
    return getPopupWindowView();
  }

  @Override
  public View onCreatePopupView() {
    return createPopupById(R.layout.c_popup_news_more);
  }

  @Override
  public View initAnimaView() {
    return null;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.layoutCollect:
        if (mListener != null) {
          mListener.onClick(ItemType.COLLECT);
        }
        break;
      case R.id.layoutShare:
        if (mListener != null) {
          mListener.onClick(ItemType.SHARE);
        }
        break;
    }
    dismiss();
  }

  public interface OnItemClickListener {
    void onClick(ItemType type);
  }
}
