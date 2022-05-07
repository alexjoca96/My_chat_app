package com.alex.mychat.Notificaciones;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.alex.mychat.MensajeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String enviado = message.getData().get("enviado");
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && enviado.equals(firebaseUser.getUid())){

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
               enviarNotificacionOreo(message);
           else
            enviarNotificacion(message);
        }
    }

    private void enviarNotificacionOreo(RemoteMessage message) {
        String usuario = message.getData().get("usuario");
        String icono = message.getData().get("icono");
        String titulo = message.getData().get("titulo");
        String cuerpo = message.getData().get("cuerpo");

        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(usuario.replaceAll("[\\D]", ""));
        Intent intent= new Intent(this, MensajeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuario);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notificaciones oreoNotificacion = new Notificaciones(this);
        Notification.Builder builder = oreoNotificacion.getNotificacion(titulo,cuerpo,pendingIntent,defaultSound,icono);
        int i= 0;
        if (j>0){
            i=j;
        }
        oreoNotificacion.getManager().notify(i, builder.build());
    }

    private void enviarNotificacion(RemoteMessage message) {
        String usuario = message.getData().get("usuario");
        String icono = message.getData().get("icono");
        String titulo = message.getData().get("titulo");
        String cuerpo = message.getData().get("cuerpo");

        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(usuario.replaceAll("[\\D]", ""));
        Intent intent= new Intent(this, MensajeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("usuario", usuario);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this).setSmallIcon(Integer.parseInt(icono))
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i= 0;
        if (j>0){
            i=j;
        }
        notificationManager.notify(i, builder.build());
    }
}
