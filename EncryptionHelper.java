package Encryption;
import java.security.Security; // Adds and manages security providers.
import javax.crypto.Cipher; // Provides cryptographic functionality for encryption and decryption.
import javax.crypto.SecretKey; // Represents a secret (symmetric) key.
import javax.crypto.spec.IvParameterSpec; // Specifies an initialization vector (IV) for cipher modes that require one.
import javax.crypto.spec.SecretKeySpec; // Converts byte arrays into secret keys.
import org.bouncycastle.jce.provider.BouncyCastleProvider; // Bouncy Castle provider for cryptographic operations.

/*******
* <p> EncryptionHelper Class. </p>
*
* <p> Description:  The EncryptionHelper class provides utility methods for encrypting and 
 * decrypting data using AES (Advanced Encryption Standard) in CBC (Cipher Block Chaining)
 * mode with PKCS5Padding. It uses the Bouncy Castle library as the security provider.
*
* @author <Zach>
* @version 1.00 11/13/2024
*/

public class EncryptionHelper {

    // Identifier for the Bouncy Castle cryptography provider
    private static final String BOUNCY_CASTLE_PROVIDER_IDENTIFIER = "BC";
    
    // Cipher object used for encryption and decryption
    private Cipher cipher;
    
    // Secret key used for encryption and decryption (AES 192-bit key)
    byte[] keyBytes = new byte[] {
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
        0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17
    };
    private SecretKey key = new SecretKeySpec(keyBytes, "AES"); // Create a SecretKey object using the byte array.

    /**
     * Constructor for the EncryptionHelper class. Initializes the cipher object 
     * and adds the Bouncy Castle security provider.
     * 
     * @throws Exception if the cipher cannot be initialized with the given parameters.
     */
    public EncryptionHelper() throws Exception {
        // Add Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());

        // Initialize the cipher object with the transformation AES/CBC/PKCS5Padding
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER_IDENTIFIER);
    }

    /**
     * Encrypts a given plaintext byte array using the provided initialization vector (IV).
     * 
     * @param plainText the data to be encrypted, as a byte array.
     * @param initializationVector the IV used for encryption, as a byte array.
     * @return the encrypted data as a byte array.
     * @throws Exception if encryption fails (e.g., due to incorrect key or IV).
     */
    public byte[] encrypt(byte[] plainText, byte[] initializationVector) throws Exception {
        // Initialize the cipher for encryption mode with the secret key and IV.
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initializationVector));
        
        // Encrypt the plaintext and return the ciphertext.
        return cipher.doFinal(plainText);
    }

    /**
     * Decrypts a given ciphertext byte array using the provided initialization vector (IV).
     * 
     * @param cipherText the data to be decrypted, as a byte array.
     * @param initializationVector the IV used for decryption, as a byte array.
     * @return the decrypted data as a byte array.
     * @throws Exception if decryption fails (e.g., due to incorrect key or IV).
     */
    public byte[] decrypt(byte[] cipherText, byte[] initializationVector) throws Exception {
        // Initialize the cipher for decryption mode with the secret key and IV.
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initializationVector));
        
        // Decrypt the ciphertext and return the plaintext.
        return cipher.doFinal(cipherText);
    }

}