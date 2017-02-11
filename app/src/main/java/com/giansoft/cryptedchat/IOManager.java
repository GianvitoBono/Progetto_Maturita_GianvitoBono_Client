package com.giansoft.cryptedchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Gianvito on 27/11/2016.
 */

public class IOManager {
    private BufferedReader in;
    private PrintWriter out;

    public IOManager(Socket socket) {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean write(String message){
        try {
            out.println(Crypter.encrypt(message));
            return true;
        } catch (Exception e){
            System.err.println("[-] Error: " + e);
            return false;
        }
    }

    public String read(){
        try {
            return Crypter.decrypt(in.readLine());
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
