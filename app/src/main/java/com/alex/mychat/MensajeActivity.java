package com.alex.mychat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MensajeActivity extends AppCompatActivity {


    CircleImageView perfil_imagen;
    TextView usuario;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    ImageButton btn_enviar,btn_adjuntar;
    EditText tex_enviar;

    MensajeAdapter mensajeAdapterer;
    List<Chat> mChat;
    RecyclerView recyclerView;

    Intent intent;
    ValueEventListener vistoEventListener;

    APIService apiService;
    public String userid;
    boolean notifcar = false;
    private Uri imageUri;
    ActivityResultLauncher<String> takeFoto;
    ActivityResultLauncher<Intent> takeFoto2;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    boolean visible= false;
    ImageView galeria, camara;
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
        // botón de enviar que recoje los datos puestos en el editText para ser enviados
        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = tex_enviar.getText().toString();
                //ponemos la variable notificar a true para que se envie la notificacion
                notifcar= true;
                if (!msg.equals("")){
                    //si el mensaje no es vacio se envia
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
        //boton de adjuntar que mostrara el layout para la eleccionde la camara o la galeria
        btn_adjuntar =findViewById(R.id.btn_adjuntar);
        btn_adjuntar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(visible)
                    ocultarLayout();//si el layout ya esta visible lo olcultamos
                else
                    mostrarLayout();//si esta oculto lo mostramos

            }
        });

        //boton que abrira el activity que busca las imagenes
        galeria = findViewById(R.id.galeria);
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifcar= true;
                //llamamos al activity result para la busqueda de imagenes
                takeFoto.launch("image/*");
            }
        });
        //boton que abrira la camara mediante el activity result
        camara = findViewById(R.id.camara);
        camara.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //intent para la captura de imagenes
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                   //si los permisos de camara y de almacenamiento estan concedidos puede hacer uso de la camara
                    if (checkCameraPermiso() && checkStoragePermiso()) {
                        notifcar = true;//variable para que se notifique al elegir una imagen
                        takeFoto2.launch(intent);//lanzamos el activity de camara
                    }else{
                        //si no estan concedidos los permisos los pedimos
                        pedirPermisoCamara();
                        pedirPermisoStorage();
                    }
            }
        });
        // se lanzara el activity result que buscara las imagenes
        takeFoto= registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //en caso de que una imagen sea seleccionada se devolvera el uri de la imagen
                        imageUri= result;
                        //si la imagen esta cargando es que la imagen esta en subida
                        if (uploadTask!= null && uploadTask.isInProgress()){
                            Toast.makeText(MensajeActivity.this,"Upload en progreso",Toast.LENGTH_LONG).show();
                        }else {
                            //sino llamamos a la funcion para subir la imagen en la base de datos
                            uploadImage(userid);
                        }
                    }
                }
        );

        //llamada al activity result para la camara
        takeFoto2= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {

                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent intent= result.getData();//recogemos el intent enviado
                        Bundle extras = intent.getExtras();
                        Bitmap image= (Bitmap) extras.get("data");//obtenemos el bitmap de la imagen tomada
                        imageUri= getImageUri(MensajeActivity.this, image); //llamamos a la funcion que convierte el bitmap a un uri
                        if (uploadTask!= null && uploadTask.isInProgress()){
                            Toast.makeText(MensajeActivity.this,"Upload en progreso",Toast.LENGTH_LONG).show();

                        }else {
                            uploadImage(userid);
                        }
                    }
                });

    }
        //funcion que verifica si un mensaje ha sido visto
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
    // funcion que registra un mensaje en la base de datos
    public void enviarMensaje(String emisor, String receptor, String mensaje){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        //creamos un bitmap para almacenar los datos correspondientes a un nodo de tipo chat
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor",emisor);
        hashMap.put("receptor",receptor);
        hashMap.put("mensaje",mensaje);
        hashMap.put("visto",false);
        hashMap.put("type","text");
        reference.child("Chats").push().setValue(hashMap);//el push que almacenara el mensaje en la base de datos

        final String msg = mensaje;
        //creamos una instancia a los nodos usuarios para obtener los datos de quien envia
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);
                if (notifcar)
                enviarNotificacion(receptor, user.getUsuario(), msg);//llamamos a la funcion encargada de notificar
                notifcar=false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    //funcion encargada de establecer los parametros para que al enviar un mensaje tambien se envie una notificacion
    private void enviarNotificacion(String receptor, final String usuario, final String mensaje){
        //creamos la instacia que apunta al nodo del usuario receptor para obtener su token identificador necesario para el push de la notificacion
        DatabaseReference  reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(receptor);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //obtenemos el objeto usuario del modulo apuntado es decir el objeto que rebira para obtenr su token
                            Usuario user = snapshot.getValue(Usuario.class);
                            //creamos un ojeto tipo data que tendra todos los datos de la notificacion
                            Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_icono, usuario+": "+mensaje, "Nuevo Mensaje");
                            //y el objeto emisor es muy importante ya que es el que asocia los datos con el usuario que recibira
                            Emisor emisor = new Emisor(data, user.getToken());
                            //para que la notificacion se haga efectiva usaremos una api de goolgle a la cual le pasamos los datos necesarios
                            //en este caso el objeto emisor para que la api funcione le pasamos la direccion url y mediante un callback hace efectiva la comunicacion

                                notifcar = false;
                                apiService.enviarNotificacion(emisor).enqueue(new Callback<MiRespuesta>() {
                                    @Override
                                    public void onResponse(Call<MiRespuesta> call, Response<MiRespuesta> response) {

                                        if (response.code() == 200) {
                                            if (response.body().exitoso != 1) {
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

    //funcion que recupera los mensajes registrados en la base de datos para su lectura dentro del chat
    private void leerMensajes(final String mi_id, String userid, String imagenurl){
        //lista de chats vacia
        mChat = new ArrayList<>();
        //creamos una instancia que apunte a todos los nodos de tipo chat
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    //convertimos el nodo obtenido en un objeto chat
                    Chat chat = snapshot1.getValue(Chat.class);
                    if (chat.getReceptor().equals(mi_id) && chat.getEmisor().equals(userid) || chat.getReceptor().equals(userid) && chat.getEmisor().equals(mi_id)){
                        //si el objeto chat conseguido ha sido enviado o recibido por mi y otro usuario en concreto entonces lo añadimos a la lista
                        //esto quiere decir que los objetos forman parte de una misma conversacion
                        mChat.add(chat);
                    }
                    //llamamos al adapter para que transforme la lista de objetos en mensajes entre 2 usuarios
                    mensajeAdapterer = new MensajeAdapter(MensajeActivity.this ,mChat, imagenurl);
                    //y luego le anexamos el adapter al recyclerview para que los muestre en la lista del chat
                    recyclerView.setAdapter(mensajeAdapterer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //funcion que hace visible el layout encargado de mostrarnos las opciones de envio de datos tipo media
    private void mostrarLayout(){
        RelativeLayout view= findViewById(R.id.datosLayout);
        float radius= Math.max(view.getWidth(),view.getHeight());
        Animator animator= ViewAnimationUtils.createCircularReveal(view,view.getLeft(), view.getTop(),0,radius*2);
        animator.setDuration(800);
        view.setVisibility(View.VISIBLE);
        animator.start();
        visible=true;
    }
    //funcion para que se oculte el layout al presionar el boton adjuntar por segunda vez
    private void ocultarLayout(){
        RelativeLayout view= findViewById(R.id.datosLayout);
        float radius= Math.max(view.getWidth(),view.getHeight());
        Animator animator= ViewAnimationUtils.createCircularReveal(view,view.getLeft(), view.getTop(),radius*2,0);
        animator.setDuration(800);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        visible = false;
    }
        //funcion que se encarga de subir una imagen a la base de datos usando firebase Storage para que se muestre en el chat
    private void uploadImage(String userid){
        final ProgressDialog pd= new ProgressDialog(MensajeActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(userid);
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask= fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Chats");

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("emisor",firebaseUser.getUid());
                        map.put("receptor",userid);
                        map.put("mensaje",mUri);
                        map.put("visto",false);
                        map.put("type","image");
                        reference.push().setValue(map);
                        ocultarLayout();
                        final String msg = "Foto";
                        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Usuario user = snapshot.getValue(Usuario.class);
                                if (notifcar)
                                enviarNotificacion(userid, user.getUsuario(), msg);
                                notifcar=false;
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        pd.dismiss();
                    }else{
                        Toast.makeText(MensajeActivity.this,"Falló" ,Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MensajeActivity.this ,e.getMessage(),Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(this,"No has seleccionado ninguna imagen", Toast.LENGTH_LONG).show();
        }
    }
    //obtiene la extencion del archivo que se quiere almacenar en la base de datos
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    //convierte un bitmap obtenido en el activity result en un uri para que sea almacenado en la base de datos
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    //verifica el estado del ultimo mensaje para saber si esta entregado o leido
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
    //pide permiso para poder almacenar las fotos
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void pedirPermisoStorage() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }

    //verifica que el permiso de almacenamiento este concedido
    private boolean checkStoragePermiso() {
        boolean res2= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return  res2;
    }
    //pide permiso de camara y el de almacenamiento para poder usar la camara
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void pedirPermisoCamara() {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
    }
    //verifica que el permiso de camara y el de almacenamiento este concedido
    private boolean checkCameraPermiso() {
        boolean res1= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        boolean res2= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
        return res1 && res2;
    }

}