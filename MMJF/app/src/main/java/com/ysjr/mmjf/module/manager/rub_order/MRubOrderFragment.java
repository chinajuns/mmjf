package com.ysjr.mmjf.module.manager.rub_order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import butterknife.BindView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.CLoanSearchBean;
import com.ysjr.mmjf.entity.ConfigBean;
import com.ysjr.mmjf.entity.Customer;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.JsonBean;
import com.ysjr.mmjf.entity.MRubSuccess;
import com.ysjr.mmjf.entity.Order;
import com.ysjr.mmjf.entity.Success;
import com.ysjr.mmjf.entity.User;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.DDCLoanSearch1Adapter;
import com.ysjr.mmjf.module.customer.adapter.DDCLoanSearch2Adapter;
import com.ysjr.mmjf.module.customer.adapter.DropDownTabAdapter;
import com.ysjr.mmjf.module.manager.VerifyDialogFragment;
import com.ysjr.mmjf.module.manager.adapter.RubOrderAdapter;
import com.ysjr.mmjf.module.manager.cus_order.MCusOrderActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.HttpError;
import com.yyydjk.library.DropDownMenu;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-12-8.
 */

public class MRubOrderFragment extends BaseFragment {
  private static final int REFRESHING = 5;
  private static final int LOAD_MORE = 6;
  private static final String TAG = MRubOrderFragment.class.getSimpleName();
  private static final String KEY_CITY_NAME = "city_name";
  @BindView(R.id.dropDownMenu) DropDownMenu dropDownMenu;
  private SmartRefreshLayout refreshLayout;
  private LoadingLayout loadingLayout;
  private RecyclerView contentRv;
  private DropDownTabAdapter mTabOneAdapter;
  private DDCLoanSearch2Adapter mTabRegionAdapter;
  private DDCLoanSearch1Adapter mTabThreeAdapter;
  private DDCLoanSearch1Adapter mTabFourAdapter;
  private List<ConfigBean.ValuesBean> mConfigOneList = new ArrayList<>();
  private List<JsonBean.CityBean.DistrictBean> mRegionList = new ArrayList<>();
  private List<CLoanSearchBean.TypeBean.ValuesBean> mConfigThreeList = new ArrayList<>();
  private List<CLoanSearchBean.TypeBean.ValuesBean> mConfigFourList = new ArrayList<>();
  private List<String> mTabTextList = new ArrayList<>();
  private List<View> mPopuViews = new ArrayList<>();
  private RubOrderAdapter mContentAdapter;
  private List<Customer> mContentList = new ArrayList<>();
  private View contentView;
  private RubPayDialogFragment mRubPayFragment;
  private RubScoreDialogFragment mRubScoreFragment;
  private RubPaySuccessDialogFragment mRubPaySuccessFragment;
  private String region_id = "";
  private String is_vip = "";
  private String has_house = "";
  private String has_car = "";
  private long create_time;
  private String mCityName = "";

  public static MRubOrderFragment newInstance() {
    MRubOrderFragment fg = new MRubOrderFragment();
    return fg;
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
  }

  @Override public int getLayoutId() {
    return R.layout.m_fragment_rub_order;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      mCityName = savedInstanceState.getString(KEY_CITY_NAME);
    }
    EventBus.getDefault().register(this);
    setDropDownMenu();
    initRefresh();
    initData();
  }

  @Override public void onResume() {
    super.onResume();
    if (!TextUtils.isEmpty(mCityName)) {
      httpGetRubData(REFRESHING);
    }
  }


  @Override public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_CITY_NAME,mCityName);
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void rubSuccessed(MRubSuccess rubSuccess) {
    mRubPaySuccessFragment = RubPaySuccessDialogFragment.newInstance(rubSuccess.score);
    mRubPaySuccessFragment.show(getChildFragmentManager(),"success_dialog");
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void successClicked(Boolean isSuccess) {
      mRubPaySuccessFragment.dismiss();
      Intent intent = new Intent(mContext, MCusOrderActivity.class);
      intent.putExtra(MCusOrderActivity.KEY_REFER, "junk");
      ActivityUtils.startActivity(intent);
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void onLocSuccess(JsonBean.CityBean bean) {
    mCityName = bean.getName();
    httpGetRegionData(bean.getName());
    httpGetRubData(REFRESHING);
  }

  private void initData() {
    loadingLayout.setEmptyText(getString(R.string.no_filter_data))
        .setEmptyImage(R.drawable.ic_filter_empty);
    httpGetGrabConfig();
    mCityName = SPUtils.getInstance().getString(Constant.KEY_LOC_CITY);
    if (!TextUtils.isEmpty(mCityName)) {
      httpGetRegionData(mCityName);
      httpGetRubData(REFRESHING);
    }
  }

  private void httpGetGrabConfig() {
    OkGo.<HttpResult<List<CLoanSearchBean.TypeBean>>>get(Api.MOBILE_BUSINESS_ORDER_GRAB_CONFIG)
        .tag(mContext)
        .execute(new JsonCallback<HttpResult<List<CLoanSearchBean.TypeBean>>>() {
          @Override
          public void onSuccess(Response<HttpResult<List<CLoanSearchBean.TypeBean>>> response) {
            mConfigThreeList.clear();
            mConfigFourList.clear();
            List<CLoanSearchBean.TypeBean> typeBeans = response.body().data;
            for (CLoanSearchBean.TypeBean bean : typeBeans) {
              if (bean.id == 25) {
                mConfigThreeList.addAll(bean.values);
                mConfigThreeList.add(0, new CLoanSearchBean.TypeBean.ValuesBean("0", "不限"));
              } else if (bean.id == 26) {
                mConfigFourList.addAll(bean.values);
                mConfigFourList.add(0, new CLoanSearchBean.TypeBean.ValuesBean("0", "不限"));
              }
            }
          }
        });
  }

  private void initRefresh() {
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        create_time = 0;
        refreshlayout.setLoadmoreFinished(false);
        httpGetRubData(REFRESHING);
      }
    });
    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        create_time = mContentList.get(mContentList.size() - 1).create_time;
        httpGetRubData(LOAD_MORE);
      }
    });
  }

  private void httpGetRubData(final int refreshType) {
    PostRequest<HttpResult<List<Customer>>> request =
        OkGo.<HttpResult<List<Customer>>>post(Api.MOBILE_BUSINESS_ORDER_INDEX).tag(mContext);
    if (refreshType == REFRESHING) {
      request
          .params("city_name",mCityName)
          .params("is_vip", is_vip)
          .params("region_id", region_id)
          .params("has_house", has_house)
          .params("has_car", has_car);
    } else {
      request
          .params("city_name",mCityName)
          .params("is_vip", is_vip)
          .params("region_id", region_id)
          .params("has_house", has_house)
          .params("has_car", has_car)
          .params("create_time", create_time);
    }
    Log.e(TAG, request.getParams().toString());
    request.execute(new JsonCallback<HttpResult<List<Customer>>>() {
      @Override public void onSuccess(Response<HttpResult<List<Customer>>> response) {
        loadingLayout.setStatus(LoadingLayout.Success);
        List<Customer> customerList = response.body().data;
        if (customerList == null) return;
        switch (refreshType) {
          case REFRESHING:
            refreshLayout.finishRefresh();
            mContentList.clear();
            break;
          case LOAD_MORE:
            refreshLayout.finishLoadmore();
            break;
        }
        mContentList.addAll(customerList);
        mContentAdapter.notifyDataSetChanged();
      }

      @Override public void onError(Response<HttpResult<List<Customer>>> response) {
        super.onError(response);
        String errorStatus = null;
        if (response.getException() != null) {
          errorStatus = response.getException().getMessage();
        }
        if (HttpError.EMPTY.equals(errorStatus)) {
          switch (refreshType) {
            case REFRESHING:
              refreshLayout.finishRefresh();
              loadingLayout.setStatus(LoadingLayout.Empty);
              break;
            case LOAD_MORE:
              refreshLayout.finishLoadmore();
              refreshLayout.setLoadmoreFinished(true);
              break;
          }
        }
      }
    });
  }

  private void httpGetRegionData(String city) {
    OkGo.<HttpResult<JsonBean.CityBean>>post(Api.MOBILE_CLIENT_LOAN_REGION).tag(this)
        .params("name", city)
        .execute(new JsonCallback<HttpResult<JsonBean.CityBean>>() {
          @Override public void onSuccess(Response<HttpResult<JsonBean.CityBean>> response) {
            mRegionList.clear();
            mRegionList.addAll(response.body().data.getDistrict());
            mRegionList.add(0, new JsonBean.CityBean.DistrictBean("不限", ""));
            mTabRegionAdapter.notifyDataSetChanged();
          }
        });
  }

  private void setDropDownMenu() {
    initContent();
    initPopupViews();
    mTabTextList.add("订单类型");
    mTabTextList.add("城市");
    mTabTextList.add("车产情况");
    mTabTextList.add("房产情况");
    dropDownMenu.setDropDownMenu(mTabTextList, mPopuViews, contentView);
  }

  private void initPopupViews() {
    mConfigOneList.add(new ConfigBean.ValuesBean("", "全部订单"));
    mConfigOneList.add(new ConfigBean.ValuesBean("1", "会员订单"));
    mConfigOneList.add(new ConfigBean.ValuesBean("0", "普通订单"));
    mTabOneAdapter = new DropDownTabAdapter(R.layout.drop_down_base, mConfigOneList);
    mTabRegionAdapter = new DDCLoanSearch2Adapter(R.layout.drop_down_base, mRegionList);
    mTabThreeAdapter = new DDCLoanSearch1Adapter(R.layout.drop_down_base, mConfigThreeList);
    mTabFourAdapter = new DDCLoanSearch1Adapter(R.layout.drop_down_base, mConfigFourList);
    RecyclerView rvTabOne = new RecyclerView(mContext);
    RecyclerView rvTabTwo = new RecyclerView(mContext);
    RecyclerView rvTabThree = new RecyclerView(mContext);
    RecyclerView rvTabFour = new RecyclerView(mContext);
    mPopuViews.add(rvTabOne);
    mPopuViews.add(rvTabTwo);
    mPopuViews.add(rvTabThree);
    mPopuViews.add(rvTabFour);
    rvTabOne.setAdapter(mTabOneAdapter);
    rvTabOne.setLayoutManager(new LinearLayoutManager(mContext));
    rvTabTwo.setAdapter(mTabRegionAdapter);
    rvTabTwo.setLayoutManager(new LinearLayoutManager(mContext));
    rvTabThree.setAdapter(mTabThreeAdapter);
    rvTabThree.setLayoutManager(new LinearLayoutManager(mContext));
    rvTabFour.setAdapter(mTabFourAdapter);
    rvTabFour.setLayoutManager(new LinearLayoutManager(mContext));

    mTabOneAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (ConfigBean.ValuesBean bean : mConfigOneList) {
          bean.isChecked = false;
        }
        ConfigBean.ValuesBean bean = mConfigOneList.get(position);
        bean.isChecked = true;
        mTabOneAdapter.notifyDataSetChanged();
        dropDownMenu.setTabText(bean.name);
        dropDownMenu.closeMenu();
        is_vip = bean.id;
        httpGetRubData(REFRESHING);
      }
    });

    mTabRegionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (JsonBean.CityBean.DistrictBean bean : mRegionList) {
          bean.isChecked = false;
        }
        JsonBean.CityBean.DistrictBean bean = mRegionList.get(position);
        bean.isChecked = true;
        region_id = bean.getId();
        mTabRegionAdapter.notifyDataSetChanged();
        dropDownMenu.setTabText(bean.getName());
        dropDownMenu.closeMenu();
        httpGetRubData(REFRESHING);
      }
    });

    mTabThreeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (CLoanSearchBean.TypeBean.ValuesBean bean : mConfigThreeList) {
          bean.isChecked = false;
        }
        CLoanSearchBean.TypeBean.ValuesBean bean = mConfigThreeList.get(position);
        bean.isChecked = true;
        mTabThreeAdapter.notifyDataSetChanged();
        dropDownMenu.setTabText(bean.attr_value);
        dropDownMenu.closeMenu();
        has_house = bean.id;
        httpGetRubData(REFRESHING);
      }
    });
    mTabFourAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (CLoanSearchBean.TypeBean.ValuesBean bean : mConfigFourList) {
          bean.isChecked = false;
        }
        CLoanSearchBean.TypeBean.ValuesBean bean = mConfigFourList.get(position);
        bean.isChecked = true;
        mTabFourAdapter.notifyDataSetChanged();
        dropDownMenu.setTabText(bean.attr_value);
        dropDownMenu.closeMenu();
        has_car = bean.id;
        httpGetRubData(REFRESHING);
      }
    });
  }

  private void initContent() {
    contentView = LayoutInflater.from(mContext).inflate(R.layout.drop_down_content, null);
    contentRv = contentView.findViewById(R.id.recyclerView);
    refreshLayout = contentView.findViewById(R.id.refreshLayout);
    loadingLayout = contentView.findViewById(R.id.loadingLayout);
    mContentAdapter = new RubOrderAdapter(R.layout.m_rub_order_item, mContentList);
    contentRv.setAdapter(mContentAdapter);
    contentRv.setLayoutManager(new LinearLayoutManager(mContext));
    mContentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override
      public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        User user = DataSupport.findFirst(User.class);
        Customer customer =mContentList.get(position);
        if (user.is_auth == 1) {
          VerifyDialogFragment verifyDialogFragment = VerifyDialogFragment.newInstance();
          verifyDialogFragment.show(getChildFragmentManager(), "dialog");
        } else if (user.is_auth == 2) {
          ToastUtils.setBgColor(Color.BLACK);
          ToastUtils.showShort(getString(R.string.verifying));
        } else if (user.is_auth == 4) {
          ToastUtils.setBgColor(Color.BLACK);
          ToastUtils.showShort(getString(R.string.verify_fail));
        } else {
          OkGo.<JSONObject>post(Api.MOBILE_BUSINESS_ORDER_CHECK_PURCHASE)
              .params("id",customer.id)
              .tag(mContext)
              .execute(new JsonCallback<JSONObject>() {
                @Override public void onSuccess(Response<JSONObject> response) {
                  try {
                    int myScore = response.body().getJSONObject("data").getInt("my_score");
                    int rubScore = mContentList.get(position).score;
                    int isMine = response.body().getJSONObject("data").getInt("is_mine");
                    if (isMine == 1) {//自己的单子
                      ToastUtils.setGravity(Gravity.CENTER, 0, 0);
                      ToastUtils.showShort("不能抢自己的甩单");
                      return;
                    }
                    //不是自己的单子
                    if (myScore >= rubScore) {
                      mRubPayFragment =
                          RubPayDialogFragment.newInstance(rubScore, mContentList.get(position).id);
                      mRubPayFragment.show(getChildFragmentManager(), "pay_dialog");
                    } else {
                      mRubScoreFragment = RubScoreDialogFragment.newInstance();
                      mRubScoreFragment.show(getChildFragmentManager(), "score_dialog");
                    }
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
              });
        }
      }
    });
    mContentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, MCusDetailActivity.class);
        intent.putExtra(MCusDetailActivity.KEY_CUS_ID, mContentList.get(position).id);
        ActivityUtils.startActivity(intent);
      }
    });
  }


}
