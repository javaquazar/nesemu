package nes.mappers;

import nes.ROM;
import machine6502.Memory;
import memory.Segmented;

public class NROM implements Mapper {
    private ROM rom;

    public NROM(ROM rom) {
        this.rom = rom;
    }

    @Override
    public Memory getSRAMMemory() {
        return new memory.Zero();
    }

    @Override
    public Memory getPRGMemory() {
        Segmented mem = new Segmented();
        
        int prgCount = rom.getPRGCount();
        
        if (prgCount == 1) {
            machine6502.Memory prg0 = rom.getPRG(0);
            mem.addSegment(0x0000, 0x3FFF, prg0);
            mem.addSegment(0x4000, 0x7FFF, prg0);
        } else if (prgCount == 2) {
            mem.addSegment(0x0000, 0x3FFF, rom.getPRG(0));
            mem.addSegment(0x4000, 0x7FFF, rom.getPRG(1));
        } else {
            throw new IllegalStateException();
        }
        
        return mem;
    }

    @Override
    public Memory getCHRMemory() {
        return rom.getCHR(0);
    }
}
