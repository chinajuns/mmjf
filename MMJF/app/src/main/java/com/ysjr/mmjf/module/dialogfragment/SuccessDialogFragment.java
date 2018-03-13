package com.ysjr.mmjf.module.dialogfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
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

public class SuccessDialogFragment extends DialogFragment {
  private static final String KEY_RES_DRAWABLE = "key_drawable";
  private static final String KEY_DESC = "key_desc";
  Unbinder unbinder;
  @BindView(R.id.tvDesc) TextView tvDesc;
  @BindView(R.id.iv) ImageView imageView;
  public static SuccessDialogFragment newInstance(int resDrawable, String desc) {
    SuccessDialogFragment fg = new SuccessDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_RES_DRAWABLE, resDrawable);
    bundle.putString(KEY_DESC, desc);
    fg.setArguments(bundle);
    return fg;
  }
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(false);
  }
  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.m_dialog_fragment_success, container, false);
    unbinder = ButterKnife.bind(this, view);
    int res = getArguments().getInt(KEY_RES_DRAWABLE);
    String desc = getArguments().getString(KEY_DESC);
    tvDesc.setText(desc);
    imageView.setImageResource(res);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.btnSure) public void onViewClicked() {
    dismiss();
    EventBus.getDefault().post(new Success());
  }
}
