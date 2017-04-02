package com.giansoft.cryptedchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

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

    public boolean write(Msg message){
        try {
            out.writeObject(message);
            out.flush();
            return true;
        } catch (Exception e){
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

    public Object read(){
        try {
            return in.readObject();
        } catch (Exception e){
            System.err.println("[-] Error: " + e);
            return null;
        }
    }

    public boolean close(){
        try{
            in.close();
            out.close();
            return true;
        } catch (Exception e){
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

}
