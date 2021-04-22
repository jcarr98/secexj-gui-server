package connection;

import com.Packet;
import encryption.RSA;
import main.User;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Register {
    private User user;

    public Register(User user) {
        this.user = user;
    }

    public boolean run() {
        // Send back a hello from the server
        Packet response = new Packet("REGISTER");
        response.addData("SERVER_HELLO");
        boolean sent = user.send(response, 5);;

        if(!sent) {
            System.out.println("Connection lost");
            return false;
        }

        /*
        * Wait for username, connection id, and public RSA key from user
        * Format should be:
        *     Data: username|RSA key
        * RSA key will be decoded into bytes and Base-64 encoded as a String
        */
        Packet pack = user.receive();

        // Check if packet received successfully
        if(pack == null) {
            error("Error receiving RSA key");
            return false;
        }

        // Save user name
        String keyString;
        try {
            user.setName(pack.getData(0));

            // Save public key
            keyString = pack.getData(1);
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Not enough info sent");
            error("Missing information");
            return false;
        }

        // Convert user's RSA key from String to bytes
        byte[] bKey = Base64.getDecoder().decode(keyString);
        // Convert bytes to public key
        PublicKey uKey;
        try {
            uKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bKey));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            error("Error receiving RSA key");
            return false;
        }
        user.setKey(uKey);

        // Respond to user with success message
        // Success message should contain RSA key: SUCCESS

        Packet success = new Packet("REGISTER");
        success.addData("SUCCESS");
        user.send(success);

        return true;
    }

    public User getUser() {
        return user;
    }

    private boolean error(String message) {
        return error(message, 5);
    }

    private boolean error(String message, int maxTries) {
        // Create error packet
        Packet errPacket = new Packet("ERROR");
        errPacket.addData(message);

        return user.send(errPacket, maxTries);
    }
}
