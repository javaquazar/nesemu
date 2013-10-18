package nes;

import machine6502.CPUCycleCounter;

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
     * 
     * VRAM, $2003, $2004, $2007 cannot be accessed during rendering
     */
    
    private boolean vblankFlag;
    private CPUCycleCounter cpuCycleCounter;
    private int lastCpuCycle;
    
    
    public PPU() {
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
        // TODO Auto-generated method stub
        
    }

    public void writePPUData(int value) {
        // TODO Auto-generated method stub
        
    }
}
