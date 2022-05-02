package com.alex.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mychat.Adapters.MensajeAdapter;
import com.alex.mychat.Modelo.Chat;
import com.alex.mychat.Modelo.Usuario;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajeActivity extends AppCompatActivity {

    CircleImageView perfil_imagen;
    TextView usuario;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ImageButton btn_enviar;
    EditText tex_enviar;

    MensajeAdapter mensajeAdapterer;
    List<Chat> mChat;
    RecyclerView recyclerView;

    Intent intent;
    ValueEventListener vistoEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MensajeActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        recyclerView= findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        perfil_imagen = findViewById(R.id.perfil_imagen);
        usuario =findViewById(R.id.usuario);
        btn_enviar =findViewById(R.id.btn_enviar);
        tex_enviar = findViewById(R.id.text_enviar);
        intent = getIntent();
        String userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = tex_enviar.getText().toString();
                if (!msg.equals("")){
                    enviarMensaje(firebaseUser.getUid(),userid, msg);
                }else{
                    Toast.makeText(MensajeActivity.this,"No puedes enviar un mensaje vacio",Toast.LENGTH_SHORT).show();
                }
                tex_enviar.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);
                usuario.setText(user.getUsuario());
                if (user.getImagenURL().equals("default")){
                    perfil_imagen.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImagenURL()).into(perfil_imagen);
                }
                leerMensajes(firebaseUser.getUid(),userid,user.getImagenURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mensajeVisto(userid);
    }

    private void mensajeVisto(final String userid){
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        vistoEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if (chat.getReceptor().equals(firebaseUser.getUid()) && chat.getEmisor().equals(userid)){
                        HashMap<String, Object> hashMap= new HashMap<>();
                        hashMap.put("visto",true);
                        snapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void enviarMensaje(String emisor, String receptor, String mensaje){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor",emisor);
        hashMap.put("receptor",receptor);
        hashMap.put("mensaje",mensaje);
        hashMap.put("visto",false);
        reference.child("Chats").push().setValue(hashMap);

    }
    private void leerMensajes(final String mi_id, String userid, String imagenurl){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if (chat.getReceptor().equals(mi_id) && chat.getEmisor().equals(userid) || chat.getReceptor().equals(userid) && chat.getEmisor().equals(mi_id)){

                        mChat.add(chat);
                    }
                    mensajeAdapterer = new MensajeAdapter(MensajeActivity.this ,mChat, imagenurl);
                    recyclerView.setAdapter(mensajeAdapterer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void estado(String estado){
        reference= FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("estado",estado);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        estado("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(vistoEventListener);
        estado("offline");
    }
}