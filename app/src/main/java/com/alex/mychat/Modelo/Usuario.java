package com.alex.mychat.Modelo;

public class Usuario {
    private String id;
    private String usuario;
    private String imagenURL;
    private String estado;
    private String token;

    public Usuario(String id, String usuario, String imagenURL ,String estado, String token) {
        this.id = id;
        this.usuario = usuario;
        this.imagenURL = imagenURL;
        this.estado = estado;
        this.token= token;
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

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
