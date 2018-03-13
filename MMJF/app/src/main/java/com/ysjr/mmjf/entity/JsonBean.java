package com.ysjr.mmjf.entity;

import com.bigkoo.pickerview.model.IPickerViewData;
import java.util.List;

/**
 * Created by Administrator on 2017-12-5.
 */

public class JsonBean implements IPickerViewData {


  /**
   * name : 省份
   * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
   */

  private String name;
  private List<CityBean> city;
  private int id;
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<CityBean> getCityList() {
    return city;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setCityList(List<CityBean> city) {
    this.city = city;
  }

  // 实现 IPickerViewData 接口，
  // 这个用来显示在PickerView上面的字符串，
  // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
  @Override
  public String getPickerViewText() {
    return this.name;
  }



  public static class CityBean implements IPickerViewData{
    /**
     * name : 城市
     * area : ["东城区","西城区","崇文区","昌平区"]
     */

    private String name;
    private List<DistrictBean> district;
    private int id;

    public CityBean() {
    }

    public CityBean(String name) {
      this.name = name;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<DistrictBean> getDistrict() {
      return district;
    }

    public void setDistrict(List<DistrictBean> district) {
      this.district = district;
    }

    @Override public String getPickerViewText() {
      return this.name;
    }

    public static class DistrictBean implements IPickerViewData{
      public DistrictBean(String name) {
        this.name = name;
      }
      public DistrictBean(String name,String id) {
        this.name = name;
        this.id = id;
      }
      private String name;
      private String id;
      public boolean isChecked;

      public String getId() {
        return id;
      }

      public void setId(String id) {
        this.id = id;
      }

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      @Override public String getPickerViewText() {
        return this.name;
      }
    }
  }
}
