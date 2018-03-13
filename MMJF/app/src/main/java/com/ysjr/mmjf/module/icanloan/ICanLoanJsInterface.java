package com.ysjr.mmjf.module.icanloan;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import com.ysjr.mmjf.module.customer.store.CStoreActivity;

/**
 * Created by Administrator on 2018-1-9.
 */

public class ICanLoanJsInterface {
  private Context mContext;
  public ICanLoanJsInterface(Context context) {
    mContext = context;
  }
  @JavascriptInterface
  public void onProClicked(String managerId) {
    Intent intent = new Intent(mContext,CStoreActivity.class);
    intent.putExtra(CStoreActivity.KEY_MANAGER_ID, managerId);
    mContext.startActivity(intent);
  }
}
