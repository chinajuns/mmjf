package com.ysjr.mmjf.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.design.widget.TabLayout;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseActivity;
import com.ysjr.mmjf.base.BaseApplication;
import com.ysjr.mmjf.module.login_module.protocol.ProtocolActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017-11-23.
 */

public class CommonUtils {
  private CommonUtils() {

  }

  public static void setEmojFilter(EditText editText,int inputLength) {
    InputFilter inputFilter= new InputFilter() {
      Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
          Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

      @Override
      public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
          ToastUtils.showShort("不能输入表情");
          return "";
        }
        return null;
      }
    };
    editText.setFilters(new InputFilter[]{inputFilter,new InputFilter.LengthFilter(inputLength)});
  }

  public static int getAge(Date birthDay) {
    Calendar cal = Calendar.getInstance();

    if (cal.before(birthDay)) {
      throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
    }
    int yearNow = cal.get(Calendar.YEAR);
    int monthNow = cal.get(Calendar.MONTH);
    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
    cal.setTime(birthDay);

    int yearBirth = cal.get(Calendar.YEAR);
    int monthBirth = cal.get(Calendar.MONTH);
    int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

    int age = yearNow - yearBirth;

    if (monthNow <= monthBirth) {
      if (monthNow == monthBirth) {
        if (dayOfMonthNow < dayOfMonthBirth) age--;
      } else {
        age--;
      }
    }
    return age;
  }
  public static String getJson(Context context,String fileName) {

    StringBuilder stringBuilder = new StringBuilder();
    try {
      AssetManager assetManager = context.getAssets();
      BufferedReader bf = new BufferedReader(new InputStreamReader(
          assetManager.open(fileName)));
      String line;
      while ((line = bf.readLine()) != null) {
        stringBuilder.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return stringBuilder.toString();
  }

  /**
   * 设置TabLayout下划线与字体宽度一致
   */
  public static void reflex(final TabLayout tabLayout) {
    //了解源码得知 线的宽度是根据 tabView的宽度来设置的
    tabLayout.post(new Runnable() {
      @Override public void run() {
        try {
          //拿到tabLayout的mTabStrip属性
          LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

          int dp10 = SizeUtils.dp2px(10);

          for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            View tabView = mTabStrip.getChildAt(i);

            //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
            Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
            mTextViewField.setAccessible(true);

            TextView mTextView = (TextView) mTextViewField.get(tabView);

            tabView.setPadding(0, 0, 0, 0);

            //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
            int width = 0;
            width = mTextView.getWidth();
            if (width == 0) {
              mTextView.measure(0, 0);
              width = mTextView.getMeasuredWidth();
            }

            //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
            LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) tabView.getLayoutParams();
            params.width = width;
            params.leftMargin = dp10;
            params.rightMargin = dp10;
            tabView.setLayoutParams(params);

            tabView.invalidate();
          }
        } catch (NoSuchFieldException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    });
  }
  /**
   * 验证电话号码是否正确
   * @param textPhone
   * @return
   */
  public static boolean checkPhone(EditText textPhone) {
    String phone = textPhone.getText().toString();
    if (TextUtils.isEmpty(phone)) {
      textPhone.setError(getString(R.string.phone_not_empty));
      return false;
    }
    if (!RegexUtils.isMobileExact(phone)) {
      textPhone.setError(getString(R.string.not_mobile_phone));
      return false;
    }
    return true;
  }

  /**
   * 检查密码和确认密码是否正确
   *
   * 密码规则 6-16位字符和数字组合
   * @param textPsw
   * @param textConfirmPsw
   * @return
   */
  public static boolean checkPsw(EditText textPsw,EditText textConfirmPsw) {
    String psw = textPsw.getText().toString();
    String confirmPsw = textConfirmPsw.getText().toString();
    if (TextUtils.isEmpty(psw)) {
      textPsw.setError(getString(R.string.psw_not_empty));
      return false;
    }
    if (psw.length() < 6) {
      textPsw.setError(getString(R.string.psw_length_not_enough));
      return false;
    }
    if (!psw.equals(confirmPsw)) {
      textConfirmPsw.setError(getString(R.string.psw_not_same));
      return false;
    }
    return true;
  }

  /**
   * 检查协议是否勾选
   * @param checkBox
   * @return
   */
  public static boolean checkProtocol(CheckBox checkBox) {
    if (!checkBox.isChecked()) {
      SnackbarUtils.with(checkBox)
          .setMessage(getString(R.string.read_and_agree_protocol))
          .showError();
      return false;
    }
    return true;
  }

  /**
   * 检查必填项是否填写
   * @param editText
   * @param isDisplay 是否显示错误信息
   * @return
   */
  public static boolean checkNotNull(TextView editText,boolean isDisplay) {
    if (TextUtils.isEmpty(editText.getText().toString())) {
      if (isDisplay) {
        editText.setError(getString(R.string.not_empty));
      }
      return false;
    }
    return true;
  }

  /**
   * 设置协议样式和点击事件
   * @param textView
   */
  public static <T> void setProtocolSpan(TextView textView,int strId, final Class<T> clazz) {
    //  创建SpannableStringBuilder
    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
    spannableStringBuilder.append(CommonUtils.getString(strId));
    //  创建ForegroundColorSpan
    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor(R.color.theme_color));
    //  设置ForegroundColorSpan到SpannableStringBuilder
    int start = spannableStringBuilder.toString().indexOf("《");
    spannableStringBuilder.setSpan(colorSpan,start,spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    ClickableSpan clickableSpan = new ClickableSpan() {
      @Override public void onClick(View widget) {
        ActivityUtils.startActivity(clazz);
      }

      @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
      }
    };
    spannableStringBuilder.setSpan(clickableSpan,start,spannableStringBuilder.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    //  将SpannableStringBuilder设置给textview
    textView.setText(spannableStringBuilder);
    //  设置此属性让textview可点击
    textView.setMovementMethod(LinkMovementMethod.getInstance());
  }

  /**
   * 获取指定资源的字符串
   * @param resId
   * @return
   */
  public static String getString(int resId) {
    return BaseApplication.mInstance.getResources().getString(resId);
  }

  /**
   * 获取指定资源的颜色
   * @param resId
   * @return
   */
  public static int getColor(int resId) {
    return BaseApplication.mInstance.getResources().getColor(resId);
  }
}
