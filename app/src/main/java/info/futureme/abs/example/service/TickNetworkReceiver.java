package info.futureme.abs.example.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.igexin.sdk.PushManager;

import info.futureme.abs.FApplication;
import info.futureme.abs.example.biz.AccountManagerImpl;
import info.futureme.abs.example.conf.MVSConstants;
import info.futureme.abs.util.FPreferenceManager;
import info.futureme.abs.util.NetworkUtil;

public class TickNetworkReceiver extends BroadcastReceiver {

    public static final String NET_ON = "network_online";
    public static final String NET_OFF = "network_off";

    private static void restartIfNecessary(final Context context) {
        if (AccountManagerImpl.instance.isLogin()) {
            if (NetworkUtil.isNetworkAvailable(context)) {
                // 重新初始化sdk
                if(UploadService.shouldRestart()) {
                    Log.w("upload:", "restarting");
                    UploadService.actionStart(context);
                }else{
                    Log.w("upload:", "stoping");
                    UploadService.actionStop(context);
                }
//            } else {
//                Log.w("network:", "push: stoping");
                //PushManager.getInstance().stopService(context);
            }
        }else{
            if (NetworkUtil.isNetworkAvailable(context)) {
                String cid = FPreferenceManager.getString(MVSConstants.KEY_GETUI_CID, "");
                if("".equals(cid)) {
                    Log.w("network:", "push: init");
                    PushManager.getInstance().initialize(context);
                }
            }
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.w("net:", "login:" + AccountManagerImpl.instance.isLogin());
        if (NetworkUtil.isNetworkAvailable(context)) {
            FApplication.getRxBus().send(NET_ON);
        }else{
            FApplication.getRxBus().send(NET_OFF);
        }
        restartIfNecessary(context);
    }

    /**
     * 1 minute receive, backup plan for service killing
     */
    public static class TickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                Log.w("tick:", "login:" + AccountManagerImpl.instance.isLogin());
                restartIfNecessary(context);
            }
        }
    }
}

