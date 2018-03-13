package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2018-1-4.
 */

public class MDelegateDetailBean {
  public int id;
  public String source;
  public String cate_name;
  public String loan_number;
  public String period;
  public String rate;
  public int apply_people;
  public long create_time;
  public List<ApplyConditionBean> apply_condition;
  public List<String> need_data;

  public static class ApplyConditionBean {
    public String attr_key;
    public List<String> attr_value;
  }
}
