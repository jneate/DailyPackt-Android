package com.iambenzo.dailypackt;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

/**
 * Service which shows the
 * daily notification
 *
 * @author Ben
 * @version 1.0
 * @since 11/05/2017
 */
public class NotificationService extends IntentService {

    private static int NOTIFICATION_ID = 1;
    Notification notification;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    //required constructor for IntentService
    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent( Intent intent) {

        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, LoaderActivity.class);

        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        notification = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notify_small)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.packticon))
                .setTicker("Packt Daily")
                .setAutoCancel(true)
                .setPriority(8)
                .setSound(soundUri)
                .setContentTitle("Packt Daily")
                .setContentText("Check out today's free book!").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notification.ledARGB = 0xFFFFA500;
        notification.ledOnMS = 800;
        notification.ledOffMS = 1000;
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }
}
