/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android app
 * Create Time: 16-2-16 下午6:31
 */

package info.futureme.abs.example.conf;

/**
 * type标明消息类型，值为：
 0 - 问候,
 1 - 抢单通知,
 2 - 分派提醒,
 3 - SLA提醒,
 4 - 催单提醒,
 5 - ITSM订单变更通知，
 6 - APP新版本通知，
 7 - 登出通知
 8 - 基础数据变更
 * Created by hippo on 12/21/15.
 */
public enum MessageType {
    BILL_NUMBER(-1),
    WELCOME(0),
    GRAB_NOTIFY(1),
    RECEIVE_NOTIFY(2),
    SLA_NOTIFY(3),
    LAZY_NOTIFY(4),
    ITSM_DATA_CHANGE(5),
    APP_NEW_VERSION(6),
    LOGOUT(7),
    TICKET_DATA_CHANGE(8);
    private final int value;
    MessageType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
