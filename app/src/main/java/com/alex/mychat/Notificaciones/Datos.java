package com.alex.mychat.Notificaciones;

public class Datos {
    private String usuario;
    private int icono;
    private String cuerpo;
    private String titilo;
    private String enviado;

    public Datos(String usuario, int icono, String cuerpo, String titilo, String enviado) {
        this.usuario = usuario;
        this.icono = icono;
        this.cuerpo = cuerpo;
        this.titilo = titilo;
        this.enviado = enviado;
    }

    public Datos() {
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

    public String getTitilo() {
        return titilo;
    }

    public void setTitilo(String titilo) {
        this.titilo = titilo;
    }

    public String getEnviado() {
        return enviado;
    }

    public void setEnviado(String enviado) {
        this.enviado = enviado;
    }
}
