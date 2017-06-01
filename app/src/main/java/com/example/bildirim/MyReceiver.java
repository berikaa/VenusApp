package com.example.bildirim;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.example.hatic.venus.R;


public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle receiver = intent.getExtras();
        String barcode=receiver.getString("barcode");
        String price=receiver.getString("price");

        Notification(context, barcode+" Barkodlu no lu ürün"+" "+price+"TL'ye düşmüştür.");
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);


    }
    public void Notification(Context context, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ikl)
                .setTicker(message)
                .setContentTitle(context.getString(R.string.notificationtitle))
                .setContentText(message)
                .setAutoCancel(true);

        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());
    }
}