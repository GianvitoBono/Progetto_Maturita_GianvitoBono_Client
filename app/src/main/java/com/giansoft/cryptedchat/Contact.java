package com.giansoft.cryptedchat;

import java.io.Serializable;

/**
 * Created by gianv on 22/03/2017.
 */

public class Contact implements Serializable{
    private String name;
    private String surname;
    private String username;
    private String tel;
    static final long serialVersionUID = 151836L;

    public Contact(String name, String surname, String username) {
        this.name = name;
        this.surname = surname;
        this.username = username;
    }

    public Contact(String name, String surname, String username, String tel) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.tel = tel;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
