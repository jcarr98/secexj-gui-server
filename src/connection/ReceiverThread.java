package connection;

import com.Packet;
import main.User;

public class ReceiverThread implements Runnable {
    final private User u1, u2;

    public ReceiverThread(User u1, User u2) {
        this.u1 = u1;
        this.u2 = u2;
    }

    @Override
    public void run() {
        System.out.println("Running receiver thread for " + u1.getName());
        while(true) {
            Packet packet = u2.receive();

//            if(packet == null) {
//                System.out.println("Receiver received bad packet");
//                continue;
//            } else {
//                System.out.println("Received packet");
//            }

            if(packet.getProtocol().equals("DISCONNECT")) {
                break;
            }

            u1.send(packet);
        }
    }
}
