package nes.ppu;

import java.io.IOException;
import java.io.OutputStream;

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
    public static final int PPU_CYCLES_PER_SCANLINE = 341;
    public static final int PPU_SCREEN_CYCLES = PPU_CYCLES_PER_SCANLINE * 242;
    public static final int PPU_VBLANK_CYCLES = PPU_CYCLES_PER_SCANLINE * 20;

    private static final int VRAM_SIZE = 0x4000;
    
    private memory.FixedSegmented memPatterntables;
    private NametableMemory memNametables;
    
    private Memory vram;
    private boolean vblankFlag;
    public boolean vramAddrLatch;
    
    private PPURender render;
    private PPURenderData renderData;
    private int[] nt0, nt1;

    private CPUCycleCounter cycleCounter;
    private int lastCPUCycles;
    private int oamAddr;
    
    public PPU(boolean horizontalMirroring) {
        this.renderData = new PPURenderData();

        nt0 = new int[0x400];
        nt1 = new int[0x400];

        Memory memPalette = new Memory() {
            @Override
            public int readByte(int addr) {
                if (addr % 4 == 0) {
                    return renderData.paletteBG;
                } else {
                    return renderData.palette[addr - addr/4 - 1];
                }
            }
            
            @Override
            public void writeByte(int addr, int value) {
                if (addr % 4 == 0) {
                    renderData.paletteBG = value;
                } else {
                    renderData.palette[addr - addr/4 - 1] = value;
                }
            }
        };
        
        memory.Segmented mem = new memory.Segmented();
        
        memPatterntables = new memory.FixedSegmented(1, 0x2000);
        memNametables = new NametableMemory();

        // Pattern table 0, 1
        mem.addSegment(0x0000, 0x1FFF, memPatterntables);
        // Name tables, attribute tables
        mem.addSegment(0x2000, 0x2FFF, memNametables);
        // Unused
        mem.addSegment(0x3000, 0x3EFF, new memory.Zero());
        // Background and sprite palettes
        mem.addSegment(0x3F00, 0x3F1F, memPalette);
        mem.addSegment(0x3F20, 0x3FFF, new memory.Mirrored(PPURenderData.PALETTE_SIZE, memPalette));
        
        // Name/attribute tables
        
        vram = new memory.Mirrored(VRAM_SIZE, mem);
        vram = new memory.Debug(vram);
        
        setNametableMirroring(horizontalMirroring);
    }
    
    public void setPatternTable(Memory mem) {
        memPatterntables.setSegment(0, mem);
        renderData.patternTableMem = mem;
    }
    
    private void setNametableMirroring(boolean horizontal) {
        memNametables.setNametableRAM(0, nt0);
        renderData.nametable[0] = nt0;
        
        if (horizontal) {
            memNametables.setNametableRAM(1, nt0);
            memNametables.setNametableRAM(2, nt1);
            renderData.nametable[1] = nt0;
            renderData.nametable[2] = nt1;
        } else {
            memNametables.setNametableRAM(1, nt1);
            memNametables.setNametableRAM(2, nt0);
            renderData.nametable[1] = nt1;
            renderData.nametable[2] = nt0;
        }
        
        memNametables.setNametableRAM(3, nt1);
        renderData.nametable[3] = nt1;
    }
    
    private void advance() {
        if (render != null) {
            int cpuCycles = cycleCounter.getCycles();
            render.advance((cpuCycles - this.lastCPUCycles) * 15/5);
            this.lastCPUCycles = cpuCycles;
        }
    }

    public void startRenderingFrame(CPUCycleCounter cycleCounter,
                                    int[] buffer, int[] palette)
    {
        assert this.render == null;
        this.cycleCounter = cycleCounter;
        this.lastCPUCycles = cycleCounter.getCycles();
        this.render = new PPURender(renderData, buffer, palette);
        
        renderData.sprite0Occurance = false;
    }

    public void finishRenderingFrame() {
        assert render != null;
        
        advance();
        this.render = null;
    }

    public boolean enterVBlank() {
        vblankFlag = true;
        
        return renderData.pcr1.isNMIVBlankOn();
    }
    
    public void leaveVBlank() {
        vblankFlag = false;
    }
    
    public int readStatus() {
        advance();
        
        boolean vblank = vblankFlag;
        
        vblankFlag = false;
        vramAddrLatch = false;
        
        return (vblank ? 0x80:0) | (renderData.sprite0Occurance ? 0x40:0);
    }
    
    public void writePPUScroll(int value) {
        advance();
        
        if (!vramAddrLatch) {
            // YY-- -yyy
            int YY = value >> 6;
            int yyy = value & 0x07;
            
            renderData.vramAddr = (renderData.vramAddr & ~0x7300) |
                                  (YY<<8) | (yyy<<12);
        } else {
            // ---- -xxx
            int xxx = value & 0x07;
            renderData.fineXScroll = xxx;
        }
        
        // flip-flop the latch
        vramAddrLatch = !vramAddrLatch;
    }

    public void writePPUAddr(int value) {
        advance();
        
        if (!vramAddrLatch) {
            // hi byte
            renderData.vramAddr = (value<<8) | (renderData.vramAddr & 0x00FF);
        } else {
            // lo byte
            renderData.vramAddr = (renderData.vramAddr & 0xFF00) | value;
        }
        
        // flip-flop the latch
        vramAddrLatch = !vramAddrLatch;
    }

    public void writePPUData(int value) {
        advance();
        
        vram.writeByte(renderData.vramAddr, value);
        renderData.vramAddr = (renderData.vramAddr +
                               renderData.pcr1.getVRAMIncrement()) & 0xFFFF;
    }

    public int readPPUData() {
        int value = vram.readByte(renderData.vramAddr);
        renderData.vramAddr = (renderData.vramAddr +
                               renderData.pcr1.getVRAMIncrement()) & 0xFFFF;
        
        return value;
    }

    public void writePCR1(int value) {
        advance();
        renderData.pcr1.setRegister(value);
    }
    
    public void writePCR2(int value) {
        advance();
        renderData.pcr2.setRegister(value);
    }
    
    public void dumpVRAM(OutputStream out) throws IOException {
        for (int i = 0; i < VRAM_SIZE; i++) {
            out.write(vram.readByte(i));
        }
    }

    public int readOAMData() {
        // XXX - does reading increment oamAddr?
        return renderData.sprram[oamAddr];
    }

    public void writeOAMAddr(int value) {
        this.oamAddr = value;
    }

    public void writeOAMData(int value) {
        renderData.sprram[oamAddr] = value;
        oamAddr = (oamAddr+1) % 256;
    }
}
