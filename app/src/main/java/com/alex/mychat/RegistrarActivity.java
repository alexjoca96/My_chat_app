package com.alex.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrarActivity extends AppCompatActivity {
    EditText usuario, email, clave;
    Button btn_registrar;

    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registrarse");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usuario =findViewById(R.id.usuario);
        email= findViewById(R.id.email);
        clave = findViewById(R.id.clave);
        btn_registrar= findViewById(R.id.btn_registrar);
        auth= FirebaseAuth.getInstance();

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_usuario = usuario.getText().toString();
                String txt_email = email.getText().toString();
                String txt_clave = clave.getText().toString();

                if (TextUtils.isEmpty(txt_usuario) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_clave)){
                    Toast.makeText(RegistrarActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                }else if (txt_clave.length() <6 ){
                    Toast.makeText(RegistrarActivity.this,"La contraseña debe ser mayor a 6 caracteres",Toast.LENGTH_SHORT).show();
                }else {
                    registrar(txt_usuario,txt_email, txt_clave);
                }
            }
        });
    }

    private void registrar(String usuario_n, String email, String clave){

        auth.createUserWithEmailAndPassword(email, clave)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(userid);

                            HashMap<String,String> hashMap= new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("usuario", usuario_n);
                            hashMap.put("imagenURL","default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(RegistrarActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
                        }else {
                            Toast.makeText(RegistrarActivity.this, "No se pudo registrar el usuario con esta clave y usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}