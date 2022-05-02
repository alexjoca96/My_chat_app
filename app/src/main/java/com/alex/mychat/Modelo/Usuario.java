package com.alex.mychat.Modelo;

public class Usuario {
    private String id;
    private String usuario;
    private String imagenURL;
    private String estado;

    public Usuario(String id, String usuario, String imagenURL ,String estado) {
        this.id = id;
        this.usuario = usuario;
        this.imagenURL = imagenURL;
        this.estado = estado;
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
}
