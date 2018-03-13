package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * c端信贷经理评价顶部实体
 */

public class CManagerTag {

  public int excellent;
  public int counts;
  public float average;
  public int better;
  public int good;
  public List<TagBean> tag;
  public static class TagBean {
    public String tag;
    public int times;
  }
}
