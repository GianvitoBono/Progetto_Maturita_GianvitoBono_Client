package com.giansoft.cryptedchat;

/**
 * Created by Gianvito on 14/02/2017.
 */

public class User {
    private String name;
    private String surname;
    private String tel;

    public User(String name, String surname, String tel){
        this.name = name;
        this.surname = surname;
        this.tel = tel;
    }

    public User(){
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getTel() {
        return tel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
