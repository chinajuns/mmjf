package com.ysjr.mmjf.module.customer.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.base.BaseFragment;
import com.ysjr.mmjf.entity.CHomeMap;
import com.ysjr.mmjf.entity.CHomeTopBean;
import com.ysjr.mmjf.entity.CLoanSearchBean;
import com.ysjr.mmjf.entity.HomeFilterItem;
import com.ysjr.mmjf.entity.HttpResult;
import com.ysjr.mmjf.entity.Manager;
import com.ysjr.mmjf.http.DialogCallback;
import com.ysjr.mmjf.http.JsonCallback;
import com.ysjr.mmjf.module.CustomerMainFragment;
import com.ysjr.mmjf.module.customer.adapter.FilterAdapter;
import com.ysjr.mmjf.module.customer.adapter.HomeVpAdapter;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.CommonUtils;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.HttpError;
import com.zaaach.citypicker.CityPickerActivity;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017-11-28.
 */

public class CHomeFragment extends BaseFragment {
  private static final int REQUEST_CODE_PICK_CODE = 1;
  private static final String KEY_MARKER_INFO = "marker_info";
  private static final String TAG = CHomeFragment.class.getSimpleName();
  @BindView(R.id.chxFilter) CheckBox chxFilter;
  @BindView(R.id.txtCity) TextView txtCity;
  @BindView(R.id.btnFilter) ImageButton btnRightFilter;
  @BindView(R.id.tabFilter) TabLayout tabFilter;
  @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
  @BindView(R.id.btnClearFilter) Button btnClearFilter;
  @BindView(R.id.btnComplteFilter) Button btnCompleteFilter;
  @BindView(R.id.viewPager) ViewPager viewPager;
  @BindView(R.id.mapView) MapView mapView;
  @BindView(R.id.recyclerView) RecyclerView recyclerView;
  private FilterAdapter mFilterAdapter;
  private List<HomeFilterItem> mFilterList = new ArrayList<>();
  private String loan_number = "";
  private String time_limit = "";
  private String loan_day = "";
  private String way = "";
  private List<Fragment> mManagers = new ArrayList<>();
  private HomeVpAdapter mVpAdapter;
  private String city;
  private LocationClient mLocationClient;
  private BaiduMap mBaiduMap;

  private DistrictSearch mDistrictSearch;
  private Overlay mDistrictMarker;
  private boolean isSearchState;
  private String mCity = "成都市";
  private String mRegion = "0";
  private String mType = "0";
  private String mDistrictName = "";
  private List<CHomeMap.Map> mMapList = new ArrayList<>();
  private GeoCoder mGeoCoder;
  private boolean isFirstIn = true;
  List<CHomeTopBean.ValuesBean> mTopTabList;

  public static CHomeFragment newInstance() {
    CHomeFragment fg = new CHomeFragment();
    return fg;
  }

  @Override public int getLayoutId() {
    return R.layout.c_fragment_home;
  }

  @Override public void initialize(View view, Bundle savedInstanceState) {
    initLocation();
    initTopFilter();
    initFilter();
    initViewPager();
    tabFilter.post(new Runnable() {
      @Override public void run() {
        initData();
      }
    });
    drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
      @Override public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        EventBus.getDefault().post(new HomeFilterItem(CustomerMainFragment.SHOW));
        if (!isSearchState) {
          clearFilter();
        }
      }
      @Override public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        EventBus.getDefault().post(new HomeFilterItem(CustomerMainFragment.HIDE));
      }
    });
  }

  private void initTopFilter() {
    chxFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          tabFilter.setVisibility(View.VISIBLE);
        } else {
          tabFilter.setVisibility(View.GONE);
        }
      }
    });
    tabFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override public void onTabSelected(TabLayout.Tab tab) {
        if (isFirstIn && mType.equals("0")) {
          isFirstIn = false;
          return;
        }
        int position = tab.getPosition();
        mType = mTopTabList.get(position).id + "";
        httpGetRecommendManager();
      }

      @Override public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override public void onTabReselected(TabLayout.Tab tab) {

      }
    });
  }

  @Override public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (mLocationClient != null) mLocationClient.stop();
    if (mapView != null) mapView.onDestroy();
    mapView = null;
    if (mGeoCoder != null) mGeoCoder.destroy();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) return;
    switch (requestCode) {
      case REQUEST_CODE_PICK_CODE:
        city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
        txtCity.setText(city);
        if (!mCity.equals(city)) {
          clearSearchState();
        } else {
          return;
        }
        httpGetRecommendManager();
        //刷新贷款页
        EventBus.getDefault().post(new CLoanSearchBean.TypeBean.ValuesBean(city));
        GeoCodeOption geoOption = new GeoCodeOption().city(city).address(city);
        mGeoCoder.geocode(geoOption);
        break;
    }
  }

  /**
   * 清空顶部和侧边筛选条件
   */
  private void clearSearchState() {
    mCity = city;
    mRegion = "0";
    mType = "0";
    tabFilter.setScrollPosition(0,0,true);
    clearFilter();
  }

  private void initData() {
    httpGetFilterData();
    httpGetTopConfig();
  }

  private void httpGetTopConfig() {
    OkGo.<HttpResult<List<CHomeTopBean>>>get(Api.MOBILE_CLIENT_TOP_CONFIG).tag(mContext)
        .execute(new JsonCallback<HttpResult<List<CHomeTopBean>>>() {
          @Override public void onSuccess(Response<HttpResult<List<CHomeTopBean>>> response) {
            if (tabFilter != null) {
              tabFilter.removeAllTabs();
              mTopTabList = response.body().data.get(0).values;
              mTopTabList.add(0, new CHomeTopBean.ValuesBean("全部"));
              for (CHomeTopBean.ValuesBean bean : mTopTabList) {
                tabFilter.addTab(tabFilter.newTab().setText(bean.name));
              }
              CommonUtils.reflex(tabFilter);
            }
          }
        });
  }

  private void httpGetFilterData() {
    OkGo.<HttpResult<List<HomeFilterItem>>>get(Api.HOME_FILTER_DATA).tag(this)
        .execute(new JsonCallback<HttpResult<List<HomeFilterItem>>>() {
          @Override public void onSuccess(Response<HttpResult<List<HomeFilterItem>>> response) {
            mFilterList.clear();
            mFilterList.addAll(response.body().data);
            mFilterAdapter.notifyDataSetChanged();
          }
        });
  }

  private void httpGetRecommendManager() {
    OkGo.<HttpResult<CHomeMap>>post(Api.MOBILE_CLIENT_MAP).tag(mContext)
        .params("city", mCity)
        .params("type", mType)
        .params("region", mRegion)
        .params("loan_number", loan_number)
        .params("time_limit", time_limit)
        .params("loan_day", loan_day)
        .params("way", way)
        .execute(new DialogCallback<HttpResult<CHomeMap>>(mContext) {
          @Override public void onSuccess(Response<HttpResult<CHomeMap>> response) {
            CHomeMap homeMap = response.body().data;
            List<Manager> managers = homeMap.list.data;
            mManagers.clear();
            if (managers != null) {
              for (Manager manager : managers) {
                CRecommendManagerFragment fg = CRecommendManagerFragment.newInstance(manager);
                mManagers.add(fg);
              }
              mVpAdapter.notifyDataSetChanged();
              if (mManagers.size() > 1) {
              if (viewPager != null) viewPager.setCurrentItem(1);
              }
            }

            if (mRegion.equals("0")) {
              mMapList = homeMap.map;
            }

            initOverlay();
          }

          @Override public void onError(Response<HttpResult<CHomeMap>> response) {
            super.onError(response);
            String errorStatus = null;
            if (response.getException() != null) {
              errorStatus = response.getException().getMessage();
            }
            if (HttpError.EMPTY.equals(errorStatus)) {
              mManagers.clear();
              mBaiduMap.clear();
              mVpAdapter.notifyDataSetChanged();
              SnackbarUtils.with(tabFilter)
                  .setDuration(2000)
                  .setBgColor(Color.WHITE)
                  .setMessageColor(Color.BLACK)
                  .setMessage("没有符合条件的信贷经理")
                  .setBottomMargin(SizeUtils.dp2px(55))
                  .show();
            }
          }
        });
  }

  /**
   * 初始化百度地图定位
   */
  private void initLocation() {
    mGeoCoder = GeoCoder.newInstance();
    mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
      @Override public void onGetGeoCodeResult(GeoCodeResult result) {
        Log.e(TAG, "address = " + result.getLocation());
        LatLng latLng = result.getLocation();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(11.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
      }

      @Override public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

      }
    });

    mBaiduMap = mapView.getMap();
    //开启定位图层
    mBaiduMap.setMyLocationEnabled(true);

    checkIfLocSuccess();

    mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
      @Override public boolean onMarkerClick(Marker marker) {
        Log.e("MarkerPosition", marker.getPosition().toString());
        Bundle info = marker.getExtraInfo();
        if (info != null) {
          CHomeMap.Map map = (CHomeMap.Map) info.getSerializable(KEY_MARKER_INFO);
          mRegion = map.id + "";
          mDistrictName = map.name;
          httpGetRecommendManager();
        }
        return true;
      }
    });
    mDistrictSearch = DistrictSearch.newInstance();
    mDistrictSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener() {
      @Override public void onGetDistrictResult(DistrictResult result) {
        if (mDistrictMarker != null) {
          mDistrictMarker.remove();
        }
        if (result == null) {
          return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
          List<List<LatLng>> polyLines = result.getPolylines();
          if (polyLines == null) {
            return;
          }
          LatLngBounds.Builder builder = new LatLngBounds.Builder();
          for (List<LatLng> polyline : polyLines) {
            OverlayOptions ooPolyline11 =
                new PolylineOptions().width(SizeUtils.dp2px(1.0f)).points(polyline).color(Color.RED);
            mBaiduMap.addOverlay(ooPolyline11);
            OverlayOptions ooPolygon = new PolygonOptions().points(polyline)
                .stroke(new Stroke(SizeUtils.dp2px(1.0f), 0xff3333))
                .fillColor(0xaaebc3c6);
            mDistrictMarker = mBaiduMap.addOverlay(ooPolygon);
            for (LatLng latLng : polyline) {
              builder.include(latLng);
            }
          }
          mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
        }
      }
    });
  }

  private void checkIfLocSuccess() {
    String cityName = SPUtils.getInstance().getString(Constant.KEY_LOC_CITY);
    if (TextUtils.isEmpty(cityName)) {
      txtCity.setClickable(false);
      txtCity.setText("定位中...");
      mLocationClient = new LocationClient(mContext.getApplicationContext());
      LocationClientOption option = new LocationClientOption();
      option.setOpenGps(true);
      option.setIsNeedAddress(true);
      mLocationClient.setLocOption(option);
      mLocationClient.registerLocationListener(mLocListener);
      mLocationClient.start();
    } else {
      city = cityName;
      mCity = cityName;
      txtCity.setText(cityName);
      float lat = Float.parseFloat(SPUtils.getInstance().getString(Constant.KEY_M_LAT));
      float lng = Float.parseFloat(SPUtils.getInstance().getString(Constant.KEY_M_LNG));
      LatLng latLng = new LatLng(lat, lng);
      final MapStatus.Builder builder = new MapStatus.Builder();
      builder.target(latLng).zoom(12.0f);
      mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
      httpGetRecommendManager();
    }
  }

  private void initOverlay() {
    mBaiduMap.clear();
    if (mMapList == null || mMapList.size() == 0) {
      return;
    }
    for (CHomeMap.Map map : mMapList) {
      if (map.counts == 0) {
        continue;
      }
      View view = LayoutInflater.from(mContext).inflate(R.layout.overlay, null);
      TextView tvName = view.findViewById(R.id.tvName);
      TextView tvNumber = view.findViewById(R.id.tvNumber);
      tvName.setText(map.name);
      tvNumber.setText(map.counts + "位");
      if (mRegion.equals(map.id + "")) {
        view.setBackgroundResource(R.drawable.yixuan);
      }
      BitmapDescriptor bd = BitmapDescriptorFactory.fromView(view);
      MarkerOptions markerOptions =
          new MarkerOptions().position(new LatLng(map.lat, map.lng)).icon(bd);
      Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
      Bundle bundle = new Bundle();
      bundle.putSerializable(KEY_MARKER_INFO, map);
      marker.setExtraInfo(bundle);
    }
    if (!mRegion.equals("0")) {
      mDistrictSearch.searchDistrict(
          new DistrictSearchOption().cityName(mCity).districtName(mDistrictName));
    }
  }

  /**
   * 底部推荐
   */
  private void initViewPager() {
    viewPager.setPageMargin(SizeUtils.dp2px(2));
    viewPager.setOffscreenPageLimit(3);
    mVpAdapter = new HomeVpAdapter(getChildFragmentManager(), mManagers);
    viewPager.setAdapter(mVpAdapter);
    viewPager.setPageTransformer(false, new ScaleTransformer());
  }

  /**
   * 侧滑筛选
   */
  private void initFilter() {
    mFilterAdapter = new FilterAdapter(R.layout.c_home_filter_item, mFilterList);
    recyclerView.setAdapter(mFilterAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
  }

  @OnClick(R.id.btnFilter) void filter() {
    if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
      drawerLayout.closeDrawer(Gravity.RIGHT);
    } else {
      drawerLayout.openDrawer(Gravity.RIGHT);
    }
  }

  /**
   * 清空侧边筛选条件
   */
  @OnClick(R.id.btnClearFilter) void onClearFilterClicked() {
    for (HomeFilterItem item : mFilterList) {
      for (HomeFilterItem.FilterItem childItem : item.values) {
        childItem.isChecked = false;
      }
    }
    mFilterAdapter.notifyDataSetChanged();
    time_limit = "";
    loan_day = "";
    way = "";
    loan_number = "";
    isSearchState = false;
    drawerLayout.closeDrawer(Gravity.RIGHT);
    httpGetRecommendManager();
  }

  private void clearFilter() {
    for (HomeFilterItem item : mFilterList) {
      for (HomeFilterItem.FilterItem childItem : item.values) {
        childItem.isChecked = false;
      }
    }
    mFilterAdapter.notifyDataSetChanged();
    time_limit = "";
    loan_day = "";
    way = "";
    loan_number = "";
  }
  @OnClick(R.id.btnComplteFilter) void completeFilter() {
    for (HomeFilterItem item : mFilterList) {
      for (HomeFilterItem.FilterItem childItem : item.values) {
        if (childItem.isChecked) {
          switch (item.id) {
            case 9:
              loan_number = childItem.id;
              break;
            case 6:
              time_limit = childItem.id;
              break;
            case 2:
              loan_day = childItem.id;
              break;
            case 1:
              way = childItem.id;
              break;
          }
        }
      }
    }
    drawerLayout.closeDrawer(Gravity.RIGHT);
    isSearchState = true;
    httpGetRecommendManager();
  }

  @OnClick(R.id.txtCity) public void onViewClicked() {
    Intent intent = new Intent(mContext, CityPickerActivity.class);
    intent.putExtra(CityPickerActivity.KEY_CITY_NAME, city);
    startActivityForResult(intent, REQUEST_CODE_PICK_CODE);
  }

  private BDLocationListener mLocListener = new BDLocationListener() {
    @Override public void onReceiveLocation(BDLocation location) {
      if (location == null || mapView == null) {
        return;
      }
      if (location.getLocType() == 61 || location.getLocType() == 161) {
        city = location.getCity();
        mCity = city;
        txtCity.setText(city);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(12.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        EventBus.getDefault().post(new CLoanSearchBean.TypeBean.ValuesBean(city));
      } else {
        txtCity.setText(getString(R.string.locate_fail));
      }
      txtCity.setClickable(true);
      httpGetRecommendManager();
    }
  };
}
