package com.alex.mychat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alex.mychat.Modelo.Chat;
import com.alex.mychat.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder>{
    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    FirebaseUser firebaseUser;

    public MensajeAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mContext = mContext;
        this.mChat= mChat;
        this.imageurl= imageurl;
    }



    @NonNull
    @Override
    public MensajeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_derecho, parent, false);
            return new MensajeAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_izquierdo, parent, false);
            return new MensajeAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.mostrar_mensaje.setText(chat.getMensaje());
        if (imageurl.equals("default")){
            holder.perfil_imagen.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(imageurl).into(holder.perfil_imagen);
        }

        if (position == (mChat.size()-1)){
            if (chat.isVisto()){
                holder.visto.setText("visto");
            }else{
                holder.visto.setText("entregado");
            }
        }else {
            holder.visto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mostrar_mensaje;
        public ImageView perfil_imagen;
        public TextView visto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mostrar_mensaje =itemView.findViewById(R.id.mostrar_mensaje);
            perfil_imagen = itemView.findViewById(R.id.perfil_imagen);
            visto= itemView.findViewById(R.id.visto);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getEmisor().equals(firebaseUser.getUid())) return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;
    }
}
