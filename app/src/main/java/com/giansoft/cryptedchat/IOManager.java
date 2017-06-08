package com.giansoft.cryptedchat;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.security.Key;

/**
 * Created by Gianvito on 27/11/2016.
 */

public class IOManager {
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public IOManager(Socket socket) {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean write(Msg message) {
        try {
            out.writeObject(message);
            out.flush();
            return true;
        } catch (Exception e) {
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

    public Object read() {
        try {
            return in.readObject();
        } catch (Exception e) {
            System.err.println("[-] Error: " + e);
            return null;
        }
    }

    public boolean close() {
        try {
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

    public boolean writeCryptedJSON(Msg JSON, Key key) {
        try {
            String objToJSON = new Gson().toJson(JSON);
            out.writeChars(Crypter.encryptWKey(objToJSON, key));
            out.close();
            return true;
        } catch (Exception e) {
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

    public boolean writeJSON(Msg JSON) {
        try {
            String objToJSON = new Gson().toJson(JSON);
            out.writeChars(objToJSON);
            out.close();
            return true;
        } catch (Exception e) {
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

    public boolean write(String msg) {
        try {
            out.writeChars(msg);
            out.close();
            return true;
        } catch (Exception e) {
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

}
