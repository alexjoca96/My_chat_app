package com.alex.mychat.Notificaciones;

public class Data {
    private String usuario;
    private int icono;
    private String cuerpo;
    private String titulo;


    public Data(String usuario, int icono, String cuerpo, String titulo) {
        this.usuario = usuario;
        this.icono = icono;
        this.cuerpo = cuerpo;
        this.titulo = titulo;

    }

    public Data() {
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
