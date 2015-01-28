package com.app.ivoke.libraries;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationManager {

    public static final String PE_FROM_NOTIFICATION = "com.app.ivoke.Notification";
    public static final String FROM_CHAT = "com.app.ivoke.Notification:From.Chat";

    public static final int CHAT_NOTIFICATION = 1;

    public static void showNotification( Activity pActivity
                                       , int pNotificationId
                                       , int pIconResID
                                       , String pTitle
                                       , String pMessage
                                       , Class pClasActivityToOpen)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(pActivity)
                .setSmallIcon(pIconResID)
                .setContentTitle(pTitle)
                .setContentText(pMessage);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(pActivity, pClasActivityToOpen);
        resultIntent.putExtra(PE_FROM_NOTIFICATION, FROM_CHAT);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(pActivity);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(pClasActivityToOpen);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        android.app.NotificationManager mNotificationManager =
            (android.app.NotificationManager) pActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(pNotificationId, mBuilder.build());
    }

    public static void disposeNotification(Activity pActivity,int pNotificationId)
    {
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) pActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.cancel(pNotificationId);
    }

}
