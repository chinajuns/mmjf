package com.ysjr.mmjf.module.manager.wallet;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import butterknife.BindView;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MCostSuccessActivity extends TopBarBaseActivity {
  @BindView(R.id.tvCount) TextView tvCount;
  private CountDownTimer mTimer;
  private String mCountStr;
  @Override protected int getContentView() {
    return R.layout.m_activity_cost_success;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.cost_success));
    mCountStr = getString(R.string.cost_success_count);
    mTimer = new CountDownTimer(6000,1000) {
      @Override public void onTick(long millisUntilFinished) {
        setTvCount(millisUntilFinished);
      }

      @Override public void onFinish() {
        finish();
      }
    };
    mTimer.start();
  }

  private void setTvCount(long ms) {
    String count = ms / 1000 + "s";
    String curStr = mCountStr.replace("5s", count);
    SpannableString spannableString = new SpannableString(curStr);
    ForegroundColorSpan colorSpan =
        new ForegroundColorSpan(getResources().getColor(R.color.text_33abff_color));
    spannableString.setSpan(colorSpan, curStr.indexOf(",") + 1, curStr.indexOf(",") + 3,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvCount.setText(spannableString);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mTimer != null) mTimer.cancel();
  }
}
