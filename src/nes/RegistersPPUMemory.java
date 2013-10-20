package nes;

import nes.ppu.PPU;
import machine6502.Memory;

public class RegistersPPUMemory implements Memory {
    private static final int PPUCTRL = 0;
    private static final int PPUMASK = 1;
    private static final int PPUSTATUS = 2;
    private static final int OAMADDR = 3;
    private static final int OAMDATA = 4;
    private static final int PPUSCROLL = 5;
    private static final int PPUADDR = 6;
    private static final int PPUDATA = 7;
    
    private PPU ppu;

    public RegistersPPUMemory(PPU ppu) {
        this.ppu = ppu;
    }

    @Override
    public int readByte(int addr) {
        //System.out.printf("PPU read: %04X\n", addr+0x2000);
        
        switch (addr) {
        case PPUSTATUS:
            return ppu.readStatus();
        case OAMDATA:
            break;
        case PPUDATA:
            return ppu.readPPUData();
        }
        
        assert addr <= PPUDATA;
        return 0;
    }

    @Override
    public void writeByte(int addr, int value) {
        //System.out.printf("PPU write: %04X (%02X)\n", addr+0x2000, value);
        
        switch (addr) {
        case PPUCTRL:
            ppu.writePCR1(value);
            break;
        case PPUMASK:
            ppu.writePCR2(value);
            break;
        case OAMADDR:
            break;
        case OAMDATA:
            break;
        case PPUSCROLL:
            ppu.writePPUScroll(value);
            break;
        case PPUADDR:
            ppu.writePPUAddr(value);
            break;
        case PPUDATA:
            ppu.writePPUData(value);
            break;
        }
        
        assert addr <= PPUDATA;
    }

}
