package com.ysjr.mmjf.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017-12-22.
 */

public class NewsBean {
  public AdverBean adver;
  public List<ConfigBean> config;
  public List<ListParent> list;
  public static class AdverBean {
    public List<ImageBean> top;
    public List<ImageBean> middle;
    public static class ImageBean {
      public String image;
      public String link;
    }
  }

  public static class ConfigBean {
   public int id;
   public String cate_name;
  }

  public static class ListParent {
    public int id;
    public String cate_name;
    public List<ListChild> list;
    public static class ListChild implements Serializable{
      public String title;
      public String picture;
      public int id;
      public long create_time;
      public int views;
      public String introduce;
      public int object_id;

    }
    }

}
