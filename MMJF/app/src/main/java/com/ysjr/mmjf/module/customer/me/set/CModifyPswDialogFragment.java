package com.ysjr.mmjf.module.customer.me.set;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.blankj.utilcode.util.ActivityUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.module.MainActivity;
import com.ysjr.mmjf.module.login_module.NavigationActivity;
import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CModifyPswDialogFragment extends DialogFragment {
  private static final String KEY_IS_CUSTOMER = "is_customer";
  Unbinder unbinder;
  @BindView(R.id.ivClose) ImageView ivClose;
  public static CModifyPswDialogFragment newInstance(boolean isCustomer) {
    CModifyPswDialogFragment fg = new CModifyPswDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putBoolean(KEY_IS_CUSTOMER,isCustomer);
    fg.setArguments(bundle);
    return fg;
  }



  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.c_fragment_dialog_modify_psw, container, false);
    unbinder = ButterKnife.bind(this, view);
    boolean isCustomer = getArguments().getBoolean(KEY_IS_CUSTOMER,false);
    if (!isCustomer) {
      ivClose.setVisibility(View.GONE);
    }
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  /**
   * 返回主页，清除登录状态
   */
  @OnClick(R.id.ivClose) public void onIvCloseClicked() {
    DataSupport.deleteAll(User.class);
    EventBus.getDefault().post(new User());
    dismiss();
    ActivityUtils.finishToActivity(MainActivity.class,false);
  }

  /**
   * 返回登录页面
   */
  @OnClick(R.id.btnLogin) public void onBtnLoginClicked() {
    dismiss();
    ActivityUtils.finishAllActivities();
    ActivityUtils.startActivity(NavigationActivity.class);
  }
}
