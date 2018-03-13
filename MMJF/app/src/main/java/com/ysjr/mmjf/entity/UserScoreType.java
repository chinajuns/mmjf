package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-20.
 */

public class UserScoreType {
 public List<TypeBean> type;
 public List<TypeBean> focus;

 public static class TypeBean {
    public int id;
    public String attr_value;
   public String label;
   public boolean isChecked;
  }
}
