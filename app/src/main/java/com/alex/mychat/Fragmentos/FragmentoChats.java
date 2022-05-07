package com.alex.mychat.Fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alex.mychat.Adapters.UsuarioAdapter;
import com.alex.mychat.Modelo.Chat;
import com.alex.mychat.Modelo.Usuario;
import com.alex.mychat.Notificaciones.Token;
import com.alex.mychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;


public class FragmentoChats extends Fragment {
    public static final String TITLE="TITLE";
    private RecyclerView recyclerView;
    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> nUsuarios;
    private List<String> userList;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_fragmento_chats, container, false);
        recyclerView= view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList= new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getEmisor().equals(firebaseUser.getUid())){
                        userList.add(chat.getReceptor());
                    }
                    if (chat.getReceptor().equals(firebaseUser.getUid())){
                        userList.add(chat.getEmisor());
                    }
                }
                leerChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getContext(), "Algo fallo", Toast.LENGTH_SHORT).show();
                    return;
                }
                String token = task.getResult();
                ActualizarTokens(FirebaseMessaging.getInstance().getToken().toString());
            }
        });

        return view;
    }
    private void ActualizarTokens(String reftoken) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(reftoken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }

    private void leerChats() {
        nUsuarios= new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               nUsuarios.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Usuario user = snapshot1.getValue(Usuario.class);

                    for (String id : userList){
                        assert user != null;
                        if (user.getId().equals(id)){
                            if (nUsuarios.size() != 0){

                                    if (!nUsuarios.contains(user)){
                                        nUsuarios.add(user);
                                    }

                            }else{
                                nUsuarios.add(user);
                            }
                        }
                    }
                }
                usuarioAdapter= new UsuarioAdapter(getContext(),nUsuarios, true);
                recyclerView.setAdapter(usuarioAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}