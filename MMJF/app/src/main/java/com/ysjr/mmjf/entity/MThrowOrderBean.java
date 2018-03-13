package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-28.
 */

public class MThrowOrderBean {
  public boolean isEmpty;//用于店铺主页展示空页面
  public int id;
  public long create_time;
  public String score;
  public String customer;
  public String current_place;
  public int is_vip;
  public float apply_number;
  public String loan_type;
  public String period;
  public String age;
  public String mobile;
  public int is_marry;
  public String description;
  public int junk_status;
  public String label;
  public long expire_time;
  public int order_status;
  public List<InfoBean> job;
  public List<InfoBean> assets;
  public List<InfoBean> info;

  public static class InfoBean {
    public String attr_key;
    public String attr_value;
  }
}
