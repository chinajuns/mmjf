package com.ysjr.mmjf.entity;

import com.bigkoo.pickerview.model.IPickerViewData;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017-11-29.
 */

public class ConfigBean{

  public List<WorkBean> work;
  public List<AssetsBean> assets;
  public List<BasicBean> basic;

  public static class WorkBean {
    public int id;
    public String name;
    public List<ValuesBean> values;
  }
  public static class AssetsBean {
    /**
     * id : 20
     * name : 是否拥有公积金
     * values : [{"id":22,"name":"有","pid":20},{"id":23,"name":"无","pid":20}]
     */

    public int id;
    public String name;
    public List<ValuesBean> values;

  }
  public static class BasicBean {
    public int id;
    public String name;
    public List<ValuesBean> values;
  }

  public static class ValuesBean implements IPickerViewData,Serializable{
    public String id;
    public String name;
    public int pid;
    public boolean isChecked;
    public ValuesBean() {

    }

    public ValuesBean(String name,boolean  isChecked) {
      this.name = name;
      this.isChecked = isChecked;
    }

    public ValuesBean(String id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override public String getPickerViewText() {
      return name;
    }
  }
}
