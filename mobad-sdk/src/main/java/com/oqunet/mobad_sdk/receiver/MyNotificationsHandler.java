package com.oqunet.mobad_sdk.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.microsoft.windowsazure.notifications.NotificationsHandler;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.oqunet.mobad_sdk.R;
import com.oqunet.mobad_sdk.settings.NotificationSettings;


public class MyNotificationsHandler extends NotificationsHandler {
    private static final String LOG_TAG = MyNotificationsHandler.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_ID = "nh-demo-channel-id";
    private static final String NOTIFICATION_CHANNEL_NAME = "Notification Hubs Demo Channel";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Notification Hubs Demo Channel";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private Context context;


    @Override
    public void onReceive(Context context, Bundle bundle) {
        this.context = context;
        String nhMessage = bundle.getString("message");
        Log.i("onReceive message: ", nhMessage);

        sendNotification(nhMessage);

    }

    private void sendNotification(String msg) {

        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.oqunet.mobad");
        if (intent == null) {
            // Bring user to the play store to install the app.
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.oqunet.mobad"));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context,
                NOTIFICATION_CHANNEL_ID)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.mobad_logo)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);

        notificationBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }



    public static void createChannelAndHandleNotifications(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            channel.setShowBadge(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            NotificationsManager.handleNotifications(context, NotificationSettings.ApId, MyNotificationsHandler.class);
        } else {
            NotificationsManager.handleNotifications(context, NotificationSettings.SenderId, MyNotificationsHandler.class);
        }
    }

}
