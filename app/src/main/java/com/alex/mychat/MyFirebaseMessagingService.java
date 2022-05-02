package com.alex.mychat;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alex.mychat.Notificaciones.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //String refrescarToken= String.valueOf(FirebaseMessaging.getInstance().getToken());
        //sendRegistrationToServer(token);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MyFirebaseMessagingService.this, "Algo fallo", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();
               if (firebaseUser !=null){
                   ActualizarTokens(token);
               }

            }
        });

    }

    private void ActualizarTokens(String reftoken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(reftoken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }


}
