package com.alex.mychat.Notificaciones;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

public class Notificaciones extends ContextWrapper {
    private static final String CHANNEL_ID= "com.alex.mychat";
    private static final String CHANNEL_NAME= "mychat";
    private NotificationManager notificationManager;
    public Notificaciones(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            crearCanal();
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void crearCanal() {
        NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if (notificationManager == null){
         notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotificacion(String titulo, String cuerpo, PendingIntent pendingIntent, Uri sonidoUri, String icono){
        return  new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setSmallIcon(Integer.parseInt(icono))
                .setSound(sonidoUri)
                .setAutoCancel(true);
    }
}
