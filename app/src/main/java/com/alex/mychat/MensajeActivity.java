package com.alex.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mychat.Adapters.MensajeAdapter;
import com.alex.mychat.Notificaciones.APIService;
import com.alex.mychat.Modelo.Chat;
import com.alex.mychat.Modelo.Usuario;
import com.alex.mychat.Notificaciones.Client;
import com.alex.mychat.Notificaciones.Data;
import com.alex.mychat.Notificaciones.Emisor;
import com.alex.mychat.Notificaciones.MiRespuesta;
import com.alex.mychat.Notificaciones.Token;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MensajeActivity extends AppCompatActivity {

    private static final String CHANNEL_ID= "com.alex.mychat";
    private static final String CHANNEL_NAME= "mychat";
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

    APIService apiService;
    public String userid;
    boolean notifcar = false;
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

        apiService = Client.getCliente("https://fcm.googleapis.com/").create(APIService.class);

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
         userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //notifcar= true;
                String msg = tex_enviar.getText().toString();
                if (!msg.equals("")){
                    enviarMensaje(firebaseUser.getUid(),userid, msg);
                    //enviarNotificacion(userid, firebaseUser.getUid(),msg);
                    //getToken(msg, userid,"default");
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
        //getToken(mensaje,  receptor, "dafault");
        final String msg = mensaje;
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);

                enviarNotificacion(receptor, user.getUsuario(), msg);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /*private void enviarNotificacion(String receptor, final String usuario, final String mensaje){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receptor);
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Token token = snapshot1.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher, usuario+": "+mensaje, "Nuevo Mensaje",userid);
                    assert token != null;
                    Emisor emisor = new Emisor(data, token.getToken());
                    apiService.enviarNotificacion(emisor).enqueue(new Callback<MiRespuesta>() {
                        @Override
                        public void onResponse(Call<MiRespuesta> call, Response<MiRespuesta> response) {
                            if (response.code() == 200){
                                if (response.body().exitoso != 1){
                                    Toast.makeText(MensajeActivity.this, "fallo", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MiRespuesta> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
    private void enviarNotificacion(String receptor, final String usuario, final String mensaje){

        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(receptor);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Usuario user = snapshot.getValue(Usuario.class);
                            Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_icono, usuario+": "+mensaje, "Nuevo Mensaje");
                            Emisor emisor = new Emisor(data, user.getToken());
                            apiService.enviarNotificacion(emisor).enqueue(new Callback<MiRespuesta>() {
                                @Override
                                public void onResponse(Call<MiRespuesta> call, Response<MiRespuesta> response) {
                                    if (response.code() == 200){
                                        if (response.body().exitoso != 1){
                                            //Toast.makeText(MensajeActivity.this, "Fallo la Api", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MiRespuesta> call, Throwable t) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

    }



   /* @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearNotificacionOreo(String titulo, String mensaje, String receptor, int ic_launcher) {
        NotificationChannel channel= new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setDescription("Descripcion de mensaje");
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Intent intent= new Intent(this,MensajeActivity.class);
        intent.putExtra("userid",receptor);
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification= new Notification.Builder(this,CHANNEL_ID)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setColor(ResourcesCompat.getColor(getResources(),R.color.purple_200,null))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(new Random().nextInt(85-65),notification);

    }*/

   /* private void notificar(JSONObject to) {
        JsonObjectRequest request= new JsonObjectRequest(Request.Method.POST,"https://fcm.googleapis.com/fcm/send",to, response -> {
            Log.d("notificacion", "enviarNotificacion: "+ response);
        },error->{
            Log.d("notificacion", "enviarNotificacion: "+ error);
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("Autorization", "key=AAAAGpKJsBQ:APA91bFkfy2FXrOhO87qfxbgrvgqsb0grt2TLin3fzMdq9yzzGtYCwyFle1uiFBkd0VZSGwyGjWMO4xAj2A46wRvD2ZMnnQhAq9-scyeOpYeSF-rR4qNqMhjkBj5qlMlsJlM2dz_5fgu\t\n");
                map.put("Content-Type","application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }*/

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

   /* private void getToken(String mensaje, String userid, String imagenUrl){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                String usuario= snapshot.child("usuario").getValue().toString();
                JSONObject to= new JSONObject();
                JSONObject data= new JSONObject();
                try {
                    data.put("titulo",usuario);
                    data.put("mensaje",mensaje);
                    data.put("usuario",firebaseUser.getUid());
                    data.put("imagenURL",R.mipmap.ic_launcher);


                    to.put("to", token);
                    to.put("data",data);

                    notificar(to);

                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

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