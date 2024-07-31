package server;


import java.io.*;
import java.net.*;

class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String clientMessage;

            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Received: " + clientMessage);
                if (clientMessage.startsWith("/store ")) {
                    String filename = clientMessage.substring(7);
                    receiveFile(filename);
                } else if (clientMessage.startsWith("/get ")) {
                    String filename = clientMessage.substring(5);
                    sendFile(filename);
                } else if (clientMessage.equals("/dir")) {
                    listFiles();
                } else if (clientMessage.equals("/leave")) {
                    socket.close();
                    break;
                } else {
                    writer.println("Unknown command.");
                }
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }

    private void receiveFile(String filename) {
        try (FileOutputStream fos = new FileOutputStream("server_files/" + filename);
             BufferedInputStream bis = new BufferedInputStream(socket.getInputStream())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            writer.println("File " + filename + " stored successfully.");
        } catch (IOException e) {
            writer.println("Error storing file: " + e.getMessage());
        }
    }

    private void sendFile(String filename) {
        try (FileInputStream fis = new FileInputStream("server_files/" + filename);
             BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
            writer.println("File " + filename + " sent successfully.");
        } catch (IOException e) {
            writer.println("Error sending file: " + e.getMessage());
        }
    }

    private void listFiles() {
        File folder = new File("server_files");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    writer.println(file.getName());
                }
            }
        } else {
            writer.println("No files found.");
        }
    }
}

