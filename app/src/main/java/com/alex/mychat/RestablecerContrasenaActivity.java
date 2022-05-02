package com.alex.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RestablecerContrasenaActivity extends AppCompatActivity {
        EditText enviar_email;
        Button btn_reestablecer;
        FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restablecer_contrasena);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reestablecer Contraseña");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        enviar_email = findViewById(R.id.enviar_email);
        btn_reestablecer = findViewById(R.id.btn_restablecer);
        firebaseAuth= FirebaseAuth.getInstance();
        btn_reestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = enviar_email.getText().toString();
                if (email.equals("")){
                    Toast.makeText(RestablecerContrasenaActivity.this, "El campo Email es requerido", Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RestablecerContrasenaActivity.this, "Se ha enviado la informacion al email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RestablecerContrasenaActivity.this, AccederActivity.class));
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(RestablecerContrasenaActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}