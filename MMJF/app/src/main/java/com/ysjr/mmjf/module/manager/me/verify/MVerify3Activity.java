package com.ysjr.mmjf.module.manager.me.verify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.TopBarBaseActivity;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.PictureUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import java.io.File;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-14.
 */

public class MVerify3Activity extends TopBarBaseActivity {
  private static final int REQUEST_CODE_IVLOGO = 4;
  private static final int REQUEST_CODE_HETONG = 3;
  private static final int REQUEST_CODE_MINGPIAN = 2;
  private static final int REQUEST_CODE_GONGPAI = 1;
  private static final int COMPRESS_PIC_QUALITY = 30;
  private static final String TEMP_PIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"temp.jpg";
  @BindView(R.id.ivGongpai) ImageView ivGongpai;
  @BindView(R.id.ivMingpian) ImageView ivMingpian;
  @BindView(R.id.ivHetong) ImageView ivHetong;
  @BindView(R.id.ivLogo) ImageView ivLogo;
  @BindView(R.id.chxProtocol) RadioButton chxProtocol;
  @BindView(R.id.btnCommit) Button btnCommit;
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
  private String mGongpaiPath = "";
  private String mHetongPath = "";
  private String mMingpianPath = "";
  private String mIvlogoPath = "";
  private String[] pictureArray =
      new String[] { mGongpaiPath, mHetongPath, mMingpianPath, mIvlogoPath };
      //0代表gongpai,1代表hetong,2代表mingpian，3代表logo
  private int index = -1;//储存上传的图片下标,依次向后
  private ProgressDialog progressDialog;
  @Override protected int getContentView() {
    return R.layout.m_activity_verify_3;
  }

  @Override protected void init(Bundle savedInstanceState) {
    setTitle(getString(R.string.verify_name));
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
    mUpCardPath = getIntent().getStringExtra("hand_idcard_front");
    mDownCardPath = getIntent().getStringExtra("hand_idcard_back");
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      return;
    }
    List<String> result = Matisse.obtainPathResult(data);
    switch (requestCode) {
      case REQUEST_CODE_GONGPAI:
        mGongpaiPath = result.get(0);
        pictureArray[0] = mGongpaiPath;
        Glide.with(mContext).load(mGongpaiPath).into(ivGongpai);
        break;
      case REQUEST_CODE_HETONG:
        mHetongPath = result.get(0);
        pictureArray[1] = mHetongPath;
        Glide.with(mContext).load(mHetongPath).into(ivHetong);
        break;
      case REQUEST_CODE_MINGPIAN:
        mMingpianPath = result.get(0);
        pictureArray[2] = mMingpianPath;
        Glide.with(mContext).load(mMingpianPath).into(ivMingpian);
        break;
      case REQUEST_CODE_IVLOGO:
        mIvlogoPath = result.get(0);
        pictureArray[3] = mIvlogoPath;
        Glide.with(mContext).load(mIvlogoPath).into(ivLogo);
        break;

    }
    switchBtnCommit();
  }

  @OnClick(R.id.ivGongpai) public void onIvGongpaiClicked() {
    Matisse.from(mContext)
        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
        .countable(true)
        .capture(true)
        .captureStrategy(new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .thumbnailScale(0.85f)
        .imageEngine(new GlideEngine())
        .forResult(REQUEST_CODE_GONGPAI);
  }

  @OnClick(R.id.ivMingpian) public void onIvMingpianClicked() {
    Matisse.from(mContext)
        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
        .countable(true)
        .capture(true)
        .captureStrategy(new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .thumbnailScale(0.85f)
        .imageEngine(new GlideEngine())
        .forResult(REQUEST_CODE_MINGPIAN);
  }

  @OnClick(R.id.ivHetong) public void onIvHetongClicked() {
    Matisse.from(mContext)
        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
        .countable(true)
        .capture(true)
        .captureStrategy(new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .thumbnailScale(0.85f)
        .imageEngine(new GlideEngine())
        .forResult(REQUEST_CODE_HETONG);
  }

  @OnClick(R.id.ivLogo) public void onIvLogoClicked() {
    Matisse.from(mContext)
        .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
        .countable(true)
        .capture(true)
        .captureStrategy(new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .thumbnailScale(0.85f)
        .imageEngine(new GlideEngine())
        .forResult(REQUEST_CODE_IVLOGO);
  }

  @OnClick(R.id.btnCommit) public void onBtnCommitClicked() {
    progressDialog = new ProgressDialog(mContext);
    progressDialog.setMessage("上传中...");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();
    OkGo.<JSONObject>post(Api.UPLOAD_IMAGE_URL).tag(this)
        .params("image", new File(PictureUtils.compressImage(mAvatarPath,TEMP_PIC_PATH,COMPRESS_PIC_QUALITY)))
        .execute(new JsonCallback<JSONObject>() {
          @Override public void onSuccess(Response<JSONObject> response) {
            try {
              mAvatarPath = response.body().getJSONObject("data").getString("src");
              OkGo.<JSONObject>post(Api.UPLOAD_IMAGE_URL).tag(this)
                  .params("image",  new File(PictureUtils.compressImage(mUpCardPath,TEMP_PIC_PATH,COMPRESS_PIC_QUALITY)))
                  .execute(new JsonCallback<JSONObject>() {
                    @Override public void onSuccess(Response<JSONObject> response) {
                      try {
                        mUpCardPath = response.body().getJSONObject("data").getString("src");
                        OkGo.<JSONObject>post(Api.UPLOAD_IMAGE_URL).tag(this)
                            .params("image",  new File(PictureUtils.compressImage(mDownCardPath,TEMP_PIC_PATH,COMPRESS_PIC_QUALITY)))
                            .execute(new JsonCallback<JSONObject>() {
                              @Override public void onSuccess(Response<JSONObject> response) {
                                try {
                                  mDownCardPath =
                                      response.body().getJSONObject("data").getString("src");
                                  uploadPicture();
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
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
  }

  private int loopTimes;

  private void uploadPicture() {
    for (int i = 0; i < pictureArray.length; i++) {
      String fileUrl = pictureArray[i];
      if (!TextUtils.isEmpty(fileUrl) && i > index) {
        index = i;
        OkGo.<JSONObject>post(Api.UPLOAD_IMAGE_URL).tag(mContext)
            .params("image",  new File(PictureUtils.compressImage(fileUrl,TEMP_PIC_PATH,COMPRESS_PIC_QUALITY)))
            .execute(new JsonCallback<JSONObject>() {
              @Override public void onSuccess(Response<JSONObject> response) {
                try {
                  final String path1 = response.body().getJSONObject("data").getString("src");
                  switch (index) {
                    case 0:
                      mGongpaiPath = path1;
                      break;
                    case 1:
                      mHetongPath = path1;
                      break;
                    case 2:
                      mMingpianPath = path1;
                      break;
                    case 3:
                      mIvlogoPath = path1;
                      break;
                  }
                  loopTimes++;
                  if (loopTimes == selectedNum) {
                    commit();
                  } else {
                    uploadPicture();
                  }
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            });
        break;
      }
    }
  }

  private void commit() {
    OkGo.<HttpResult<Void>>post(Api.MOBILE_BUSINESS_MANAGER_SUBMIT_PROFILE).tag(mContext)
        .params("header_img", mAvatarPath)
        .params("front_cert", mUpCardPath)
        .params("back_cert", mDownCardPath)
        .params("card", mGongpaiPath)
        .params("work_card", mMingpianPath)
        .params("contract_page", mHetongPath)
        .params("logo_personal", mIvlogoPath)
        .params("true_name", mUserName)
        .params("cert_number", mIdCard)
        .params("mechanism_type", mAgentType)
        .params("mechanism", mAgentName)
        .params("department", mDepartment)
        .params("province_id", mProvinceId)
        .params("city_id", mCityId)
        .params("region_id", mRegionId)
        .params("lat", SPUtils.getInstance().getString(Constant.KEY_M_LAT))
        .params("lng", SPUtils.getInstance().getString(Constant.KEY_M_LNG))
        .execute(new DialogCallback<HttpResult<Void>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<Void>> response) {
            progressDialog.dismiss();
            ActivityUtils.startActivity(MVerify4Activity.class);
          }
        });
  }

  private int selectedNum;

  private void switchBtnCommit() {
    selectedNum = 0;
    for (String path : pictureArray) {
      if (!TextUtils.isEmpty(path)) {
        selectedNum++;
      }
    }
    if (selectedNum >= 2) {
      btnCommit.setEnabled(true);
    } else {
      btnCommit.setEnabled(false);
    }
  }
}
