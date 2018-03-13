package com.ysjr.mmjf.module.popup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import com.ysjr.mmjf.R;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Administrator on 2017-12-7.
 */

public class SharePopup extends BasePopupWindow implements View.OnClickListener {
  TextView tvSina,tvFriendCircle,tvWeixin,tvQQ, tvQQSpace,tvCopyUrl,tvCancel;
  private OnItemClickListener mListener;

  public enum ItemType {
    SINA,CIRCLE,WEIXIN,QQ,QQSPACE,COPY
  }

  public interface OnItemClickListener {
    void onClick(ItemType type);
  }

  public void setListener(OnItemClickListener listener) {
    mListener = listener;
  }
  public SharePopup(Context context) {
    super(context);
    tvSina = (TextView) findViewById(R.id.tvSina);
    tvFriendCircle = (TextView) findViewById(R.id.tvFriendCircle);
    tvWeixin = (TextView) findViewById(R.id.tvWeixin);
    tvQQ = (TextView) findViewById(R.id.tvQQ);
    tvQQSpace = (TextView) findViewById(R.id.tvQQSpace);
    tvCopyUrl = (TextView) findViewById(R.id.tvCopy);
    tvCancel = (TextView) findViewById(R.id.tvCancel);

    tvSina.setOnClickListener(this);
    tvFriendCircle.setOnClickListener(this);
    tvWeixin.setOnClickListener(this);
    tvQQ.setOnClickListener(this);
    tvQQSpace.setOnClickListener(this);
    tvCopyUrl.setOnClickListener(this);
    tvCancel.setOnClickListener(this);
  }

  @Override protected Animation initShowAnimation() {
    return null;
  }

  @Override public View getClickToDismissView() {
    return getPopupWindowView();
  }

  @Override public View onCreatePopupView() {
    return createPopupById(R.layout.share_layout);
  }

  @Override public View initAnimaView() {
    return null;
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tvSina:
        mListener.onClick(ItemType.SINA);
        break;
      case R.id.tvFriendCircle:
        mListener.onClick(ItemType.CIRCLE);
        break;
      case R.id.tvWeixin:
        mListener.onClick(ItemType.WEIXIN);
        break;
      case R.id.tvQQ:
        mListener.onClick(ItemType.QQ);
        break;
      case R.id.tvQQSpace:
        mListener.onClick(ItemType.QQSPACE);
        break;
      case R.id.tvCopy:
        mListener.onClick(ItemType.COPY);
        break;
      case R.id.tvCancel:
        dismiss();
        break;
    }
  }
}
