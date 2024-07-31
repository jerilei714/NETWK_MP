package client;
import java.io.*;
import java.net.*;

public class FileExchangeClient {
    private static final String HOST = "127.0.0.1"; // Localhost
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT)) {

            System.out.println("Connected to the server");

            new ReadThread(socket).start();  // Thread to read server messages
            new WriteThread(socket).start(); // Thread to send messages to the server

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}

