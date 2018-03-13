package com.ysjr.mmjf.module.customer.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.willy.ratingbar.ScaleRatingBar;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.module.customer.store.CStoreActivity;
import com.ysjr.mmjf.utils.Api;

/**
 * Created by Administrator on 2017-12-18.
 */

public class CRecommendManagerFragment extends BaseFragment {
  private static final String KEY_MANAGER = "manager";
  @BindView(R.id.imgCard) ImageView imgCard;
  @BindView(R.id.tvLoanType) TextView tvLoanType;
  @BindView(R.id.tvName) TextView tvName;
  @BindView(R.id.ivVerify) ImageView ivVerify;
  @BindView(R.id.check_box) CheckBox checkBox;
  @BindView(R.id.tvMaxLoan) TextView tvMaxLoan;
  @BindView(R.id.tvLoanNumber) TextView tvLoanNumber;
  @BindView(R.id.tv30Days) TextView tv30Days;
  @BindView(R.id.tvLoanDay) TextView tvLoanDay;
  //@BindView(R.id.ratingBar) AppCompatRatingBar ratingBar;
  @BindView(R.id.ratingBar) ScaleRatingBar ratingBar;
  @BindView(R.id.tvScore) TextView tvScore;
  @BindView(R.id.tvFocus) TextView tvFocus;
  @BindView(R.id.tvHot) TextView tvHot;
  private Manager mManager;
  public static CRecommendManagerFragment newInstance(Manager manager) {
    CRecommendManagerFragment fg = new CRecommendManagerFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(KEY_MANAGER, manager);
    fg.setArguments(bundle);
    return fg;
  }
  @Override public int getLayoutId() {
    return R.layout.recommend_manager_item;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    Manager manager = (Manager) getArguments().getSerializable(KEY_MANAGER);
    init(manager);
    view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(mContext, CStoreActivity.class);
        intent.putExtra(CStoreActivity.KEY_MANAGER_ID, mManager.id);
        intent.putExtra(CStoreActivity.KEY_HEADER_IMAGE, mManager.header_img);
        intent.putExtra(CStoreActivity.KEY_MANAGER_NAME, mManager.name);
        intent.putExtra(CStoreActivity.KEY_MANAGER_TAG, mManager.tag);
        ActivityUtils.startActivity(intent);
      }
    });

  }

  public void init(Manager manager) {
    mManager = manager;
    Glide.with(mContext).load(Api.IMAGE_ROOT_URL+manager.header_img).placeholder(R.drawable.touxiang).into(imgCard);
    tvLoanType.setText(manager.tag);
    tvName.setText(manager.name);
    //需要格式化金额
    tvLoanNumber.setText(""+manager.all_number+"万");
    tvMaxLoan.setText(""+manager.max_loan+"万");
    tv30Days.setText(""+manager.loan_number+"万");
    if (manager.is_auth == 3) {
      ivVerify.setImageResource(R.drawable.ic_verify);
    } else {
      ivVerify.setImageResource(R.drawable.ic_unverify);
    }
    tvLoanDay.setText(manager.loan_day+"天");
    ratingBar.setRating(manager.score);
    tvScore.setText(""+manager.score);
    if (!TextUtils.isEmpty(manager.tags)) {
      if (manager.tags.contains(",")) {
        String[] split = manager.tags.split(",");
        tvFocus.setText(split[0]);
        tvHot.setText(split[1]);
        tvFocus.setVisibility(View.VISIBLE);
        tvHot.setVisibility(View.VISIBLE);
      } else {
        tvFocus.setText(manager.tags);
        tvFocus.setVisibility(View.VISIBLE);
      }
    }
  }
}
