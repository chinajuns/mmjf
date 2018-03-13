package com.ysjr.mmjf.utils;

/**
 * Created by Administrator on 2017-11-16.
 */

public class Api {
  public static  final String NEWS_DETAIL_H5 = "http://h5.kuanjiedai.com/Article-details.html";
  public static String IMAGE_ROOT_URL;
  //登录模块
  public static String LOGIN_URL = "";
  public static String UPLOAD_IMAGE_URL = "";
  public static String GET_CODE_URL = "";
  public static String CHECK_CODE_URL = "";
  public static String REGISTER_URL= "";
  public static String TAKE_PSW= "";
  public static String RESET_PSW="";
  public static String LOGOUT="";
  public static String SET_AUTH_BIND="";
  public static String SET_AUTH="";


  //C端首页
  public static String RECOMMEND_MANAGER="";
  public static String HOME_FILTER_DATA="";
  public static String HOME_FILTER_SEARCH="";
  public static String MOBILE_CLIENT_CHECK_NOTICE="";
  public static String MOBILE_CLIENT_ARTICLE="";
  public static String MOBILE_CLIENT_ARTICLE_LIST="";
  public static String MOBILE_CLIENT_TOP_CONFIG="";
  public static String MOBILE_CLIENT_MAP="";

  //C端产品详情
  public static String C_PRODUCT_DETAIL="";
  public static String CLIENT_CONFIG="";
  public static String APPLY_LOAN="";
  public static String APPLY_SUCCESS_RECOMMEND_MANAGER="";
  public static String MOBILE_CLIENT_LOAN="";
  public static String MOBILE_CLIENT_LOAN_SEARCH="";
  public static String MOBILE_CLIENT_LOAN_CONFIG="";
  public static String MOBILE_CLIENT_LOAN_REGION ="";
  public static String MOBILE_CLIENT_MESSAGE_TYPE="";
  public static String MOBILE_CLIENT_QUICK_APPLY ="";
  public static String MOBILE_CLIENT_MEMBER_INVITE_INFO ="";
  public static String WEB_GET_QRCODE ="";
  //店铺="
  public static String MOBILE_CLIENT_EVALUATE    ="";
  public static String MOBILE_CLIENT_AVERAGE     ="";
  //个人中心
  public static String MOBILE_CLIENT_MEMBER_POINT        ="";
  public static String MOBILE_CLIENT_MEMBER_POINTLIST    ="";
  public static String MOBILE_CLIENT_USER_AVATAR         ="";
  public static String MOBILE_CLIENT_MEMBER_FEEDBACK     ="";
  public static String MOBILE_CLIENT_MEMBER_DOCUMENT     ="";
  public static String MOBILE_CLIENT_MEMBER_AUTH_DOCUMENT="";
  public static String MOBILE_CLIENT_MEMBER_HISTORY      ="";
  public static String MOBILE_CLIENT_REPORT              ="";
  public static String MOBILE_CLIENT_USER_SCORE_TYPE     ="";
  public static String MOBILE_CLIENT_USER_ADD_SCORE      ="";
  public static String MOBILE_CLIENT_CHECK_FAVOURITE     ="";
  public static String MOBILE_CLIENT_MEMBER_SET_FAVORITE ="";
  public static String MOBILE_CLIENT_MEMBER_SET_FAVORITE_LIST = "";
  public static String CLIENT_MEMBER_OAUTH_BIND = "";
  public static String MOBILE_CLIENT_MEMBER_SET_OAUTH_BIND = "";
  public static String MOBILE_CLIENT_MEMBER_GET_PUSH_STATUS = "";
  public static String MOBILE_CLIENT_MEMBER_SET_PUSH_STATUS = "";

  //消息
  public static String MOBILE_CLIENT_MESSAGE_SET_READ="";

  //B端
  public static String MOBILE_BUSINESS_INDEX                 ="";
  public static String MOBILE_BUSINESS_ORDER_INDEX           ="";
  public static String MOBILE_BUSINESS_ORDER_JUNK_PUBLISH    ="";
  public static String MOBILE_BUSINESS_ORDER_DETAIL          ="";
  public static String MOBILE_BUSINESS_ORDER_CHECK_PURCHASE  ="";
  public static String MOBILE_BUSINESS_ORDER_GRAB_CONFIG  ="";

  public static String MOBILE_BUSINESS_ORDER_PURCHASE        ="";
  public static String MOBILE_BUSINESS_MANAGER_SUBMIT_PROFILE="";
  public static String MOBILE_BUSINESS_MANAGER_PROFILE       ="";
  public static String MOBILE_BUSINESS_ORDER_JUNK_DETAIL     ="";
  public static String MOBILE_BUSINESS_ORDER_JUNK_LIST       ="";
  public static String MOBILE_BUSINESS_ORDER_JUNK_AGAIN      ="";
  public static String MOBILE_BUSINESS_SHOP_INDEX            ="";
  public static String MOBILE_BUSINESS_SHOP_SHOW_CREATE      ="";
  public static String MOBILE_BUSINESS_SHOP_CREATE           ="";
  public static String MOBILE_BUSINESS_PRODUCT_SET_AGENT ="";
  public static String MOBILE_BUSINESS_PRODUCT_MY_PRODUCT    ="";
  public static String MOBILE_BUSINESS_PRODUCT_OTHER_TYPE = "";
  public static String MOBILE_BUSINESS_PRODUCT_DETAIL        ="";
  public static String MOBILE_BUSINESS_SHOP_ORDER            ="";
  public static String MOBILE_BUSINESS_SHOP_ORDER_DETAIL     ="";
  public static String MOBILE_BUSINESS_SHOP_CUSTOMER_ORDER_REFUSE="";
  public static String MOBILE_BUSINESS_SHOP_ORDER_JUNK           ="";
  public static String MOBILE_BUSINESS_SHOP_ORDER_PROCESS        ="";
  public static String MOBILE_BUSINESS_SHOP_REPORT               ="";
  public static String MOBILE_BUSINESS_SHOP_RORDER_COMMENT_LABEL ="";
  public static String MOBILE_BUSINESS_SHOP_RORDER_COMMENT       ="";

  /**
   * 刷新token成功后，获取到服务器跟地址，赋值所有接口
   *
   * @param apiUrl 服务器根地址
   */
  public static void setApi(String apiUrl) {

    LOGIN_URL = apiUrl + "login/";
    GET_CODE_URL = apiUrl + "getCode/";
    CHECK_CODE_URL = apiUrl + "checkCode/";
    REGISTER_URL = apiUrl + "register/";
    UPLOAD_IMAGE_URL = apiUrl + "upload";
    TAKE_PSW = apiUrl + "forgot/";
    LOGOUT = apiUrl + "logout";
    SET_AUTH = apiUrl + "setAuth";
    SET_AUTH_BIND = apiUrl + "setOauthBind";
    CLIENT_MEMBER_OAUTH_BIND = apiUrl + "mobile/client/member/oauthBind";
    MOBILE_CLIENT_MEMBER_SET_OAUTH_BIND = apiUrl + "mobile/client/member/setOauthBind";
    MOBILE_CLIENT_MEMBER_INVITE_INFO = apiUrl + "mobile/client/member/inviteInfo";
    WEB_GET_QRCODE = apiUrl + "web/getQrcode";
    MOBILE_CLIENT_MEMBER_SET_PUSH_STATUS = apiUrl + "mobile/client/member/setPushStatus";
    MOBILE_CLIENT_MEMBER_GET_PUSH_STATUS = apiUrl + "mobile/client/member/getPushStatus";
    //RECOMMEND_MANAGER = apiUrl + "mobile/client/manager";
    RECOMMEND_MANAGER = apiUrl + "mobile/client/map";
    HOME_FILTER_DATA = apiUrl + "mobile/client/attrConfig";
    HOME_FILTER_SEARCH = apiUrl + "mobile/client/search";
    C_PRODUCT_DETAIL = apiUrl + "mobile/client/single";
    CLIENT_CONFIG = apiUrl + "mobile/client/config";
    MOBILE_CLIENT_TOP_CONFIG = apiUrl + "mobile/client/topConfig";
    MOBILE_CLIENT_MAP = apiUrl + "mobile/client/map";
    APPLY_LOAN = apiUrl + "mobile/client/apply";
    APPLY_SUCCESS_RECOMMEND_MANAGER = apiUrl + "mobile/client/recommend";
    MOBILE_CLIENT_LOAN = apiUrl + "mobile/client/loan";
    MOBILE_CLIENT_LOAN_SEARCH = apiUrl + "mobile/client/loan/search";
    MOBILE_CLIENT_LOAN_CONFIG = apiUrl + "mobile/client/loan/config";
    MOBILE_CLIENT_LOAN_REGION = apiUrl + "mobile/client/loan/region";
    MOBILE_CLIENT_MESSAGE_TYPE = apiUrl + "mobile/client/message/type";
    MOBILE_CLIENT_MEMBER_POINT = apiUrl + "mobile/client/member/point";
    MOBILE_CLIENT_MEMBER_POINTLIST = apiUrl + "mobile/client/member/pointList";
    MOBILE_CLIENT_USER_AVATAR = apiUrl + "user/avatar";
    MOBILE_CLIENT_QUICK_APPLY = apiUrl + "mobile/client/quickApply";
    MOBILE_CLIENT_MEMBER_FEEDBACK = apiUrl + "mobile/client/member/feedback";
    MOBILE_CLIENT_MEMBER_DOCUMENT = apiUrl + "mobile/client/member/document";
    MOBILE_CLIENT_MEMBER_AUTH_DOCUMENT = apiUrl + "mobile/client/member/authDocument";
    MOBILE_CLIENT_MEMBER_HISTORY = apiUrl + "mobile/client/member/history";
    MOBILE_CLIENT_REPORT = apiUrl+"mobile/client/report";
    MOBILE_CLIENT_USER_SCORE_TYPE = apiUrl + "user/scoreType";
    MOBILE_CLIENT_USER_ADD_SCORE = apiUrl + "user/addScore";
    RESET_PSW = apiUrl + "reset";
    MOBILE_CLIENT_CHECK_FAVOURITE = apiUrl + "mobile/client/checkFavorite";
    MOBILE_CLIENT_MEMBER_SET_FAVORITE = apiUrl + "mobile/client/member/setFavorite";
    MOBILE_CLIENT_MEMBER_SET_FAVORITE_LIST = apiUrl + "mobile/client/member/favoriteList";
    MOBILE_CLIENT_CHECK_NOTICE = apiUrl + "mobile/client/checkNotice";
    MOBILE_CLIENT_MESSAGE_SET_READ = apiUrl + "mobile/client/message/setRead";
    MOBILE_CLIENT_EVALUATE = apiUrl + "mobile/client/evaluate";
    MOBILE_CLIENT_AVERAGE = apiUrl + "mobile/client/average";
    MOBILE_CLIENT_ARTICLE = apiUrl + "mobile/client/article";
    MOBILE_CLIENT_ARTICLE_LIST = apiUrl + "mobile/client/articleList/";

  //  b端
    MOBILE_BUSINESS_INDEX = apiUrl + "mobile/business/index";
    MOBILE_BUSINESS_ORDER_INDEX = apiUrl + "mobile/business/order/index";
    MOBILE_BUSINESS_ORDER_DETAIL = apiUrl + "mobile/business/order/detail";
    MOBILE_BUSINESS_ORDER_GRAB_CONFIG = apiUrl + "mobile/business/order/grabConfig";
    MOBILE_BUSINESS_ORDER_CHECK_PURCHASE = apiUrl + "mobile/business/order/checkPurchase";
    MOBILE_BUSINESS_ORDER_PURCHASE = apiUrl + "mobile/business/order/purchase";
    MOBILE_BUSINESS_ORDER_JUNK_PUBLISH = apiUrl + "mobile/business/order/junkPublish";
    //MOBILE_BUSINESS_ORDER_JUNK_PUBLISH = apiUrl + "product/create";
    MOBILE_BUSINESS_MANAGER_SUBMIT_PROFILE = apiUrl + "mobile/business/manager/submitProfile";
    MOBILE_BUSINESS_MANAGER_PROFILE = apiUrl + "mobile/business/manager/profile";
    MOBILE_BUSINESS_ORDER_JUNK_DETAIL = apiUrl + "mobile/business/order/junkDetail";
    MOBILE_BUSINESS_ORDER_JUNK_LIST = apiUrl + "mobile/business/order/junkList";
    MOBILE_BUSINESS_ORDER_JUNK_AGAIN = apiUrl + "mobile/business/order/junkAgain";
    MOBILE_BUSINESS_SHOP_INDEX = apiUrl + "mobile/business/shop/index";
    MOBILE_BUSINESS_SHOP_SHOW_CREATE = apiUrl + "mobile/business/shop/showCreate";
    MOBILE_BUSINESS_SHOP_CREATE = apiUrl + "mobile/business/shop/create";
    MOBILE_BUSINESS_PRODUCT_SET_AGENT = apiUrl + "mobile/business/product/setAgent";
    MOBILE_BUSINESS_PRODUCT_MY_PRODUCT = apiUrl + "mobile/business/product/myProduct";
    MOBILE_BUSINESS_PRODUCT_OTHER_TYPE = apiUrl + "mobile/business/product/otherType";
    MOBILE_BUSINESS_SHOP_ORDER = apiUrl + "mobile/business/shop/order";
    MOBILE_BUSINESS_SHOP_ORDER_DETAIL = apiUrl + "mobile/business/shop/orderDetail";
    MOBILE_BUSINESS_SHOP_ORDER_JUNK = apiUrl + "mobile/business/shop/orderJunk";
    MOBILE_BUSINESS_SHOP_CUSTOMER_ORDER_REFUSE = apiUrl + "mobile/business/shop/orderRefuse";
    MOBILE_BUSINESS_SHOP_ORDER_PROCESS = apiUrl + "mobile/business/shop/orderProcess";
    MOBILE_BUSINESS_SHOP_REPORT = apiUrl + "mobile/business/shop/report";
    MOBILE_BUSINESS_SHOP_RORDER_COMMENT = apiUrl + "mobile/business/shop/orderComment";
    MOBILE_BUSINESS_SHOP_RORDER_COMMENT_LABEL = apiUrl + "mobile/business/shop/orderCommentLabel";
    MOBILE_BUSINESS_PRODUCT_DETAIL = apiUrl + "mobile/business/product/detail";
  }

  /**
   * 刷新token成功后，获取到服务器跟地址，赋值所有图片相关接口
   *
   * @param imageUrl 服务器图片存储根地址
   */
  public static void setImageApi(String imageUrl) {
    IMAGE_ROOT_URL = imageUrl;
  }
}
