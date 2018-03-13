package com.ysjr.mmjf.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018-1-8.
 */

public class CHomeMap {
  public ListManager list;
  public List<Map> map;
  public static class ListManager {
    public List<Manager> data;
  }
  public static class Map implements Serializable {

    public int id;
    public String name;
    public int counts;
    public double lat;
    public double lng;

  }

}
