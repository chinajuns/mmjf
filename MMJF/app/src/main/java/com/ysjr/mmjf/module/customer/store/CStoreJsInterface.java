package com.ysjr.mmjf.module.customer.store;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

/**
 * Created by Administrator on 2018-1-9.
 */

public class CStoreJsInterface {
  private Context mContext;
  private int mManagerId;
  public CStoreJsInterface(Context context,int managerId) {
    mContext = context;
    mManagerId = managerId;
  }
  @JavascriptInterface
  public void onProClicked(String proId) {
    Intent intent = new Intent(mContext,ProductDetailActivity.class);
    intent.putExtra(ProductDetailActivity.KEY_MANAGER_ID, mManagerId);
    intent.putExtra(ProductDetailActivity.KEY_PRO_ID, proId);
    mContext.startActivity(intent);
  }
  @JavascriptInterface
  public void onEvaluateClicked() {
    Intent intent = new Intent(mContext,CustomerEvaluateActivity.class);
    intent.putExtra(CustomerEvaluateActivity.KEY_MANAGER_ID, mManagerId);
    mContext.startActivity(intent);
  }
}
