package nes.ppu;

import machine6502.Memory;

final class NametableMemory implements Memory {
    private int[][] nametables;
    
    public NametableMemory() {
        this.nametables = new int[4][];
    }

    public void setNametableRAM(int nametable, int[] ram) {
        assert ram.length == 0x400;
        this.nametables[nametable] = ram;
    }

    @Override
    public void writeByte(int addr, int value) {
        int nametable = addr / 0x400;
        int off = addr % 0x400;
        
        this.nametables[nametable][off] = value;
    }

    @Override
    public int readByte(int addr) {
        int nametable = addr / 0x400;
        int off = addr % 0x400;
        
        return this.nametables[nametable][off];
    }
}