package nes;

import java.io.IOException;
import java.io.InputStream;

import machine6502.CPU;
import machine6502.CPUCycleCounter;

public class GameRunnable implements Runnable {
    private static final int PPU_PER_SCANLINE = 341;
    private static final int PPU_SCREEN_CYCLES = PPU_PER_SCANLINE * 240;
    private static final int PPU_VBLANK_CYCLES = PPU_PER_SCANLINE * 22;
    
    private nes.ROM rom;
    
    public GameRunnable(InputStream input) throws IOException {
        rom = new nes.ROM(input);
    }

    @Override
    public void run() {
        Joypad joypad1;
        joypad1 = new Joypad() {
            @Override
            public int getButtonCount() {
                return 8;
            }
            
            @Override
            public boolean getButton(int button) {
                return false;
            }
        };
        
        PPU ppu = new PPU();

        memory.Segmented mem = new memory.Segmented();
        memory.RAM ram = new memory.RAM(0x800);
        RegistersPPUMemory registersPPU = new RegistersPPUMemory(ppu);
        Registers2A03Memory registers2A03;
        
        registers2A03 = new Registers2A03Memory(mem, joypad1, null);
        
        mem.addSegment(0x0000, 0x1FFF, new memory.Mirrored(0x800, ram));
        
        mem.addSegment(0x2000, 0x3FFF, new memory.Mirrored(8, registersPPU));
        mem.addSegment(0x4000, 0x401F, registers2A03);
        
        mem.addSegment(0x5000, 0x7FFF, new memory.Zero());
        
        mem.addSegment(0x8000, 0xBFFF, rom.getPRG(0));
        mem.addSegment(0xC000, 0xFFFF, rom.getPRG(1));
        
        CPU cpu = new CPU(new memory.Debug(mem));
        cpu.reset();
        
        try {
            while (true) {
                CPUCycleCounter cycleCounter = new CPUCycleCounter();
                
                ppu.startRenderingFrame(cycleCounter);
                
                // Go until screen is done rendering
                cpu.runForXCycles(PPU_SCREEN_CYCLES*5/15, cycleCounter);
                
                // entering VBlank
                // the crt beam is moving its way back to the top of the TV
                ppu.enterVBlank();
                ppu.finishRenderingFrame();
                
                // notify and update the emulator ui
                
                Thread.sleep(1000/60);
                
                // Go until VBlank is done
                cpu.runForXCycles(PPU_VBLANK_CYCLES*5/15);
                
                // leaving VBlank
                ppu.leaveVBlank();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
