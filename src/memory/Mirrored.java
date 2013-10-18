package memory;

import machine6502.Memory;

/**
 * Repeats a memory space 
 *
 */
public class Mirrored implements Memory {
    private int length;
    private Memory mem;

    public Mirrored(int length, Memory mem) {
        this.length = length;
        this.mem = mem;
    }

    @Override
    public int readByte(int addr) {
        return mem.readByte(addr % length);
    }

    @Override
    public void writeByte(int addr, int value) {
        mem.writeByte(addr % length, value);
    }
}
