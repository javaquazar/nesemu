package machine6502;

/**
 * <pre>
 * Bit No. 7 6 5 4 3 2 1 0
 *         N V   B D I Z C
 * </pre>
 * N - Negative<br>
 * V - Overflow<br>
 * B - Break<br>
 * D - Decimal (unused on NES)<br>
 * I - Interrupt-disable<br>
 * Z - Zero<br>
 * C - Carry
 *
 */
public class CPUFlags {
    public static final int N = 1<<7;
    public static final int V = 1<<6;
    public static final int RESERVED = 1<<5;
    public static final int B = 1<<4;
    public static final int D = 1<<3;
    public static final int I = 1<<2;
    public static final int Z = 1<<1;
    public static final int C = 1<<0;
}
