package com.alex.mychat.Fragmentos;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mychat.Modelo.Usuario;
import com.alex.mychat.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilFragment extends Fragment {

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    TextView usuario;
    CircleImageView perfil_imagen;
    StorageReference storageReference;
    private static final  int IMAGE_REQUEST=1;
    ActivityResultLauncher<String> takeFoto;

    private Uri imagenUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_perfil, container, false);
        usuario = view.findViewById(R.id.usuario);
        perfil_imagen= view.findViewById(R.id.perfil_imagen);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (isAdded()) {
                   Usuario user = snapshot.getValue(Usuario.class);
                   assert user != null;
                   usuario.setText(user.getUsuario());
                   if (user.getImagenURL().equals("default")) {
                       perfil_imagen.setImageResource(R.mipmap.ic_launcher);
                   } else {
                       Glide.with(getContext()).load(user.getImagenURL()).into(perfil_imagen);
                   }

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        takeFoto= registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        // profile_image.setImageURI(result);
                        imagenUri= result;

                        if (uploadTask!= null && uploadTask.isInProgress()){
                            Toast.makeText(getContext(),"Upload en progreso",Toast.LENGTH_LONG).show();

                        }else {
                            uploadImage();
                        }
                    }
                }
        );


        perfil_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeFoto.launch("image/*");
            }
        });


        return view;
    }

    private void uploadImage(){
        final ProgressDialog pd= new ProgressDialog(getContext());
        pd.setMessage("Subiendo");
        pd.show();

        if (imagenUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imagenUri));
            uploadTask= fileReference.putFile(imagenUri);
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
                        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imagenURL",mUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }else{
                        Toast.makeText(getContext(),"Falló" ,Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(),"No has seleccionado ninguna imagen", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}