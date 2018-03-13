package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-18.
 */

public class CProBean {
  public String title;
  public String shop_id;
  public String loaner_id;
  public String rate;
  public String time_limit;
  public String loan_number;
  public String loan_day;
  public String category;
  public int apply_people;
  public String need_data;
  public String platform;
  public long agent_time;
  public List<OptionsBean> options;
  public static class OptionsBean {
   public String option;
   public String option_values;

  }
}
