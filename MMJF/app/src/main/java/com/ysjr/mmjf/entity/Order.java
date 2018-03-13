package com.ysjr.mmjf.entity;

/**
 * 订单
 */

public class Order {
  public int type;
  //用于订单详情页签约金额和放款金额
  public String money;
  public Order() {

  }
  public Order(int type) {
    this.type = type;
  }
}
