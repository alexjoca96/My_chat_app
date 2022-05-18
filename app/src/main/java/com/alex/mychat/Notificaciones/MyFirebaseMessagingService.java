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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null) {
            ActualizarTokens(token);
            ActualizarTokensUsuario(token);
        }
    }

    private void ActualizarTokens(String token) {
        /*FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        Map<String,Object> map= new HashMap<>();
        map.put("token", token);
        reference.updateChildren(map);*/
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token tokn= new Token(token);
        reference.child(firebaseUser.getUid()).setValue(tokn);
    }
    private void ActualizarTokensUsuario(String token) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        reference.updateChildren(map);
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
               enviarNotificacionOreo(message);
           else
            enviarNotificacion(message);

    }

    private void enviarNotificacionOreo(RemoteMessage message) {
        String usuario = message.getData().get("usuario");
        String icono = message.getData().get("icono");
        String cuerpo = message.getData().get("cuerpo");
        String titulo = message.getData().get("titulo");



        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(usuario.replaceAll("[\\D]", ""));
        Intent intent= new Intent(this, MensajeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", usuario);
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
        String cuerpo = message.getData().get("cuerpo");
        String titulo = message.getData().get("titulo");



        RemoteMessage.Notification notification = message.getNotification();
        int j = Integer.parseInt(usuario.replaceAll("[\\D]", ""));
        Intent intent= new Intent(this, MensajeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", usuario);
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
