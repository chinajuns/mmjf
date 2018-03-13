package com.ysjr.mmjf.base;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.weavey.loading.lib.LoadingLayout;
import com.ysjr.mmjf.R;
import com.ysjr.mmjf.utils.Api;
import com.ysjr.mmjf.utils.Constant;
import com.ysjr.mmjf.utils.CrashHandler;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import okhttp3.OkHttpClient;
import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2017-11-16.
 */

public class BaseApplication extends LitePalApplication {
  private static final int CONNECT_TIME_OUT = 30;
  private static final long CACHE_TIME = 7 * 24 * 60 * 60 * 1000;//七天缓存时间
  public static BaseApplication mInstance;

  public static String token;
  public static String deviceid;

  static {
    //设置全局的Header构建器
    SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
      @Override
      public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
        layout.setPrimaryColorsId(R.color.bg_white_color, android.R.color.black);//全局设置主题颜色
        return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
      }
    });
    //设置全局的Footer构建器
    SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
      @Override
      public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
        //指定为经典Footer，默认是 BallPulseFooter
        return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
      }
    });
  }

  @Override public void onCreate() {
    super.onCreate();



    mInstance = this;
    JPushInterface.init(this);
    JPushInterface.setDebugMode(true);
    LitePal.initialize(this);
    //        初始化工具类AndroidUtilCode
    Utils.init(this);
    String rootApi = SPUtils.getInstance().getString(Constant.ROOT_API_KEY);
    String rootImageApi = SPUtils.getInstance().getString(Constant.ROOT_IMAGE_API_KEY);
    if (!TextUtils.isEmpty(rootApi)) {
      Api.setApi(rootApi + "api/v1/");
    }
    if (!TextUtils.isEmpty(rootImageApi)) {
      Api.setImageApi(rootImageApi);
    }

    HttpHeaders headers = new HttpHeaders();
    String token = SPUtils.getInstance().getString(Constant.KEY_TOKEN_SP);
    String imei = SPUtils.getInstance().getString(Constant.KEY_IMEI_SP);
    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(imei)) {
      headers.put("token", token);
      headers.put("deviceid",imei );
      BaseApplication.token = token;
      BaseApplication.deviceid = imei;
      OkGo.getInstance().addCommonHeaders(headers);
    }

    //        初始化全局异常捕获工具类
          CrashHandler.getInstance().init(this);
    //        初始化网络框架
    initOkGo();
    initBaidu();
  //  umeng
    UMShareAPI.get(this);
    Config.DEBUG = true;
  //  loading
    LoadingLayout.getConfig()
        .setLoadingPageLayout(R.layout.loading_layout)
        .setEmptyImage(R.drawable.ic_filter_empty)
        .setAllTipTextSize(14);
  }

  {
    PlatformConfig.setWeixin("wx03ba1b2dd8eab537", "02fe73753d1e1d8ee3cd2f33e5022270");
    PlatformConfig.setQQZone("1106413424", "6rUbDKprrHE6D5Ry");
    PlatformConfig.setSinaWeibo("1025369079", "564180e943875b96f8a173f55e92a0e1", "https://sns.whalecloud.com/sina2/callback");
  }


  private void initBaidu() {
    SDKInitializer.initialize(this);
  }

  /**
   * 初始化网络框架
   */
  private void initOkGo() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
    loggingInterceptor.setColorLevel(Level.INFO);
    loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
    builder.readTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
    builder.writeTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
    builder.addInterceptor(loggingInterceptor);
    OkGo.getInstance().init(this).setOkHttpClient(builder.build()).setCacheTime(CACHE_TIME);
  }
}
