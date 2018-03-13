package com.ysjr.mmjf.module.manager.me.verify;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import java.util.List;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MVerify2Activity extends TopBarBaseActivity {
  private static final int REQUEST_CODE_UP = 1;
  private static final int REQUEST_CODE_DOWN = 2;
  @BindView(R.id.tvTitle1) TextView tvTitle1;
  @BindView(R.id.imgUpCard) ImageView imgUpCard;
  @BindView(R.id.tvTitle2) TextView tvTitle2;
  @BindView(R.id.imgDownCard) ImageView imgDownCard;
  @BindView(R.id.tvDesc3) TextView tvDesc3;
  @BindView(R.id.tvDesc4) TextView tvDesc4;
  @BindView(R.id.btnNext) Button btnNext;
  private String mAvatarPath;
  private String mUserName;
  private String mIdCard;
  private String mAgentType;
  private String mAgentName;
  private String mDepartment;
  private String mProvinceId;
  private String mCityId;
  private String mRegionId;
  private String mUpCardPath;
  private String mDownCardPath;
  @Override protected int getContentView() {
    return R.layout.m_activity_verify_2_step;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.verify_name));
    setTxtStyle();
    handleIntent();
  }

  private void handleIntent() {
    mAvatarPath = getIntent().getStringExtra("header_img");
    mUserName = getIntent().getStringExtra("username");
    mIdCard = getIntent().getStringExtra("idcard");
    mProvinceId = getIntent().getStringExtra("province_id");
    mCityId = getIntent().getStringExtra("city_id");
    mRegionId = getIntent().getStringExtra("region_id");
    mAgentName = getIntent().getStringExtra("agent_name");
    mAgentType = getIntent().getStringExtra("agent_type");
    mDepartment = getIntent().getStringExtra("department");
  }

  private void setTxtStyle() {
    SpannableString spannableTitle1 = new SpannableString("手持身份证正面证照（文字清晰，四角齐全)");
    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.text_3_b3_color));
    spannableTitle1.setSpan(colorSpan,9,spannableTitle1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvTitle1.setText(spannableTitle1);

    SpannableString spannableTitle2 = new SpannableString("手持身份证反面证照（文字清晰，四角齐全)");
    spannableTitle2.setSpan(colorSpan,9,spannableTitle1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvTitle2.setText(spannableTitle2);

    SpannableString spannableTitle3 = new SpannableString("3、仅支持.jpg.bmp.png.gif的图片格式，建议图片大小不超过3M。");
    ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.parseColor("#33abff"));
    spannableTitle3.setSpan(colorSpan2,5,21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvDesc3.setText(spannableTitle3);

    SpannableString spannableTitle4 = new SpannableString("4、您提供的照片信息毛毛金服将予以保护，不会用于其他用途。");
    ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(getResources().getColor(R.color.theme_color));
    spannableTitle4.setSpan(colorSpan3,10,14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvDesc4.setText(spannableTitle4);
  }

  @OnClick(R.id.imgUpCard) public void onImgUpCardClicked() {
    Matisse.from(mContext)
        .choose(MimeType.of(MimeType.JPEG,MimeType.PNG))
        .countable(true)
        .capture(true)
        .captureStrategy(
            new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .thumbnailScale(0.85f)
        .imageEngine(new GlideEngine())
        .forResult(REQUEST_CODE_UP);
  }

  @OnClick(R.id.imgDownCard) public void onImgDownCardClicked() {
    Matisse.from(mContext)
        .choose(MimeType.of(MimeType.JPEG,MimeType.PNG))
        .countable(true)
        .capture(true)
        .captureStrategy(
            new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .thumbnailScale(0.85f)
        .imageEngine(new GlideEngine())
        .forResult(REQUEST_CODE_DOWN);
  }

  @OnClick(R.id.btnNext) public void onViewClicked() {
    Intent intent = new Intent(mContext, MVerify3Activity.class);
    intent.putExtra("header_img", mAvatarPath);
    intent.putExtra("username", mUserName);
    intent.putExtra("idcard", mIdCard);
    intent.putExtra("agent_type", mAgentType);
    intent.putExtra("agent_name", mAgentName);
    intent.putExtra("department", mDepartment);
    intent.putExtra("province_id", mProvinceId);
    intent.putExtra("city_id", mCityId);
    intent.putExtra("region_id", mRegionId);
    intent.putExtra("hand_idcard_front", mUpCardPath);
    intent.putExtra("hand_idcard_back", mDownCardPath);
    ActivityUtils.startActivity(intent);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      return;
    }
    List<String> result = Matisse.obtainPathResult(data);
    switch (requestCode) {
      case REQUEST_CODE_UP:
        mUpCardPath = result.get(0);
        Glide.with(mContext).load(mUpCardPath).into(imgUpCard);
        break;
      case REQUEST_CODE_DOWN:
        mDownCardPath = result.get(0);
        Glide.with(mContext).load(mDownCardPath).into(imgDownCard);
        break;
    }
    if (mUpCardPath != null && mDownCardPath != null) {
      btnNext.setEnabled(true);
    }
  }
}
