package connection;

import com.Packet;
import encryption.RSA;
import main.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements Runnable {
    private ReentrantLock lock;
    private List<User> users;
    final int id;
    volatile int numUsers = 0;

    public Connection(int id) {
        this.id = id;
        users = new ArrayList<>();
        lock = new ReentrantLock();
    }

    /**
     * Registers user with system and adds to connection
     */
    public void addUser(User newUser) {
        // Adding user should be an atomic operation
        lock.lock();
        try {
            if(users.size() >= 2) {
                System.out.println("Max users already connected");
                lock.unlock();
                return;
            }

            // Register user
            Register reg = new Register(newUser);

            boolean registered = reg.run();

            // Get updated user
            newUser = reg.getUser();

            // Check if registration was successful
            if(registered) {
                users.add(newUser);
                numUsers++;
                System.out.println("User registered. Total user: " + users.size());
            } else {
                System.out.println("Error registering");
                Packet err = new Packet("ERROR");
                err.addData("Error registering");
                newUser.send(err);
                newUser.endConnection();
            }
        } finally {
            lock.unlock();
        }
    }

    public int getNumUsers() {
        return numUsers;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        // Spinner to wait for second user
        while (numUsers < 2) {
            Thread.onSpinWait();
        }

        communicate();
    }

    private void communicate() {
        System.out.println("Two users connected, communicating");

        // For testing, send messages between users
        // Wait for messages from users
        List<Thread> threads = new ArrayList<>();
        MessageReader R1 = new MessageReader(users.get(1), users.get(0));
        threads.add(new Thread(R1));
//
        for(Thread t : threads) {
            t.setDaemon(true);
            t.start();
        }
    }

    /**
     * Initiates secret sharing between clients. Swaps RSA keys, symmetric keys, user info
     */
    private void shareSecrets() {
        // First, send
    }
}
