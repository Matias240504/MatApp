package com.barcozeg.proyecto_prueba.Clases;

import java.io.Serializable;

public class Cliente implements Serializable {
    String id_cliente;
    String uid_cliente;
    String nombres;
    String apellidos;
    String correo;
    String telefono;
    String dni;
    String direccion;

    public Cliente(){}

    public Cliente(String id_cliente, String uid_cliente, String nombres, String apellidos, String correo, String telefono, String dni, String direccion){
        this.id_cliente = id_cliente;
        this.uid_cliente = uid_cliente;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.dni = dni;
        this.direccion = direccion;
    }


    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getUid_cliente() {
        return uid_cliente;
    }

    public void setUid_cliente(String uid_cliente) {
        this.uid_cliente = uid_cliente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

}
