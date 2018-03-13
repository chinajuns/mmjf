package com.ysjr.mmjf.module.manager.store;

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
import com.ysjr.mmjf.R;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-12-14.
 */

public class DelegateDialogFragment extends DialogFragment {
  Unbinder unbinder;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.dialog_fragment_delegate, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.btnIgnore) public void onBtnIgnoreClicked() {
    dismiss();
  }

  @OnClick(R.id.btnDelegate) public void onBtnDelegateClicked() {
    dismiss();
    EventBus.getDefault().post(1);
  }
}
