package com.ysjr.mmjf.module.manager.rub_order;

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
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.Success;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-12-11.
 */

public class RubPaySuccessDialogFragment extends DialogFragment {
  private static final String KEY_SCORE = "key_score";
  private static final String KEY_TYPE = "key_type";
  Unbinder unbinder;
  @BindView(R.id.tvScore) TextView tvScore;
  public static RubPaySuccessDialogFragment newInstance(String score) {
    RubPaySuccessDialogFragment fg = new RubPaySuccessDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putString(KEY_SCORE,score);
    fg.setArguments(bundle);
    return fg;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.m_dialog_fragment_rub_pay_success, container, false);
    unbinder = ButterKnife.bind(this, view);
    tvScore.setText("支付金额："+getArguments().getString(KEY_SCORE)+"积分");
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.btnSure) public void onViewClicked() {
    EventBus.getDefault().post(true);
  }
}
