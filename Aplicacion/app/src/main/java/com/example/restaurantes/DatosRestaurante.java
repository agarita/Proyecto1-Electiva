package com.example.restaurantes;

public class DatosRestaurante{
    private double latitud;
    private double longitud;
    private String Nombre;
    private String Horario;
    private String Telefono;
    private String Correo;
    private String Precio;
    private String TipoComida;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getHorario() {
        return Horario;
    }

    public String getTelefono() {
        return Telefono;
    }

    public String getPrecio() {
        return Precio;
    }

    public String getTipoComida() {
        return TipoComida;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setHorario(String horario) {
        Horario = horario;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public void setTipoComida(String tipoComida) {
        TipoComida = tipoComida;
    }
}
