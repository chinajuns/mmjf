package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-13.
 */

public class ProBean {

  public int id;
  public String title;
  public String time_limit;
  public String rate;
  public String loan_number;
  public String loan_day;
  public int cate_id;
  public long create_time;
  public int apply_peoples;
  public String need_data;
  public int time_limit_id;
  public int is_proxy;
  public long proxy_time;
  public String loan_type;
  public List<OptionsBean> options;

  public static class OptionsBean {
    /**
     * option_name : 还款方式
     * option_values : 等额本息,等额本金,先息后本,随借随还,一次性还本付息
     */
    public String option_name;
    public String option_values;

  }
}
