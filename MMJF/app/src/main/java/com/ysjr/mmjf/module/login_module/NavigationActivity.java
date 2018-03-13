package com.ysjr.mmjf.module.login_module;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.module.login_module.login.LoginActivity;
import com.ysjr.mmjf.utils.Constant;

public class NavigationActivity extends BaseActivity {
    @BindView(R.id.btnCustomerLogin) Button btnCustomerLogin;
    @BindView(R.id.btnManagerLogin) Button btnManagerLogin;

    @Override
    protected void setUIBeforeSetContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    public void initialize(Bundle savedInstanceState) {
      ButterKnife.bind(this);
    }
    @OnClick(R.id.imgBack) void back(){finish();}
    @OnClick(R.id.btnCustomerLogin) void customerLogin() {
        Intent intent = new Intent(mContext,LoginActivity.class);
        intent.putExtra(LoginActivity.KEY_LOGIN_TYPE, Constant.TYPE_ROLE_CUSTOMER);
        ActivityUtils.startActivity(intent);
    }
    @OnClick(R.id.btnManagerLogin) void managerLogin(){
        Intent intent = new Intent(mContext,LoginActivity.class);
        intent.putExtra(LoginActivity.KEY_LOGIN_TYPE,Constant.TYPE_ROLE_MANAGER);
        ActivityUtils.startActivity(intent);
    }
}
