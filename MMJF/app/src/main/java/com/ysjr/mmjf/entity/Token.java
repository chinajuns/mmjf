package com.ysjr.mmjf.entity;

/**
 * Created by Administrator on 2017-11-16.
 */

public class Token {
    public String uid;
    public String token;
    public String api_url;
    public String image_url;

  @Override public String toString() {
    return "Token{"
        + "uid='"
        + uid
        + '\''
        + ", token='"
        + token
        + '\''
        + ", api_url='"
        + api_url
        + '\''
        + ", image_url='"
        + image_url
        + '\''
        + '}';
  }
}
