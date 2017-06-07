package com.giansoft.cryptedchat;

import java.net.Socket;

/**
 * Created by gianv on 08/06/2017.
 */

public class ServerThread extends Thread {
    private Socket socket;
    private int mode;
    private IOManager ioManager;

    public ServerThread(Socket socket, int mode) {
        this.socket = socket;
        this.mode = mode;
        ioManager = new IOManager(socket);
    }

    @Override
    public void run() {
        switch (mode) {
            case Utils.HELLO:

                break;
        }
    }
}
