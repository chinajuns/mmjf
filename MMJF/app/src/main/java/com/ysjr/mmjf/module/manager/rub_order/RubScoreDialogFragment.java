package com.ysjr.mmjf.module.manager.rub_order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.blankj.utilcode.util.ActivityUtils;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.module.manager.wallet.MCostMoneyActivity;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-12-11.
 */

public class RubScoreDialogFragment extends DialogFragment {
  Unbinder unbinder;

  public static RubScoreDialogFragment newInstance() {
    RubScoreDialogFragment fg = new RubScoreDialogFragment();
    return fg;
  }
  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.m_dialog_fragment_rub_score, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.btnCancel) public void onBtnCancelClicked() {
    dismiss();
  }

  @OnClick(R.id.btnPay) public void onBtnPayClicked() {
    dismiss();
    ActivityUtils.startActivity(MCostMoneyActivity.class);
  }
}
