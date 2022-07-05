package com.alex.mychat.Notificaciones;

import com.alex.mychat.Notificaciones.Emisor;
import com.alex.mychat.Notificaciones.MiRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {       "Content-Type: application/json",
                    "Authorization: key=AAAAGpKJsBQ:APA91bFkfy2FXrOhO87qfxbgrvgqsb0grt2TLin3fzMdq9yzzGtYCwyFle1uiFBkd0VZSGwyGjWMO4xAj2A46wRvD2ZMnnQhAq9-scyeOpYeSF-rR4qNqMhjkBj5qlMlsJlM2dz_5fgu"}

    )
    @POST("fcm/send")
    Call<MiRespuesta> enviarNotificacion(@Body Emisor body);
}
