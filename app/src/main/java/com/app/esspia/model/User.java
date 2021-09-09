package com.app.esspia.model;

public class User {

    private int cedula;
    private String name;
    private String email;
    private int phone;

    public User(int cedula, String name, String email, int phone) {
        this.cedula = cedula;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User( ) {

    }

    public int getCedula() {
        return cedula;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }
}
