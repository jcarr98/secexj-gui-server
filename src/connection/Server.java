package connection;

import com.Packet;
import encryption.RSA;
import main.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements Runnable {
    private ServerSocket socket;
    private Map<Integer, Connection> connections;
    private List<Thread> threads;

    public Server(int port) {
        connections = new HashMap<>();
        threads = new ArrayList<>();

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                // Wait for connection
                System.out.println("Waiting for a connection");
                Socket connectedSocket = socket.accept();
                System.out.println("Connection from " + connectedSocket.getInetAddress());

                // Check how many threads are running to avoid overloading the server
                if(threads.size() >= 10) {
                    System.out.println("Maximum threads already reached");
                    closeConnection(connectedSocket, "Server busy");
                    continue;
                }

                // Check connection id
                Connection connection = getConnection(connectedSocket);
                if(connection == null) {
                    closeConnection(connectedSocket, "Bad Connection");
                    continue;
                }

                connections.put(connection.getId(), connection);
            } catch(SocketException f) {
                break;
            } catch (IOException e) {
                System.out.println("Error accepting connections...");
            }
        }

        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads in the first packet and checks if this connection ID is already in use
     * @return Less than 0 if error in communication, 0 if not in use, connection id if in use
     */
    private Connection getConnection(Socket connected) throws IOException {
        System.out.println("Checking connection...");
        // First, create user (to save input/output streams)
        User user = new User(connected);

        // Get registration packet from user
        Packet data;
        data = user.receive();

        // Check proper protocol, should be REGISTER
        if(data.getProtocolCode() != 1) {
            System.out.println("Improper protocol");
            return null;
        }

        // Check connection ID
        // First packet should be CLIENT_HELLO|connection id
        String sid;
        try {
            sid = data.getData(1);
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Connection ID not provided");
            return null;
        }

        // Check for valid connection id
        int cid;
        try {
            cid = Integer.parseInt(sid);
        } catch(NumberFormatException e) {
            System.out.println("Invalid connection ID");
            return null;
        }

        // Check if connection id is in use
        if(cid <= 0) {
            System.out.println("Invalid connection ID");
            return null;
        }
        else if(connections.containsKey(cid)) {
            Connection currentConnection = connections.get(cid);
            currentConnection.addUser(user);
            return currentConnection;
        }
        else {
            // Create new connection and add this user to it
            Connection currentConnection = new Connection(cid);
            currentConnection.addUser(user);

            // Create new thread for this connection and start it
            Thread newThread = new Thread(currentConnection);
            newThread.setDaemon(true);
            newThread.start();
            threads.add(newThread);

            return currentConnection;
        }
    }

    private void closeConnection(Socket socket, String message) throws IOException {
        // Create output stream for this socket
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

        // Create packet
        Packet pack = new Packet("ERROR");
        pack.addData(message);

        // Send packet
        out.writeObject(pack);

        // Close socket
        socket.close();
    }
}
