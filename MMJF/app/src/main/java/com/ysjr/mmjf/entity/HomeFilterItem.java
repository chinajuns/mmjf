package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-18.
 */

public class HomeFilterItem {
  public String name;
  public int id;
  public List<FilterItem> values;

  public HomeFilterItem() {

  }
  public HomeFilterItem(int id) {
    this.id = id;
  }
  public static class FilterItem {
    public String id;
    public String attr_id;
    public String options;
    public boolean isChecked;
  }

}
