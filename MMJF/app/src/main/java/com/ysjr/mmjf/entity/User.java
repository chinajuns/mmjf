package com.ysjr.mmjf.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-11-27.
 */

public class User extends DataSupport{
  public int id;
  public String username;
  public String mobile;
  public int sex;
  public String header_img;
  public int is_auth;
  public int integral;
  public long last_login_time;
  public int type;
}
