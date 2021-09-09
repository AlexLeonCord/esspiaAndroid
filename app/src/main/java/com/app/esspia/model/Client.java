package com.app.esspia.model;

public class Client {
    private int id;
    private String tipoId;
    private String compania;
    private String representante;
    private int idRepresentante;
    private String email;
    private int telefono;
    private String direccion;

    public Client() {

    }

    public Client(int id, String tipoId, String compania, String representante, int idRepresentante, String email, int telefono, String direccion) {
        this.id = id;
        this.tipoId = tipoId;
        this.compania = compania;
        this.representante = representante;
        this.idRepresentante = idRepresentante;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipoId() {
        return tipoId;
    }

    public void setTipoId(String tipoId) {
        this.tipoId = tipoId;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public int getIdRepresentante() {
        return idRepresentante;
    }

    public void setIdRepresentante(int idRepresentante) {
        this.idRepresentante = idRepresentante;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
