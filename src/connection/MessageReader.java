package connection;

import com.Packet;
import main.User;

public class MessageReader implements Runnable {
    final private User user1, user2;

    public MessageReader(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    @Override
    public void run() {
        while(true) {
            // Wait for messages from user and print them out
            Packet packet = user1.receive();

            System.out.println("Forwarding: " + packet.getData());
            user2.send(packet);
        }
    }
}
