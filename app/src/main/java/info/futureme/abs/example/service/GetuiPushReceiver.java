/*
 * 
 * Copyright (c) 2015-2016 All Rights Reserved.
 * Project Name: lmrp-android app
 * Create Time: 16-2-16 下午6:42
 */

package info.futureme.abs.example.service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import java.util.Date;

import info.futureme.abs.entity.FGson;
import info.futureme.abs.example.ABSApplication;
import info.futureme.abs.example.R;
import info.futureme.abs.example.biz.AccountManagerImpl;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.example.conf.MessageType;
import info.futureme.abs.example.entity.g.Notification;
import info.futureme.abs.example.entity.g.NotificationDao;
import info.futureme.abs.example.ui.MainActivity;
import info.futureme.abs.example.ui.fragment.TicketListFragment;
import info.futureme.abs.example.util.SendNotification;
import info.futureme.abs.example.util.update.CheckUpdateTask;
import info.futureme.abs.util.DLog;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.rx.RxBus;


public class GetuiPushReceiver extends BroadcastReceiver {
    //getui push producing events
    public static final String NEW_RECEIVE = "new_receive";
    public static final String NEW_GRAB = "new_grab";
    public static final String NEW_NOTIFICATION_UNREAD = "new_notification";

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                if(!AccountManagerImpl.instance.isLogin()){
                    return;
                }
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                Log.d("第三方回执接口调用", (result ? "成功" : "失败"));

                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                if (payload != null) {
                    String data = new String(payload);

                    Log.d("GetuiSdkDemo", "receiver payload : " + data);

                    try {
                        final Notification notification = FGson.gson().fromJson(data, new TypeToken<Notification>() {
                        }.getType());
                        if (notification.getType() == null) {
                            break;
                        }
                        if (TextUtils.isEmpty(notification.getTicketid())) {
                            notification.setTicketid("" + System.currentTimeMillis());
                        }
                        if (MessageType.LOGOUT.getValue() == notification.getType()) {
                            if (AccountManagerImpl.instance.isLogin()) {
                                Toast.makeText(context, R.string.conflict_login, Toast.LENGTH_LONG).show();
                                AccountManagerImpl.instance.reLogin();
                            }
                            return;
                        } else {
                            final RxBus rxBus = ABSApplication.getRxBus();
                            final NotificationDao dao = ABSApplication.getDaoSession().getNotificationDao();
                            if (!((null == notification.getDesc() || "".equals(notification.getDesc().trim()))
                                    && (null == notification.getContent() || "".equals(notification.getContent().trim()))
                                    && (null == notification.getTitle() || "".equals(notification.getTitle().trim())))) {
                                String account = FPreferenceManager.getString(MVSConstants.ACCOUNT_SIGNED, "");
                                notification.setAccount(account);
                                notification.setDate(new Date());
                                notification.setReaded(false);
                                long id = dao.insert(notification);
                                notification.setId(id);
                            }
                            rxBus.send(NEW_NOTIFICATION_UNREAD);
                            if (MessageType.GRAB_NOTIFY.getValue() == notification.getType()) {
                                SendNotification.sendNotification(R.mipmap.ic_launcher, notification.getTitle(), notification.getDesc(), context, true, notification.getTicketid().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT, MainActivity.OPEN_GRAB);
                                rxBus.send(NEW_GRAB);
                            } else if (MessageType.RECEIVE_NOTIFY.getValue() == notification.getType()) {
                                SendNotification.sendNotification(R.mipmap.ic_launcher, notification.getTitle(), notification.getDesc(), context, true, notification.getTicketid().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT, MainActivity.OPEN_RECEIVING);
                                rxBus.send(NEW_RECEIVE);
                            } else if (MessageType.SLA_NOTIFY.getValue() == notification.getType()) {
                                SendNotification.sendNotification(R.mipmap.ic_launcher, notification.getTitle(), notification.getDesc(), context, true, notification.getTicketid().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT, MainActivity.OPEN_ACTIVE);
                            } else if (MessageType.TICKET_DATA_CHANGE.getValue() == notification.getType()) {
                                SendNotification.sendNotification(R.mipmap.ic_launcher, notification.getTitle(), notification.getDesc(), context, true, notification.getTicketid().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT, MainActivity.OPEN_NOTIFICATION);
                                rxBus.send(notification);
                                rxBus.send(TicketListFragment.REFRESH_LSIT_ACTIVE);
                            } else if (MessageType.ITSM_DATA_CHANGE.getValue() == notification.getType()) {
                                SendNotification.sendNotification(R.mipmap.ic_launcher, notification.getTitle(), notification.getDesc(), context, true, notification.getTicketid().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT, MainActivity.OPEN_NOTIFICATION);
                                rxBus.send(notification);
                                rxBus.send(TicketListFragment.REFRESH_LSIT_RECEIVE);
                                rxBus.send(TicketListFragment.REFRESH_LSIT_ACTIVE);
                            } else if (MessageType.LAZY_NOTIFY.getValue() == notification.getType()) {
                                SendNotification.sendNotification(R.mipmap.ic_launcher, notification.getTitle(), notification.getDesc(), context, true, notification.getTicketid().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT, MainActivity.OPEN_ACTIVE);
                            } else if (MessageType.WELCOME.getValue() == notification.getType()) {
                                SendNotification.sendNotification(R.mipmap.ic_launcher, notification.getTitle(), notification.getDesc(), context, true, notification.getTicketid().hashCode(), PendingIntent.FLAG_UPDATE_CURRENT, MainActivity.OPEN_NOTIFICATION);
                            } else if (MessageType.APP_NEW_VERSION.getValue() == notification.getType()) {
                                new CheckUpdateTask(context, MVSConstants.APIConstants.TYPE_NOTIFICATION, false).execute();
                            }
                        }
                    }catch(Exception e){
                        DLog.p(e);
                    }
                }

                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                final String cid = bundle.getString("clientid");
                Log.d("cid:", cid);
                if(cid != null && !TextUtils.isEmpty(cid)){
                    FPreferenceManager.putString(MVSConstants.KEY_GETUI_CID, cid);
                }
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 * 
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }
}
