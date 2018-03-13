package com.ysjr.mmjf.module.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.blankj.utilcode.util.ActivityUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.module.manager.me.verify.MVerify1Activity;

/**
 * Created by Administrator on 2017-12-26.
 */

public class VerifyDialogFragment extends DialogFragment {
  Unbinder unbinder;
  public static VerifyDialogFragment newInstance() {
    VerifyDialogFragment fg = new VerifyDialogFragment();
    return fg;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.m_dialog_fragment_verify, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick({ R.id.btnCancel, R.id.btnVerify }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btnCancel:
        dismiss();
        break;
      case R.id.btnVerify:
        dismiss();
        ActivityUtils.startActivity(MVerify1Activity.class);
        break;
    }
  }
}
