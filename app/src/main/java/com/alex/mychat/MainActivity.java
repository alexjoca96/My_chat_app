package com.alex.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alex.mychat.Adapters.AdapterViewPager;
import com.alex.mychat.Fragmentos.FragmentoChats;
import com.alex.mychat.Fragmentos.FragmentoUsuarios;
import com.alex.mychat.Modelo.Usuario;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView perfil_imagen;
    TextView usuario;

    private ArrayList<String> titulos;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("");


        perfil_imagen = findViewById(R.id.perfil_imagen);
        usuario =findViewById(R.id.usuario);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);
                assert user != null;
                usuario.setText(user.getUsuario());
                assert user.getImagenURL() != null;
                    if (user.getImagenURL().equals("default")) {
                        perfil_imagen.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImagenURL()).into(perfil_imagen);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TabLayout tabLayout =findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        AdapterViewPager adapterViewPager= new AdapterViewPager(getSupportFragmentManager(),getLifecycle());
        adapterViewPager.addFragmento(new FragmentoChats(),"Chats");
        adapterViewPager.addFragmento(new FragmentoUsuarios(),"Usuarios");
        viewPager2.setAdapter(adapterViewPager);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) ->  {
            titulos= adapterViewPager.getTitulos();
           tab.setText(titulos.get(position));
        }).attach();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,InicioActivity.class));
                finish();
                return true;
        }
        return false;
    }

}