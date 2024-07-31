package client;

import java.io.*;
import java.net.*;

class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;

    public ReadThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));

            String response;
            while ((response = reader.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException ex) {
            System.out.println("Server closed connection: " + ex.getMessage());
        }
    }
}