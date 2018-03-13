package com.ysjr.mmjf.module.popup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import com.ysjr.mmjf.R;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Administrator on 2017-11-29.
 */

public class ComplaintPopup extends BasePopupWindow implements View.OnClickListener {
  private TextView tvOne,tvTwo,tvThree,tvCancel;
  private OnItemClickListener mListener;

  public void setListener(OnItemClickListener listener) {
    mListener = listener;
  }
  @Override public void onClick(View v) {
    dismiss();
    switch (v.getId()) {
      case R.id.tvOne:
        if (mListener != null) mListener.onClick(ItemType.ONE);
        break;
      case R.id.tvTwo:
        if (mListener != null) mListener.onClick(ItemType.TWO);
        break;
      case R.id.tvThree:
        if (mListener != null) mListener.onClick(ItemType.THREE);
        break;
    }
  }

  public enum ItemType {
    ONE,TWO,THREE
  }

  public interface OnItemClickListener {
    void onClick(ItemType type);
  }

  public ComplaintPopup(Context context) {
    super(context);
    tvOne = (TextView) findViewById(R.id.tvOne);
    tvTwo = (TextView) findViewById(R.id.tvTwo);
    tvThree = (TextView) findViewById(R.id.tvThree);
    tvCancel = (TextView) findViewById(R.id.tvCancel);
    tvOne.setOnClickListener(this);
    tvTwo.setOnClickListener(this);
    tvThree.setOnClickListener(this);
    tvCancel.setOnClickListener(this);
  }

  @Override protected Animation initShowAnimation() {
    return null;
  }

  @Override public View getClickToDismissView() {
    return getPopupWindowView();
  }

  @Override public View onCreatePopupView() {
    return createPopupById(R.layout.c_popup_store_complaint);
  }

  @Override public View initAnimaView() {
    return null;
  }
}
