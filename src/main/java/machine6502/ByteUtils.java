package machine6502;

public class ByteUtils {
    public static int unsignedToSigned(int byteval) {
        if (byteval >= 0x80) {
            return byteval - 256;
        } else {
            return byteval;
        }
    }
}
