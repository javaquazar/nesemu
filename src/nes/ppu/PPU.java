package nes.ppu;

import java.io.IOException;
import java.io.OutputStream;

import machine6502.CPUCycleCounter;
import machine6502.Memory;
import memory.Debug;
import memory.RAM;

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

    private static final int VRAM_SIZE = 0x4000;
    private static final int PALETTE_SIZE = 0x20;
    
    private memory.FixedSegmented memPatterntables;
    private NametableMemory memNametables;
    private int[] nt0, nt1;
            
    private Memory vram;
    private int[] sprram;
    private boolean vblankFlag;
    private int[] palette;
    
    private CPUCycleCounter cpuCycleCounter;
    private int lastCpuCycle;
    
    /**
     * VRAM address: 0yyyNNYY YYYXXXXX
     * <p>
     * yyy: fine Y scroll;<br>
     * NN: name table index;<br>
     * YYYYY: coarse Y scroll;<br>
     * XXXXX: coarse X scroll;<br>
     * xxx: fine X scroll (not part of the address);
     * <p>
     * Source: http://forums.nesdev.com/viewtopic.php?p=105762#p105762
     */
    private int vramAddr;
    private boolean vramAddrLatch;
    private int fineXScroll;
    
    private PCR1 pcr1;
    private PCR2 pcr2;
    
    public PPU() {
        pcr1 = new PCR1();
        pcr2 = new PCR2();
        
        palette = new int[PALETTE_SIZE];

        memory.RAM memPalette = new memory.RAM(palette);
        
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
        mem.addSegment(0x3F20, 0x3FFF, new memory.Mirrored(PALETTE_SIZE, memPalette));
        
        // Name/attribute tables
        
        vram = new memory.Mirrored(VRAM_SIZE, mem);
        vram = new memory.Debug(vram);
        
        sprram = new int[256];
        
        nt0 = new int[0x400];
        nt1 = new int[0x400];
        
        setNametableMirroring(true);
    }
    
    public void setPatternTable(Memory mem) {
        memPatterntables.setSegment(0, mem);
    }
    
    private void setNametableMirroring(boolean horizontal) {
        memNametables.setNametableRAM(0, nt0);
        
        if (horizontal) {
            memNametables.setNametableRAM(1, nt0);
            memNametables.setNametableRAM(2, nt1);
        } else {
            memNametables.setNametableRAM(1, nt1);
            memNametables.setNametableRAM(2, nt0);
        }
        
        memNametables.setNametableRAM(3, nt1);
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
    
    public void writePPUScroll(int value) {
        if (!vramAddrLatch) {
            // YY-- -yyy
            int YY = value >> 6;
            int yyy = value & 0x07;
            
            vramAddr = (vramAddr & ~0x7300) | (YY<<8) | (yyy<<12);
        } else {
            // ---- -xxx
            int xxx = value & 0x07;
            fineXScroll = xxx;
        }
        
        // flip-flop the latch
        vramAddrLatch = !vramAddrLatch;
    }

    public void writePPUAddr(int value) {
        if (!vramAddrLatch) {
            // hi byte
            vramAddr = (value<<8) | (vramAddr & 0x00FF);
        } else {
            // lo byte
            vramAddr = (vramAddr & 0xFF00) | value;
        }
        
        // flip-flop the latch
        vramAddrLatch = !vramAddrLatch;
    }

    public void writePPUData(int value) {
        vram.writeByte(vramAddr, value);
        vramAddr = (vramAddr + pcr1.getVRAMIncrement()) & 0xFFFF;
    }

    public int readPPUData() {
        int value = vram.readByte(vramAddr);
        vramAddr = (vramAddr + pcr1.getVRAMIncrement()) & 0xFFFF;
        
        return value;
    }

    public void writePCR1(int value) {
        pcr1.setRegister(value);
    }
    
    public void writePCR2(int value) {
        pcr2.setRegister(value);
    }
    
    public void dumpVRAM(OutputStream out) throws IOException {
        for (int i = 0; i < VRAM_SIZE; i++) {
            out.write(vram.readByte(i));
        }
    }
}
