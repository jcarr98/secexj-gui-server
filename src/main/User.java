package main;

import com.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

public class User {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;
    private PublicKey publicKey;

    public User(Socket socket) {
        this.socket = socket;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Stream created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKey(PublicKey key) {
        publicKey = key;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public boolean send(Packet packet) {
        return send(packet, 5);
    }

    public boolean send(Packet packet, int maxTries) {
        boolean sent = false;
        int count = 0;
        while(!sent && count < maxTries) {
            try {
                out.flush();
                out.writeObject(packet);
                sent = true;
            } catch(IOException e) {
                System.out.println("Sending failed, retrying...");
                count++;
            }
        }

        return sent;
    }

    public Packet receive() {
        Packet packet = null;
        try {
            packet = (Packet) in.readObject();
            if(packet == null) {
                System.out.println("Received nothing");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public void endConnection() {
        boolean connected = true;
        while(connected) {
            try {
                socket.close();
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Ended connection");
    }
}
