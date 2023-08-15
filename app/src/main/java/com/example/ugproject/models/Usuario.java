package com.example.ugproject.models;

public class Usuario {

    private Integer id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String celular;
    private String direccion;
    private String carrera;
    private Integer semestre;
    private String titulo;
    private String foto;
    private String saludo;

    private String latitud_gps;

    private String longitud_gps;



    public Usuario(

    ) {
        this.id = 0;
        this.cedula = "";
        this.nombre = "";
        this.apellido = "";
        this.correo = "";
        this.celular = "";
        this.direccion = "";
        this.carrera = "";
        this.semestre = 0;
        this.titulo = "";
        this.foto = "";
        this.saludo = "";
        this.latitud_gps = "";
        this.longitud_gps = "";
    }

    public Usuario(
            Integer id,
            String cedula,
            String nombre,
            String apellido,
            String correo,
            String celular,
            String direccion,
            String carrera,
            Integer semestre,
            String titulo,
            String foto,
            String saludo,
            String latitud_gps,
            String longitud_gps
    ) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.celular = celular;
        this.direccion = direccion;
        this.carrera = carrera;
        this.semestre = semestre;
        this.titulo = titulo;
        this.foto = foto;
        this.saludo = saludo;
        this.latitud_gps = latitud_gps;
        this.longitud_gps = longitud_gps;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getSaludo() {
        return saludo;
    }

    public void setSaludo(String saludo) {
        this.saludo = saludo;
    }

    public String getLatitud_gps() {
        return latitud_gps;
    }

    public void setLatitud_gps(String latitud_gps) {
        this.latitud_gps = latitud_gps;
    }

    public String getLongitud_gps() {
        return longitud_gps;
    }

    public void setLongitud_gps(String longitud_gps) {
        this.longitud_gps = longitud_gps;
    }
}
