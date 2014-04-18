package memory;

import machine6502.Memory;

public class RAM implements Memory {
    private int[] ram;
    
    public RAM(int size) {
        this.ram = new int[size];
    }
    
    public RAM(int[] ram) {
        this.ram = ram;
    }
    
    @Override
    public int readByte(int addr) {
        return ram[addr];
    }

    @Override
    public void writeByte(int addr, int value) {
        ram[addr] = value;
    }
}
