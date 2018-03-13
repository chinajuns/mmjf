package com.ysjr.mmjf.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017-12-20.
 */

public class COrder implements Serializable{
  public List<DataItem> data;
  public static class DataItem implements Serializable{
    public String loan_account;
    public int id;
    public int user_id;
    public int status;
    public int process;
    public int loaner_id;
    public int is_comment;
    public Manager loaner;
    public List<AllProcessBean> all_process;
    public List<ProcessingBean> processing;
    public static class AllProcessBean implements Serializable{
      public int id;
      public String attr_value;
    }
    public static class ProcessingBean implements Serializable{
      public int id;
      public String describe;
      public long create_time;
      public int loaner_id;
      public String value;
      public String loanername;
      public String mobile;

    }
  }
}
