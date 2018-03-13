package com.ysjr.mmjf.module.popup;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import com.ysjr.mmjf.R;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Administrator on 2017-12-5.
 */

public class OrderPopup extends BasePopupWindow implements View.OnClickListener {
  private LinearLayout layoutContact;
  private LinearLayout layoutComplaint;
  private OnItemClickListener mListener;

  public enum ItemType {
    CONTACT,COMPLAINT
  }

  public void setListener(OnItemClickListener listener) {
    this.mListener = listener;
  }

  public OrderPopup(Activity context) {
    super(context);
    layoutContact = (LinearLayout) findViewById(R.id.layoutContact);
    layoutComplaint = (LinearLayout) findViewById(R.id.layoutComplaint);

    layoutContact.setOnClickListener(this);
    layoutComplaint.setOnClickListener(this);
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
    return createPopupById(R.layout.c_popup_order_detail);
  }

  @Override
  public View initAnimaView() {
    return null;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.layoutContact:
        if (mListener != null) {
          mListener.onClick(ItemType.CONTACT);
        }
        break;
      case R.id.layoutComplaint:
        if (mListener != null) {
          mListener.onClick(ItemType.COMPLAINT);
        }
        break;
    }
    dismiss();
  }

  public interface OnItemClickListener {
    void onClick(ItemType type);
  }
}
