package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * B端客户实体
 */

public class Customer {
  public String order_num;
  public String mobile;
  public String age;
  public int id;
  public String customer;
  public long create_time = -1;
  public int is_vip;
  public float apply_number;
  public String loan_type;
  public String period;
  public int score;
  public String current_place;
  public int purchased;
  public List<InfoBean> info;
  public List<InfoBean> job;
  public List<InfoBean> assets;
  public Basic basic;
  public int[] processIds;
  public int process;
  public int is_comment;
  public int price;
  public int loaner_id;
  public List<ProcessHistory> processHistory;
  public String description;

  public static class Basic {
    public String age;
    public String mobile;
    public int is_marry;
  }
  public static class InfoBean {
    public String attr_key;
    public String attr_value;
  }

  public static class ProcessHistory {
    public int id;
    public int loan_id;
    public int loaner_id;
    public int process;
    public long create_time;
    public int status;
    public String describe;
    public String loanername;
    public String mobile;
  }
}
