package com.ysjr.mmjf.entity;

import java.util.List;

/**
 * Created by Administrator on 2017-12-19.
 */

public class CLoanSearchBean {
  public TypeBean type;
  public FocusBean focus;
  public static class TypeBean {
   public int id;
   public String attr_key;
   public List<ValuesBean> values;
    public static class ValuesBean {
      public boolean isChecked;
      public String attr_id;
      public String attr_value;
      public String id;
      public ValuesBean() {

      }

      public ValuesBean(String attr_value) {
        this.attr_value = attr_value;
      }

      public ValuesBean(String id, String attr_value) {
        this.id = id;
        this.attr_value = attr_value;
      }
    }
  }

  public static class FocusBean {
    public String attr_key;
    public int id;
    public List<ValuesBeanX> values;

    public static class ValuesBeanX {
      public ValuesBeanX(String attr_value, String id) {
        this.attr_value = attr_value;
        this.id = id;
      }

      public boolean isChecked;
      public String attr_value;
      public String pid;
      public String id;
    }
  }

}
