package connection;

import com.Packet;
import main.User;

public class SenderThread implements Runnable {
    final private User u1, u2;

    public SenderThread(User u1, User u2) {
        this.u1 = u1;
        this.u2 = u2;
    }
    @Override
    public void run() {
        System.out.println("Running SenderThread for " + u1.getName());

        while(true) {
            Packet packet = u1.receive();

//            if(packet == null) {
//                System.out.println("Sender received bad packet");
//                continue;
//            } else {
//                System.out.println("Received packet");
//            }

            // Check for disconnect packet
            if(packet.getProtocol().equals("DISCONNECT")) {
                break;
            }

            // Forward message
            System.out.println("Forwarding " + packet.getData());
            u2.send(packet);
        }
    }
}
