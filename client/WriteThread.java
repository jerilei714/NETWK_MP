package client;

import java.io.*;
import java.net.*;

class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private String userHandle;

    public WriteThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            Console console = System.console();
            String text;

            do {
                text = console.readLine("Enter command: ");
                if (text.startsWith("/join ")) {
                    System.out.println("Already connected to the server.");
                } else if (text.equalsIgnoreCase("/leave")) {
                    writer.println("/leave");
                    socket.close();
                    System.out.println("Disconnected from the server.");
                    break;
                } else if (text.startsWith("/register ")) {
                    String[] parts = text.split(" ", 2);
                    if (parts.length > 1) {
                        userHandle = parts[1];
                        writer.println("/register " + userHandle);
                    } else {
                        System.out.println("Handle is required.");
                    }
                } else if (text.startsWith("/store ")) {
                    String[] parts = text.split(" ", 2);
                    if (parts.length > 1) {
                        sendFileToServer(parts[1]);
                    } else {
                        System.out.println("Filename is required.");
                    }
                } else if (text.equalsIgnoreCase("/dir")) {
                    writer.println("/dir");
                } else if (text.startsWith("/get ")) {
                    String[] parts = text.split(" ", 2);
                    if (parts.length > 1) {
                        writer.println("/get " + parts[1]);
                        receiveFile(parts[1]);
                    } else {
                        System.out.println("Filename is required.");
                    }
                } else if (text.equalsIgnoreCase("/?")) {
                    displayHelp();
                } else {
                    System.out.println("Unknown command. Type /? for help.");
                }

            } while (!text.equalsIgnoreCase("/leave"));

        } catch (IOException ex) {
            System.out.println("Error in client writer: " + ex.getMessage());
        }
    }

    private void sendFileToServer(String filename) {
        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            writer.println("/store " + filename);
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                bos.flush();
                System.out.println("File " + filename + " sent successfully.");
            } catch (IOException e) {
                System.out.println("Error sending file: " + e.getMessage());
            }
        } else {
            System.out.println("File not found.");
        }
    }

    private void receiveFile(String filename) {
        try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
             FileOutputStream fos = new FileOutputStream("client_files/" + filename)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("File " + filename + " received successfully.");
        } catch (IOException e) {
            System.out.println("Error receiving file: " + e.getMessage());
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("/join <server_ip> <port> - Connect to the server");
        System.out.println("/leave - Disconnect from the server");
        System.out.println("/register <handle> - Register a unique handle or alias");
        System.out.println("/store <filename> - Send file to the server");
        System.out.println("/dir - Request directory list from the server");
        System.out.println("/get <filename> - Fetch file from the server");
        System.out.println("/? - Display help");
    }
}


