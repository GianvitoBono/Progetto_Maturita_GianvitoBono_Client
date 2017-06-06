package com.giansoft.cryptedchat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gianv on 02/04/2017.
 */

public class Msg implements Serializable{
    private int id;
    private ArrayList<Object> data;
    static final long serialVersionUID = 151836L;
    private String message;
    private String datetime;

    public Msg(int id, ArrayList<Object> data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Object> getData() {
        return data;
    }

    public void setData(ArrayList<Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
