package com.ysjr.mmjf.module.dialogfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.ysjr.mmjf.R;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-12-6.
 */

public class SureOrCancelFragment extends DialogFragment {
  private static final String KEY_DESC = "key_desc";
  Unbinder unbinder;
  @BindView(R.id.tvDesc) TextView tvDesc;

  public static SureOrCancelFragment newInstance(String desc) {
    SureOrCancelFragment fg = new SureOrCancelFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_DESC, desc);
    fg.setArguments(bundle);
    return fg;
  }


  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.dialog_fragment_sure_or_cancel, container, false);
    unbinder = ButterKnife.bind(this, view);
    tvDesc.setText(getArguments().getString(KEY_DESC));
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.btnCancel) public void onBtnCancelClicked() {
    dismiss();
  }

  @OnClick(R.id.btnSure) public void onBtnSureClicked() {
    EventBus.getDefault().post("Sure");
    dismiss();
  }
}
