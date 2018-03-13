package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2018-1-8.
 */

public class CHomeTopBean {

  public String type;
  public int id;
  public List<ValuesBean> values;

  public static class ValuesBean {
    public ValuesBean(String name) {
      this.name = name;
    }

    public int id;
   public String name;
   public int pid;

  }
}
