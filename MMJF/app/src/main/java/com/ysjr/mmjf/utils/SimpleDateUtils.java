package com.ysjr.mmjf.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017-12-18.
 */

public class SimpleDateUtils {
  public static String getNoHours(long mills) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    return format.format(new Date(mills));
  }

  public static String getHasHours(long mills) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return format.format(new Date(mills));
  }
}
