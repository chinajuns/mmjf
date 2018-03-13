package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-4.
 */

public class MsgWrapBean {
  public int current_page;
  public String first_page_url;
  public int from;
  public int last_page;
  public String last_page_url;
  public String path;
  public int per_page;
  public int to;
  public int total;
  public List<DataBean> data;

  public static class DataBean {
    public int from_uid;
    public int to_uid;
    public String title;
    public String content;
    public int type;
    public long create_time;
    public int is_success;
    public int status;
  }
}
