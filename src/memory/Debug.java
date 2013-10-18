package memory;

import machine6502.Memory;

/**
 * Ensures that values and addresses being read and written are in the correct
 * ranges.
 * <p>
 * All addresses must be unsigned 16-bit ($0000..$FFFF)<br>
 * All values must be unsigned 8-bit ($00..$FF)
 */
public class Debug implements Memory {
    private Memory mem;
    
    public Debug(Memory mem) {
        this.mem = mem;
    }

    @Override
    public int readByte(int addr) {
        assertAddress(addr);
        int value = mem.readByte(addr);
        
        assertValue(addr, value);
        return value;
    }

    @Override
    public void writeByte(int addr, int value) {
        assertAddress(addr);
        assertValue(addr, value);
        mem.writeByte(addr, value);
    }
    
    private static void assertAddress(int addr) {
        // address must be in the short range (16-bit value, $0000..$FFFF)
        assert addr >= 0x0000 && addr <= 0xFFFF :
            String.format("Address is not a short ($%04X)", addr);
    }

    private static void assertValue(int addr, int value) {
        // value must be in the byte range (8-bit value, $00..$FF)
        assert value >= 0x00 && value <= 0xFF :
            String.format("Value is not a byte (#$%02x at $%04X)", value, addr);
    }
    
}
