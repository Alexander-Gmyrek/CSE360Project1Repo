package Encryption;

import java.nio.ByteBuffer; // Used for handling byte data and conversions.
import java.nio.CharBuffer; // Used for handling char data and conversions.
import java.nio.charset.Charset; // Manages character encoding and decoding.
import java.util.Arrays; // Utility class for handling arrays.

/*******
* <p> EncryptionUtils Class. </p>
*
* <p> Description: EncryptionUtils class provides utility methods for handling byte arrays,
* 	  character arrays, and generating initialization vectors for encryption
*
* @author <Zach>
* @version 1.00 11/13/2024
*/

public class EncryptionUtils {

    // Constant for the size of the initialization vector (16 bytes for AES)
    private static final int IV_SIZE = 16;

    /**
     * Converts a byte array to a character array using the default character set encoding.
     * 
     * @param bytes the byte array to be converted.
     * @return a character array representing the encoded byte data.
     */
    public static char[] toCharArray(byte[] bytes) {
        // Decode the byte array into a CharBuffer using the default charset
        CharBuffer charBuffer = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes));
        
        // Return a copy of the characters in the buffer, truncated to the actual data length
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
    }

    /**
     * Converts a character array to a byte array using the default character set encoding.
     * 
     * @param chars the character array to be converted.
     * @return a byte array representing the encoded character data.
     */
    static byte[] toByteArray(char[] chars) {
        // Encode the character array into a ByteBuffer using the default charset
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(CharBuffer.wrap(chars));
        
        // Return a copy of the bytes in the buffer, truncated to the actual data length
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
    }

    /**
     * Generates an initialization vector (IV) from the provided character array.
     * 
     * This method creates an IV of size 16 bytes (128 bits for AES) by cycling through
     * the characters in the provided text.
     * 
     * @param text the character array from which to derive the IV.
     * @return a byte array representing the initialization vector.
     */
    public static byte[] getInitializationVector(char[] text) {
        // Initialize a char array of size IV_SIZE (16 bytes)
        char[] iv = new char[IV_SIZE];

        // Initialize pointers for text and IV traversal
        int textPointer = 0;
        int ivPointer = 0;

        // Populate the IV by cycling through the characters in the text array
        while (ivPointer < IV_SIZE) {
            iv[ivPointer++] = text[textPointer++ % text.length];
        }

        // Convert the IV (char array) to a byte array and return it
        return toByteArray(iv);
    }

    /**
     * Prints the contents of a character array to the console.
     * 
     * @param chars the character array to be printed.
     */
    public static void printCharArray(char[] chars) {
        // Print each character in the array
        for (char c : chars) {
            System.out.print(c);
        }
    }
}
