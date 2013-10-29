package memory;

import machine6502.Memory;

public class Split implements Memory {
    private Memory mem;
    private int lo;
    private int length;

    public Split(Memory mem, int lo, int length) {
        this.mem = mem;
        this.lo = lo;
        this.length = length;
    }
    
    @Override
    public void writeByte(int addr, int value) {
        assert addr < this.length;
        mem.writeByte(addr+this.lo, value);
    }
    
    @Override
    public int readByte(int addr) {
        assert addr < this.length;
        return mem.readByte(addr+this.lo);
    }
}
