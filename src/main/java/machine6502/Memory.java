package machine6502;

/**
 * Reads and writes values in memory.
 * <p>
 * The address needs to be a 16-bit value (0x0000 - 0xFFFF), as the NES and 6502
 * use a 16-bit address space.
 * <p>
 * Note that reads and writes may have side-effects.
 * In other words, subsequent reads and writes may not have the same effect.
 *
 */
public interface Memory {
    /**
     * Called when the 6502 machine requests memory
     * 
     * @param addr A 16-bit address ($0000..$FFFF)
     * @return The 8-bit value at addr ($00..$FF)
     */
    public int readByte(int addr);
    /**
     * Called when the 6502 machine writes to memory
     * 
     * @param addr A 16-bit address ($0000..$FFFF)
     * @param value The 8-bit value to write ($00..$FF)
     */
    public void writeByte(int addr, int value);
}
