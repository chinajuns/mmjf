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
import com.blankj.utilcode.util.ActivityUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.MRubSuccess;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.module.manager.wallet.MCostMoneyActivity;
import com.ysjr.mmjf.utils.Api;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-12-11.
 */

public class RubPayDialogFragment extends DialogFragment {
  private static final String KEY_SCORE = "key_score";
  private static final String KEY_ID = "key_id";
  Unbinder unbinder;
  @BindView(R.id.tvScore) TextView tvScore;
  private int id;
  private String score;
  public static RubPayDialogFragment newInstance(int score,int id) {
    RubPayDialogFragment fg = new RubPayDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_SCORE, score);
    bundle.putInt(KEY_ID,id);
    fg.setArguments(bundle);
    return fg;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    View view = inflater.inflate(R.layout.m_dialog_fragment_rub_pay, container, false);
    unbinder = ButterKnife.bind(this, view);
    score = getArguments().getInt(KEY_SCORE, 0) + "";
    tvScore.setText(score);
    id = getArguments().getInt(KEY_ID);
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
    OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_ORDER_PURCHASE)
        .tag(getActivity())
        .params("id",id)
        .execute(new DialogCallback<HttpResult<Void>>(getActivity()) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            MRubSuccess rubSuccess = new MRubSuccess();
            rubSuccess.score = score;
            EventBus.getDefault().post(rubSuccess);
          }
        });
  }
}
