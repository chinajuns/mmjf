package com.ysjr.mmjf.module.customer.loan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import butterknife.BindView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.CLoanSearchBean;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.JsonBean;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.entity.ManagerWrapBean;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.customer.adapter.DDCLoanSearch1Adapter;
import com.ysjr.mmjf.module.customer.adapter.DDCLoanSearch2Adapter;
import com.ysjr.mmjf.module.customer.adapter.DDCLoanSearch3Adapter;
import com.ysjr.mmjf.module.customer.adapter.LoanContentAdapter;
import com.ysjr.mmjf.module.customer.store.CStoreActivity;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.HttpError;
import com.yyydjk.library.DropDownMenu;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017-11-28.
 */

public class CLoanFragment extends BaseFragment {
  public static final int REFRESHING = 1;
  public static final int LOAD_MORE = 2;
  @BindView(R.id.dropDownMenu) DropDownMenu dropDownMenu;
  private SmartRefreshLayout refreshLayout;
  private RecyclerView recyclerView;
  private LoadingLayout loadingLayout;
  private DDCLoanSearch1Adapter mTabTypeAdapter;
  private DDCLoanSearch2Adapter mTabRegionAdapter;
  private DDCLoanSearch3Adapter mTabFocusAdapter;
  private LoanContentAdapter mContentAdapter;
  private List<CLoanSearchBean.TypeBean.ValuesBean> mSearchTypeList = new ArrayList<>();
  private List<JsonBean.CityBean.DistrictBean> mSearchRegionList = new ArrayList<>();
  private List<CLoanSearchBean.FocusBean.ValuesBeanX> mSearchFocusList = new ArrayList<>();
  private List<Manager> mManagerList = new ArrayList<>();
  private List<String> mTabTextList = new ArrayList<>();
  private List<View> mPopuViews = new ArrayList<>();
  private View contentView;
  private int mPageCurrent = 1;
  private String cityName;
  private String loanType ="0";
  private String focus_id = "0";
  private String region_id = "0";

  @Override public int getLayoutId() {
    return R.layout.c_fragment_loan;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    contentView = LayoutInflater.from(mContext).inflate(R.layout.drop_down_content, null);
    refreshLayout = contentView.findViewById(R.id.refreshLayout);
    recyclerView = contentView.findViewById(R.id.recyclerView);
    loadingLayout = contentView.findViewById(R.id.loadingLayout);
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(RefreshLayout refreshlayout) {
        refreshlayout.setLoadmoreFinished(false);
        httpGetLoanData(REFRESHING);
      }
    });
    refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
      @Override public void onLoadmore(RefreshLayout refreshlayout) {
        httpGetLoanData(LOAD_MORE);
      }
    });
    initPopupViews();
    initContent();
    setDropDownMenu();
    initData();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void switchCity(CLoanSearchBean.TypeBean.ValuesBean bean) {
    cityName = bean.attr_value;
    resetAll();
    httpGetRegionData(bean.attr_value);
    httpGetLoanData(REFRESHING);
  }

  private void resetAll() {
    loanType = "0";
    focus_id = "0";
    region_id = "0";
    mPageCurrent = 1;
    for (int i = 0; i < mTabTextList.size(); i++) {
      dropDownMenu.setTabText(mTabTextList.get(i),i*2);
    }
    mTabTypeAdapter.clearState();
    mTabFocusAdapter.clearState();
    mTabRegionAdapter.clearState();
  }

  private void initData() {
    loadingLayout.setStatus(LoadingLayout.Loading);
    loadingLayout.setEmptyText(getString(R.string.no_data));
    httpGetSearchData();
    String cityName = SPUtils.getInstance().getString(Constant.KEY_LOC_CITY);
    if (!TextUtils.isEmpty(cityName)) {
      this.cityName = cityName;
      resetAll();
      httpGetRegionData(cityName);
      httpGetLoanData(REFRESHING);
    }
  }

  private void httpGetRegionData(String city) {
    OkGo.<HttpResult<JsonBean.CityBean>>post(Api.MOBILE_CLIENT_LOAN_REGION).tag(this)
        .params("name", city)
        .execute(new JsonCallback<HttpResult<JsonBean.CityBean>>() {
          @Override public void onSuccess(Response<HttpResult<JsonBean.CityBean>> response) {
            mSearchRegionList.clear();
            mSearchRegionList.addAll(response.body().data.getDistrict());
            mSearchRegionList.add(0,new JsonBean.CityBean.DistrictBean("不限", "0"));
            mTabRegionAdapter.notifyDataSetChanged();
          }
        });
  }

  private void httpGetSearchData() {
    OkGo.<HttpResult<CLoanSearchBean>>get(Api.MOBILE_CLIENT_LOAN_CONFIG).tag(this)
        .execute(new JsonCallback<HttpResult<CLoanSearchBean>>() {
          @Override public void onSuccess(Response<HttpResult<CLoanSearchBean>> response) {
            mSearchTypeList.clear();
            mSearchFocusList.clear();
            mSearchTypeList.addAll(response.body().data.type.values);
            mSearchFocusList.addAll(response.body().data.focus.values);
            mSearchTypeList.add(0,new CLoanSearchBean.TypeBean.ValuesBean("0", "不限"));
            mSearchFocusList.add(0, new CLoanSearchBean.FocusBean.ValuesBeanX("不限", "0"));
            mTabTypeAdapter.notifyDataSetChanged();
            mTabFocusAdapter.notifyDataSetChanged();
          }
        });
  }

  private void httpGetLoanData(final int refreshType) {
    if (refreshType == REFRESHING) {
      mPageCurrent = 1;
      refreshLayout.setLoadmoreFinished(false);
    } else {
      mPageCurrent++;
    }
      OkGo.<HttpResult<ManagerWrapBean>>post(Api.MOBILE_CLIENT_LOAN_SEARCH)
          .tag(this)
          .params("city", cityName)
          .params("type", loanType)
          .params("focus_id", focus_id)
          .params("region_id", region_id)
          .params("page", mPageCurrent)
          .execute(new JsonCallback<HttpResult<ManagerWrapBean>>() {
            @Override public void onSuccess(Response<HttpResult<ManagerWrapBean>> response) {
              loadingLayout.setStatus(LoadingLayout.Success);
              List<Manager> beanList = response.body().data.data;
              switch (refreshType) {
                case REFRESHING:
                  mManagerList.clear();
                  refreshLayout.finishRefresh();
                  break;
                case LOAD_MORE:
                  refreshLayout.finishLoadmore();
                  break;
              }
              mManagerList.addAll(beanList);
              mContentAdapter.notifyDataSetChanged();
            }

            @Override public void onError(Response<HttpResult<ManagerWrapBean>> response) {
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

  private void setDropDownMenu() {
    mTabTextList.add("产品类型");
    mTabTextList.add("产品区域");
    mTabTextList.add("筛选");
    dropDownMenu.setDropDownMenu(mTabTextList, mPopuViews, contentView);
  }

  private void initContent() {
    mContentAdapter = new LoanContentAdapter(R.layout.recommend_manager_item, mManagerList);
    recyclerView.setAdapter(mContentAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mContentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, CStoreActivity.class);
        intent.putExtra(CStoreActivity.KEY_MANAGER_ID, mManagerList.get(position).id);
        intent.putExtra(CStoreActivity.KEY_HEADER_IMAGE, mManagerList.get(position).header_img);
        intent.putExtra(CStoreActivity.KEY_MANAGER_NAME, mManagerList.get(position).name);
        intent.putExtra(CStoreActivity.KEY_MANAGER_TAG, mManagerList.get(position).tag);
        ActivityUtils.startActivity(intent);
      }
    });
  }

  private void initPopupViews() {
    mTabTypeAdapter = new DDCLoanSearch1Adapter(R.layout.drop_down_base, mSearchTypeList);
    mTabRegionAdapter = new DDCLoanSearch2Adapter(R.layout.drop_down_base, mSearchRegionList);
    mTabFocusAdapter = new DDCLoanSearch3Adapter(R.layout.drop_down_base, mSearchFocusList);
    RecyclerView rvTabOne = new RecyclerView(mContext);
    RecyclerView rvTabTwo = new RecyclerView(mContext);
    RecyclerView rvTabThree = new RecyclerView(mContext);
    mPopuViews.add(rvTabOne);
    mPopuViews.add(rvTabTwo);
    mPopuViews.add(rvTabThree);
    rvTabOne.setAdapter(mTabTypeAdapter);
    rvTabOne.setLayoutManager(new LinearLayoutManager(mContext));
    rvTabTwo.setAdapter(mTabRegionAdapter);
    rvTabTwo.setLayoutManager(new LinearLayoutManager(mContext));
    rvTabThree.setAdapter(mTabFocusAdapter);
    rvTabThree.setLayoutManager(new LinearLayoutManager(mContext));
    mTabTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (CLoanSearchBean.TypeBean.ValuesBean bean : mSearchTypeList) {
          bean.isChecked = false;
        }
        CLoanSearchBean.TypeBean.ValuesBean bean = mSearchTypeList.get(position);
        bean.isChecked = true;
        loanType = bean.id;
        mTabTypeAdapter.notifyDataSetChanged();
        dropDownMenu.setTabText(bean.attr_value);
        dropDownMenu.closeMenu();
        httpGetLoanData(REFRESHING);
      }
    });

    mTabRegionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (JsonBean.CityBean.DistrictBean bean : mSearchRegionList) {
          bean.isChecked = false;
        }
        JsonBean.CityBean.DistrictBean bean = mSearchRegionList.get(position);
        bean.isChecked = true;
        region_id = bean.getId();
        mTabRegionAdapter.notifyDataSetChanged();
        dropDownMenu.setTabText(bean.getName());
        dropDownMenu.closeMenu();
        httpGetLoanData(REFRESHING);
      }
    });

    mTabFocusAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (CLoanSearchBean.FocusBean.ValuesBeanX beanX : mSearchFocusList) {
          beanX.isChecked = false;
        }
        CLoanSearchBean.FocusBean.ValuesBeanX beanX = mSearchFocusList.get(position);
        beanX.isChecked = true;
        focus_id = beanX.id;
        mTabFocusAdapter.notifyDataSetChanged();
        dropDownMenu.setTabText(beanX.attr_value);
        dropDownMenu.closeMenu();
        httpGetLoanData(REFRESHING);
      }
    });
  }

  public static CLoanFragment newInstance() {
    CLoanFragment fg = new CLoanFragment();
    return fg;
  }

}
