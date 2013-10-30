package nes.mappers;

import machine6502.Memory;
import memory.FixedSegmented;
import nes.ROM;
import nes.ppu.PPU;

public class MMC1 implements Mapper {
    private ROM rom;
    private PPU ppu;
    private Memory prg;
    private FixedSegmented prgSegmented;
    private FixedSegmented chrSegmented;
    
    private int prgROMBankMode, chrROMBankMode;
    
    private int shiftRegister;
    private int shiftRegisterState;

    public MMC1(ROM rom, PPU ppu) {
        this.rom = rom;
        this.ppu = ppu;
        
        prgSegmented = new FixedSegmented(2, 0x4000);
        chrSegmented = new FixedSegmented(2, 0x1000);
        
        // XXX
        prgSegmented.setSegment(0, rom.getPRG(rom.getPRGCount()-2));
        prgSegmented.setSegment(1, rom.getPRG(rom.getPRGCount()-1));
        setCHRBank0(0);
        setCHRBank1(1);
        
        this.prg = new Memory() {
            @Override
            public void writeByte(int addr, int value) {
                boolean clear = (value & 0x80) != 0;
                
                if (!clear) {
                    int bit = value & 0x01;
                    shiftIntoRegister(bit, addr);
                } else {
                    clearRegister();
                }
            }
            
            @Override
            public int readByte(int addr) {
                return prgSegmented.readByte(addr);
            }
        };
        
        clearRegister();
    }

    @Override
    public Memory getSRAMMemory() {
        // XXX
        return new memory.RAM(0x2000);
    }

    @Override
    public Memory getPRGMemory() {
        return this.prg;
    }

    @Override
    public Memory getCHRMemory() {
        return this.chrSegmented;
    }
    
    private void clearRegister() {
        this.shiftRegister = 0;
        this.shiftRegisterState = 0;
    }
    
    private void shiftIntoRegister(int bit, int addr) {
        this.shiftRegister = this.shiftRegister | (bit<<this.shiftRegisterState);
        this.shiftRegisterState++;
        
        if (this.shiftRegisterState == 5) {
            // select into 1 of 4 internal registers
            int select = (addr >> 13)&0x03;
            int r = shiftRegister;
            
            switch (select) {
            case 0: setControl(r); break;
            case 1: setCHRBank0(r); break;
            case 2: setCHRBank1(r); break;
            case 3: setPRGBank(r); break;
            default: throw new IllegalStateException();
            }
            
            clearRegister();
        }
    }

    private void setControl(int r) {
        this.ppu.advance();
        
        // TODO - set mirroring
        //int mirroring = r & 0x03;
        this.prgROMBankMode = (r >> 2)&0x03;
        this.chrROMBankMode = (r >> 4)&0x01;
        
        //this.ppu.setNametableMirroring(horizontal);
    }
    
    private static int off = 16;

    private void setCHRBank0(int r) {
        this.ppu.advance();
        r += off;
        
        if (chrROMBankMode == 1) {
            // switch 4KiB
            Memory m = rom.getROMBank(r, 0x1000);
            
            chrSegmented.setSegment(0, m);
        } else {
            // switch 8Kib
            int b = r & ~0x01;
            chrSegmented.setSegment(0, rom.getROMBank(b, 0x1000));
            chrSegmented.setSegment(1, rom.getROMBank(b, 0x1000));
        }
    }

    private void setCHRBank1(int r) {
        this.ppu.advance();

        r += off;
        
        if (chrROMBankMode == 1) {
            // switch 4KiB
            Memory m = rom.getROMBank(r, 0x1000);
            
            chrSegmented.setSegment(1, m);
        }
    }

    private void setPRGBank(int r) {
        // TODO - use ram
        //boolean ram = (r & 0x10) != 0;
        int bank = r & 0x0F;
        
        if (prgROMBankMode == 0 || prgROMBankMode == 1) {
            // switch 32KiB
            int bank32 = bank & ~0x01;
            prgSegmented.setSegment(0, rom.getPRG(bank32));
            prgSegmented.setSegment(1, rom.getPRG(bank32+1));
        } else if (prgROMBankMode == 2) {
            // fix $8000 to first, switch 16Kib at $C000
            prgSegmented.setSegment(0, rom.getPRG(0));
            prgSegmented.setSegment(1, rom.getPRG(bank));
        } else if (prgROMBankMode == 3) {
            // fix $C000 to last, switch 16KiB at $8000
            prgSegmented.setSegment(0, rom.getPRG(bank));
            prgSegmented.setSegment(1, rom.getPRG(rom.getPRGCount()-1));
        }
    }
}
