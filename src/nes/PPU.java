package nes;

import machine6502.CPUCycleCounter;
import machine6502.Memory;

public class PPU {
    /*
     * 5:15  NTSC-CPU:PPU cycles (3 PPU cycles per CPU)
     * 5:16  PAL-CPU:PPU cycles  (3.2 PPU cycles per CPU)
     * 
     * Screen is 256x240
     *  - top and bottom 8 rows are typically cut off (shows 256x224)
     *  - 22-scanline vblank period
     * 
     * Each frame lasts 89342 (341*262) PPU cycles
     *  - 341 PPU cycles per scanline
     *  - 262 scanlines, including vblank
     */
    
    private Memory mem;
    private boolean vblankFlag;
    private CPUCycleCounter cpuCycleCounter;
    private int lastCpuCycle;
    
    private int vramAddr;
    private boolean vramAddrWriting;
    
    private static class PCR1 {
        private int reg;
        
        public int getNameTable() {
            switch (reg & 0x03) {
            case 0: return 0x2000;
            case 1: return 0x2400;
            case 2: return 0x2800;
            case 3: return 0x2C00;
            default: throw new IllegalStateException();
            }
        }
        
        public int getVRAMIncrement() {
            return ((reg & 0x04) != 0) ? 1:32;
        }
        
        public int getSpritePatternTable() {
            return ((reg & 0x08) != 0) ? 0x0000:0x1000;
        }
        
        public int getBackgroundPatternTable() {
            return ((reg & 0x10) != 0) ? 0x0000:0x1000;
        }
        
        public boolean isSprite8x8() {
            return (reg & 0x20) == 0;
        }
        
        public boolean isNMIVBlankOn() {
            return (reg & 0x80) != 0;
        }
        
        public int getRegister() {
            return reg;
        }
        
        public void setRegister(int value) {
            reg = value;
        }
    }
    
    private static class PCR2 {
        private int reg;
        
        public boolean isColorDisplay() {
            return (reg & 0x01) == 0;
        }
        
        public boolean isBackgroundClipping() {
            return (reg & 0x02) == 0;
        }
        
        public boolean isSpriteClipping() {
            return (reg & 0x04) == 0;
        }
        
        public boolean isBackgroundVisible() {
            return (reg & 0x08) != 0;
        }
        
        public boolean isSpriteVisible() {
            return (reg & 0x10) != 0;
        }
        
        public int getFullBGColor() {
            return (reg & 0xE0) >> 5;
        }
        
        public int getRegister() {
            return reg;
        }
        
        public void setRegister(int value) {
            reg = value;
        }
    }
    
    private PCR1 pcr1;
    private PCR2 pcr2;
    
    public PPU() {
        pcr1 = new PCR1();
        pcr2 = new PCR2();
    }
    
    public void advance() {
        int currentCpuCycle = this.cpuCycleCounter.getCycles();
        
        this.lastCpuCycle = currentCpuCycle;
    }

    public void startRenderingFrame(CPUCycleCounter cycleCounter) {
        this.cpuCycleCounter = cycleCounter;
        this.lastCpuCycle = cycleCounter.getCycles();
    }

    public void finishRenderingFrame() {
        advance();
        
        this.cpuCycleCounter = null;
    }

    public void enterVBlank() {
        vblankFlag = true;
    }
    
    public void leaveVBlank() {
        vblankFlag = false;
    }
    
    public int readStatus() {
        boolean vblank = vblankFlag;
        
        vblankFlag = false;
        
        return (vblank ? 0x80:0) | (0);
    }

    public void writePPUAddr(int value) {
        if (!vramAddrWriting) {
            // lo byte
            vramAddr = value;
        } else {
            // hi byte
            vramAddr = (value<<8) | vramAddr;
        }
        
        vramAddrWriting = !vramAddrWriting;
    }

    public void writePPUData(int value) {
        mem.writeByte(vramAddr, value);
        vramAddr = (vramAddr + pcr1.getVRAMIncrement()) & 0xFFFF;
    }
}
