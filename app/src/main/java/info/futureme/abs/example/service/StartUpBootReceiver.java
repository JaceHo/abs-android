package info.futureme.abs.example.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import info.futureme.abs.example.biz.AccountManagerImpl;

public class StartUpBootReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if(AccountManagerImpl.instance.isLogin()) {
                AccountManagerImpl.async();
            }
        }
    }

}

