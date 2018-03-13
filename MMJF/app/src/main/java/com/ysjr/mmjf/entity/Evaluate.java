package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-1.
 */

public class Evaluate {
  public List<DataItem> data;
  public static class DataItem {
    public int loan_id;
    public int user_id;
    public String describe;
    public long create_time;
    public float score_avg;
    public String focus;
    public String username;
  }
}
