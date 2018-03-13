package com.ysjr.mmjf.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Administrator on 2017-11-23.
 */

public class TextWatcherChecker implements TextWatcher {
  private Button mButton;
  private TextView[] mEditTextArray;
  public TextWatcherChecker(Button button) {
    mButton = button;
  }

  public void setRelevantView(TextView... editTexts) {
    mEditTextArray = editTexts;
    for (TextView editText : editTexts) {
      editText.addTextChangedListener(this);
    }
  }
  @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override public void afterTextChanged(Editable s) {
    for (TextView editText : mEditTextArray) {
      if (!CommonUtils.checkNotNull(editText,false)) {
        mButton.setEnabled(false);
        return;
      }
    }
    mButton.setEnabled(true);
  }
}
