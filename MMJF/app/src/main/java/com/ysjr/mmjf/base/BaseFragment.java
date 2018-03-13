package com.ysjr.mmjf.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.ysjr.mmjf.entity.User;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-11-17.
 */

public abstract class BaseFragment extends Fragment {
    protected BaseActivity mContext;
    protected Unbinder mUnbinder;
    public BaseFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mUnbinder =  ButterKnife.bind(this, view);
        initialize(view,savedInstanceState);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) mUnbinder.unbind();
    }

    public abstract int getLayoutId();

    public abstract void initialize(View view, Bundle savedInstanceState);

    protected boolean isLogin() {
      User user = DataSupport.findFirst(User.class);
      if (user != null) {
        return true;
      }
      return false;
    }
}
