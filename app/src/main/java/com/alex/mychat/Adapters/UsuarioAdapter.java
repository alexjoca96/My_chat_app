package com.alex.mychat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.mychat.MensajeActivity;
import com.alex.mychat.Modelo.Chat;
import com.alex.mychat.Modelo.Usuario;
import com.alex.mychat.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter <UsuarioAdapter.MyViewHolder> {
    @NonNull
    private Context context;
    private List<Usuario> nUsuarios;
    private boolean en_linea;
    String ultimoMensaje;

    public UsuarioAdapter(Context context, List<Usuario> nUsuarios, boolean en_linea) {
        this.context = context;
        this.nUsuarios = nUsuarios;
        this.en_linea= en_linea;
    }
    public UsuarioAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.usuario_item,parent ,false);
        return new UsuarioAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioAdapter.MyViewHolder holder, int position) {
        Usuario user= nUsuarios.get(position);
        holder.usuario.setText(user.getUsuario());
        if (user.getImagenURL().equals("default")){
            holder.perfil_imagen.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(user.getImagenURL()).into(holder.perfil_imagen);
        }
        if(en_linea){
            ultimoMensaje(user.getId(), holder.ultimo_msg);
        }else {
            holder.ultimo_msg.setVisibility(View.GONE);
        }

        if (en_linea){
            if (user.getEstado().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, MensajeActivity.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);


            }
        });
    }


    @Override
    public int getItemCount() {
        return nUsuarios.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{
        public TextView usuario;
        public ImageView perfil_imagen;
        private ImageView img_on, img_off;
        public TextView ultimo_msg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usuario =itemView.findViewById(R.id.usuario_item);
            perfil_imagen = itemView.findViewById(R.id.perfil_imagen_item);
            img_on= itemView.findViewById(R.id.img_on);
            img_off= itemView.findViewById(R.id.img_off);
            ultimo_msg= itemView.findViewById(R.id.ultimo_msg);

        }
    }

    private void ultimoMensaje(String userid, TextView ultimo_msg){
        ultimoMensaje ="default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if (chat.getReceptor().equals(firebaseUser.getUid()) && chat.getEmisor().equals(userid) ||
                        chat.getReceptor().equals(userid) && chat.getEmisor().equals(firebaseUser.getUid())){
                           ultimoMensaje = chat.getMensaje();
                    }
                }

                switch (ultimoMensaje){
                    case "dafault":
                        ultimo_msg.setText("Sin Mensajes");
                        break;
                    default:
                        ultimo_msg.setText(ultimoMensaje);
                        break;
                }
                ultimoMensaje= "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
