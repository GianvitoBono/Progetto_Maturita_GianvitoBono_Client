package com.giansoft.cryptedchat;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;

/**
 * Created by Gianvito on 27/11/2016.
 */

public class IOManager {

    public final static int JSON = 1;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private BufferedReader sIn;
    private PrintWriter sOut;

    public IOManager(Socket socket) {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IOManager(Socket socket, int mode) {
        try {
            this.sIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.sOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
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

    public boolean writeJSON(Msg JSON) {
        try {
            String objToJSON = new Gson().toJson(JSON);
            Log.d("IOManager", objToJSON);
            sOut.println(objToJSON);//Crypter.encrypt(objToJSON));
            sOut.close();
            return true;
        } catch (Exception e) {
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

    public Msg readJSON() {
        try {
            String objJSON = sIn.readLine();//Crypter.decrypt(sIn.readLine());
            Gson gson = new Gson();
            return gson.fromJson(objJSON, Msg.class);
        } catch (Exception e) {
            Log.i("RM", "error", e);
            System.err.println("[-] Error: " + e.getStackTrace());
            return null;
        }
    }

}
