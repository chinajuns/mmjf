package com.ysjr.mmjf.module.manager.cus_order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.Order;
import com.ysjr.mmjf.utils.TextWatcherChecker;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-12-13.
 */

public class MCusOrderDetailNextDialogFragment extends DialogFragment {
  @BindView(R.id.etSignMoney) EditText etSignMoney;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.btnCancel) Button btnCancel;
  @BindView(R.id.btnCommit) Button btnCommit;
  @BindView(R.id.tvUnit) TextView tvUnit;
  Unbinder unbinder;
  public static final int TYPE_SIGN = 0x100;
  public static final int TYPE_SEND = 0x101;
  public static final int TYPE_THROW_ORDER = 0x102;
  private static final String KEY_TYPE = "key_type";
  int type;

  public static MCusOrderDetailNextDialogFragment newInstance(int type) {
    MCusOrderDetailNextDialogFragment fg = new MCusOrderDetailNextDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TYPE, type);
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
    View view =
        inflater.inflate(R.layout.m_dialog_fragment_cus_order_detail_next, container, false);
    unbinder = ButterKnife.bind(this, view);
    type = getArguments().getInt(KEY_TYPE);
    if (type == TYPE_SIGN) {
      tvTitle.setText(R.string.sign_money);
      etSignMoney.setHint(R.string.input_sign_money);
    } else if (type == TYPE_SEND) {
      tvTitle.setText(R.string.send_money);
      etSignMoney.setHint(R.string.input_send_money);
    } else if (type == TYPE_THROW_ORDER) {
      tvTitle.setText("甩单积分");
      etSignMoney.setHint("请输入甩单积分");
      tvUnit.setText("积分");
    }
    new TextWatcherChecker(btnCommit).setRelevantView(etSignMoney);
    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick(R.id.btnCommit) public void onBtnCommitClicked() {
    dismiss();
    Order order = new Order();
    order.type = type;
    order.money = etSignMoney.getText().toString();
    EventBus.getDefault().post(order);
  }

  @OnClick(R.id.btnCancel) public void onBtnCancelClicked() {
    dismiss();
  }
}
