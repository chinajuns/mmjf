package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class Score {
  public int total;
  public int day;
  public String month;
  public List<ListBean> list;
  public static class ListBean {
    public int id;
    public int number;
    public int integral_id;
    public long create_time;
    public String type_name;
    public String description;
  }
}
