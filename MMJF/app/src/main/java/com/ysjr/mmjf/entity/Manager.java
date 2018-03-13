package com.ysjr.mmjf.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017-11-28.
 */

public class Manager implements Serializable{
  public int id;
  public String name;
  public String loanername;
  public String loanername_mobile;
  public String header_img;
  public int max_loan;
  public String tag;
  public String loan_day;
  public float loan_number;
  public float score;
  public float all_number;
  public int is_auth;
  public String tags;
  public String mobile;
  public long create_time;
  public int object_id;
  public boolean isChecked;
  public double loaner_lng;
  public double loaner_lat;
  public int region_id;
}
