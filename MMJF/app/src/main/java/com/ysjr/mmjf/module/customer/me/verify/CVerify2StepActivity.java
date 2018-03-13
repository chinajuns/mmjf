package com.ysjr.mmjf.module.customer.me.verify;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.AuthResultBean;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.PictureUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import java.io.File;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017-12-5.
 */

public class CVerify2StepActivity extends TopBarBaseActivity {
  private static final int REQUEST_CODE_UP = 1;
  private static final int REQUEST_CODE_DOWN = 2;
  public static final String KEY_PROVINCE_ID = "province_id";
  public static final String KEY_CITY_ID = "city_id";
  public static final String KEY_REGION_ID = "region_id";
  public static final String KEY_NAME = "name";
  public static final String KEY_CARD_NUMBER = "card_number";
  private static final String TEMP_PIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"temp.jpg";
  private static final int COMPRESS_PIC_QUALITY = 30;
  @BindView(R.id.tvTitle1) TextView tvTitle1;
  @BindView(R.id.imgUpCard) ImageView imgUpCard;
  @BindView(R.id.tvTitle2) TextView tvTitle2;
  @BindView(R.id.imgDownCard) ImageView imgDownCard;
  @BindView(R.id.tvDesc3) TextView tvDesc3;
  @BindView(R.id.tvDesc4) TextView tvDesc4;
  @BindView(R.id.btnCommit) Button btnCommit;
  private String mUpCardPath;
  private String mDownCardPath;
  private int provinceId;
  private int cityId;
  private String regionId;
  private String name;
  private String cardNumber;
  @Override protected int getContentView() {
    return R.layout.c_activity_verify_2_step;
  }

  @Override protected void init(Bundle savedInstanceState) {
    handleIntent();
    setTitle(getString(R.string.verify_name));
    setTxtStyle();
  }

  private void handleIntent() {
    provinceId = getIntent().getIntExtra(KEY_PROVINCE_ID, 0);
    cityId = getIntent().getIntExtra(KEY_CITY_ID, 0);
    regionId = getIntent().getStringExtra(KEY_REGION_ID);
    name = getIntent().getStringExtra(KEY_NAME);
    cardNumber = getIntent().getStringExtra(KEY_CARD_NUMBER);
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

  @OnClick(R.id.btnCommit) public void onViewClicked() {
      httpVerify();
  }


  private void httpVerify() {
    OkGo.<JSONObject>post(Api.UPLOAD_IMAGE_URL)
        .tag(this)
        .params("image", new File(PictureUtils.compressImage(mUpCardPath,TEMP_PIC_PATH,COMPRESS_PIC_QUALITY)))
        .execute(new DialogCallback<JSONObject>(mContext) {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              final String frontCert = response.body().getJSONObject("data").getString("src");
              OkGo.<JSONObject>post(Api.UPLOAD_IMAGE_URL)
                  .tag(this)
                  .params("image",new File(PictureUtils.compressImage(mDownCardPath,TEMP_PIC_PATH,COMPRESS_PIC_QUALITY)))
                  .execute(new DialogCallback<JSONObject>(mContext) {
                    @Override public void onSuccess(Response<JSONObject> response) {
                      try {
                        String backCert = response.body().getJSONObject("data").getString("src");
                        OkGo.<HttpResult<Void>>post(Api.MOBILE_CLIENT_MEMBER_DOCUMENT)
                            .tag(this)
                            .params("name",name)
                            .params("province_id",provinceId)
                            .params("city_id",cityId)
                            .params("district_id",regionId)
                            .params("number",cardNumber)
                            .params("front_cert",frontCert)
                            .params("back_cert",backCert)
                            .execute(new JsonCallback<HttpResult<Void>>() {
                              @Override public void onSuccess(Response<HttpResult<Void>> response) {
                                ActivityUtils.startActivity(CVerify3StepActivity.class);
                              }
                            });
                      } catch (JSONException e) {
                        e.printStackTrace();
                      }
                    }
                  });
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
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
      btnCommit.setEnabled(true);
    }
  }
}
