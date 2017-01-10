package info.futureme.abs.example.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import info.futureme.abs.example.biz.AccountManagerImpl;

/**
 * Created by Jeffrey on 03/01/2017.
 */

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class GetuiIntentService extends GTIntentService {
    public static final String NEW_RECEIVE = "new_receive";
    public static final String NEW_GRAB = "new_grab";
    public static final String NEW_NOTIFICATION_UNREAD = "new_notification";

    public GetuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(final Context context, GTTransmitMessage msg) {
        if (!AccountManagerImpl.instance.isLogin()) {
            return;
        }
        // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();

        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d("第三方回执接口调用", (result ? "成功" : "失败"));

        // 获取透传数据
        // String appid = bundle.getString("appid");
        final byte[] payload = msg.getPayload();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (payload != null) {
                    String data = new String(payload);

                    Log.d("GetuiSdkDemo", "receiver payload : " + data);

                }
            }
        });
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        // 获取ClientID(CID)
        // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
        Log.d("cid:", clientid);
        if (clientid != null && !TextUtils.isEmpty(clientid)) {
//            FPreferenceManager.putString(FConstants.KEY_GETUI_CID, clientid);
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }
}
