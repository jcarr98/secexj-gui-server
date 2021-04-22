package encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class RSA {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher cipher;

    public RSA() {
        // Create public/private key pairs
        KeyPair pair = generateKeys();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();

        // Create cipher
        try {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        } catch (GeneralSecurityException e) {
            System.out.println("Error creating cipher");
            return;
        }
    }

    public RSA(String key) {

    }

    private KeyPair generateKeys() {
        KeyPairGenerator gen;

        try {
            gen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error creating key generator");
            return null;
        }

        // Initialize key generator
        gen.initialize(2048, new SecureRandom());

        // Create keypair
        return gen.generateKeyPair();
    }

    /**
     * Get the public key for this RSA object
     * @return PublicKey
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Encrypt the provided plaintext using the provided PublicKey
     * @param plaintext Plaintext to encrypt
     * @param key Key to encrypt plaintext with
     * @return An array of bytes representing the encrypted plaintext
     */
    public byte[] encrypt(String plaintext, PublicKey key) {
        try {
            return encrypt(plaintext.getBytes("UTF-8"), key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypts the provided bytes using the provided PublicKey
     * @param plaintext Bytes to encrypt
     * @param key Key to encrypt with
     * @return An array of bytes representing the encrypted plaintext
     */
    public byte[] encrypt(byte[] plaintext, PublicKey key) {
        // Initialize cipher in encrypt mode and encrypt with cipher
        byte[] ciphertext;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ciphertext = cipher.doFinal(plaintext);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }

        return ciphertext;
    }

    /**
     * Decrypt the provided ciphertext using public key belonging to this RSA object
     * @param ciphertext Ciphertext to decrypt
     * @return A plaintext string of the provided ciphertext
     */
    public String decrypt(byte[] ciphertext) {
        // Initialize cipher in decrypt mode and encrypt with cipher
        String plaintext;
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] plaintextBytes = cipher.doFinal(ciphertext);
            plaintext = new String(plaintextBytes, "UTF-8");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        return plaintext;
    }
}
