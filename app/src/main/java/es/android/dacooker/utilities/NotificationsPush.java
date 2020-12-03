package es.android.dacooker.utilities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.TimeUnit;

import es.android.dacooker.R;
import es.android.dacooker.activities.MainActivity;

public class NotificationsPush extends BroadcastReceiver {

    public static void createNotifyChannel(Context c, String channelName, String channelDescription){
        createNotificationChannel(c, channelName, channelDescription);
    }

    private static void createNotificationChannel(Context context, String channelName, String channelDescription) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //CharSequence name = channelName;
            //String description = channelDescription;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyEat", channelName, importance);
            channel.setDescription(channelDescription);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public void createNotification(Context context, String title, String content, String channel_id){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_notify_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);

        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000 });
        builder.setLights(Color.RED, 3000, 3000);

        //ID must be unique
        nm.notify(200, builder.build());

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.createNotification(context, context.getString(R.string.notify_title),
                context.getString(R.string.notify_description), "notifyEat");

    }
}
