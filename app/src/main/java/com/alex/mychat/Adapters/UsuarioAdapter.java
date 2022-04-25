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
import com.alex.mychat.Modelo.Usuario;
import com.alex.mychat.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter <UsuarioAdapter.MyViewHolder> {
    @NonNull
    private Context context;
    private List<Usuario> nUsuarios;

    public UsuarioAdapter(Context context, List<Usuario> nUsuarios) {
        this.context = context;
        this.nUsuarios = nUsuarios;
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
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usuario =itemView.findViewById(R.id.usuario_item);
            perfil_imagen = itemView.findViewById(R.id.perfil_imagen_item);

        }
    }
}
