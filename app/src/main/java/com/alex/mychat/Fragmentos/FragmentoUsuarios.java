package com.alex.mychat.Fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alex.mychat.Adapters.UsuarioAdapter;
import com.alex.mychat.Modelo.Usuario;
import com.alex.mychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FragmentoUsuarios extends Fragment {

    private RecyclerView recyclerView;
    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> nUsuarios;
    public static final String TITLE="TITLE";
    EditText buscar_usuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_fragmento_usuarios, container, false);
        recyclerView= view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        nUsuarios= new ArrayList<>();
        leerUsuarios();

        buscar_usuario= view.findViewById(R.id.buscar_usuario);
        buscar_usuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    buscarUsuarios(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;

    }

    private void buscarUsuarios(String s) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query= FirebaseDatabase.getInstance().getReference("Usuarios").orderByChild("usuario").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nUsuarios.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Usuario user = snapshot1.getValue(Usuario.class);

                    if (!user.getId().equals(firebaseUser.getUid())){
                        nUsuarios.add(user);
                    }
                }

                usuarioAdapter= new UsuarioAdapter(getContext(),nUsuarios,false);
                recyclerView.setAdapter(usuarioAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void leerUsuarios() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Usuarios");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (buscar_usuario.getText().toString().equals("")) {
                    nUsuarios.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Usuario user = snapshot1.getValue(Usuario.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            nUsuarios.add(user);
                        }

                    }
                    usuarioAdapter = new UsuarioAdapter(getContext(), nUsuarios, false);
                    recyclerView.setAdapter(usuarioAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}