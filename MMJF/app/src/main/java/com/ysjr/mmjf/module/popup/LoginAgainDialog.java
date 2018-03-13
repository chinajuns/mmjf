package com.ysjr.mmjf.module.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018-1-20.
 */

public class LoginAgainDialog extends Dialog {
  private boolean isCustomer;
  public LoginAgainDialog(@NonNull Context context,boolean isCustomer) {
    super(context);
    this.isCustomer = isCustomer;
  }

  public LoginAgainDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
  }

  protected LoginAgainDialog(@NonNull Context context, boolean cancelable,
      @Nullable OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_duplicate_login, null);
    setContentView(view);
    Window window = getWindow();
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.gravity = Gravity.CENTER;
    lp.width = SizeUtils.dp2px(300);
    lp.height = SizeUtils.dp2px(150);
    lp.dimAmount = 0.8f;
    window.setAttributes(lp);

    TextView textView = view.findViewById(R.id.tvLogin);
    ImageView ivClose = view.findViewById(R.id.ivClose);
    textView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ActivityUtils.finishAllActivities();
        ActivityUtils.startActivity(NavigationActivity.class);
      }
    });

    ivClose.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        DataSupport.deleteAll(User.class);
        EventBus.getDefault().post(new User());
        dismiss();
      }
    });

    if (!isCustomer) {
      ivClose.setVisibility(View.GONE);
    }

    setCancelable(false);
  }
}
