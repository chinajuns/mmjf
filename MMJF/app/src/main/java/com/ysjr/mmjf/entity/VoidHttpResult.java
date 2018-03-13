package com.ysjr.mmjf.entity;

/**
 * Created by Administrator on 2017-11-17.
 */

public class VoidHttpResult {
    public String msg;
    public int status;

    public HttpResult toHttpResult() {
        HttpResult result = new HttpResult();
        result.msg = msg;
        result.status = status;
        return result;
    }
}
