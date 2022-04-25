package com.alex.mychat.Modelo;

public class Usuario {
    private String id;
    private String usuario;
    private String imagenURL;

    public Usuario(String id, String usuario, String imagenURL) {
        this.id = id;
        this.usuario = usuario;
        this.imagenURL = imagenURL;
    }

    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String username) {
        this.usuario = username;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }
}
