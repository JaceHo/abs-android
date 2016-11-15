package info.futureme.abs.example.util;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import info.futureme.abs.example.ui.MainActivity;


public class SendNotification {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void sendNotification(int icon, String title, String data, Context context, boolean autocancle, int notificationID, int pending_mode,String extra) {
        //http://stackoverflow.com/questions/16885706/click-on-notification-to-go-current-activity
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(data.trim());
        builder.setAutoCancel(autocancle);
        builder.setVibrate(null);
        builder.setSound(null);
//        builder.setVibrate(new long[]{1000l, 1000l});
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        Intent intent_app = new Intent(context, MainActivity.class);
        intent_app.setAction(Intent.ACTION_MAIN);
        intent_app.addCategory(Intent.CATEGORY_LAUNCHER);
        intent_app.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent_app.putExtra(extra, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID, intent_app, 0);
        /*
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent_app);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(notificationID, pending_mode);
        */

        builder.setContentIntent(pendingIntent);
        notificationManager.notify(notificationID, builder.build());
    }
    /*Intent intent = new Intent(context,MainActivity.class);
    PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, 0);

    然后就是设置一些notifacation需要的参数，你这里需要打开fragment，那么就用以下代码：
    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:104040444"));
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    notification.setLatestEventInfo(this, title, content, pendingIntent);
    这里虽然是activity，但是fragment是不能独立打开的，因为fragment寄生于activity，你需要打开你的fragment的activity，然后指定该activity使用需要的fragment就可以了。
    */
}
