/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android app
 * Create Time: 16-2-16 下午6:31
 */

package info.futureme.abs.example.conf;

/**
 * 修改订单信息，上传附件，状态。action有如下值
 grab - 抢单, reject - 退回, confirm - 接单, appoint - 预约, signin - 签到, pickparts - 取备件, attach - 传附件, evaluate - 评价, close - 关单,breakoff - 快速结束,reappoint - 改约,reconfirm - 再约， execute - 执行

 * Created by hippo on 12/21/15.
 */
public enum TicketAction {
    GRAB("grab"),
    REJECT("reject"),
    CONFIRM("confirm"),
    APPOINT("appoint"),
    SIGININ("signin"),
    PICKPARTS("pickparts"),
    ATTACH("attach"),
    EVALUATE("evaluate"),
    CLOSE("close"),
    BREAKOFF("breakoff"),
    REAPPOINT("reappint"),
    RECONFIRM("reconfirm"),
    EXECUTE("execute");
    private final String value;
    TicketAction(String value) {
        this.value = value;
    }

    public final String getValue() {
        return value;
    }
}
