package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-29.
 */

public class MStore {
  public boolean isEmpty;
  public int check_result;
  public ShopInfo shop_info;
  public List<MThrowOrderBean> order;

  public static class ShopInfo {
    public String username;
    public String header_img;
    public String service_city;
    public float score;
    public int pageviews;
    public int status;
    public int loaner_id;
    public String introduce;
  }
}
