package com.alex.mychat.Modelo;

public class Chat {
    private String emisor;
    private String receptor;
    private String mensaje;
    private boolean visto;
    private String type;

    public Chat() {
    }

    public Chat(String emisor, String receptor, String mensaje, boolean visto, String type) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.visto= visto;
        this.type = type;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
