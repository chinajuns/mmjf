package com.ysjr.mmjf.entity;

/**
 * 用作操作成功时消息通知  Eventbus
 */

public class Success {
  public static final int TYPE_RUB = 0;
  public static final int TYPE_THROW = 1;
  public int type;
  public int id;

  public Success() {
  }

  public Success(int type) {
    this.type = type;
  }
}
