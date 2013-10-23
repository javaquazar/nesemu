package nes;

import java.io.IOException;
import java.io.InputStream;

import nes.ppu.PPU;
import machine6502.CPU;
import machine6502.CPUCycleCounter;

public class GameRunnable implements Runnable {
	public static interface UIUpdate {
		public void update(int[] buffer);
	}
    
    private nes.ROM rom;
	private UIUpdate ui;
    
    public GameRunnable(InputStream input, UIUpdate ui) throws IOException {
        this.rom = new nes.ROM(input);
        this.ui = ui;
    }

    @Override
    public void run() {
    	boolean running = true;
    	
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
        
        PPU ppu = new PPU(false);
        
        ppu.setPatternTable(rom.getCHR(0));

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
        
        int[] renderBuffer = new int[256*240];
        int[] palette = new DefaultPalette().getPalette(); 
        
        try {
            while (running) {
                CPUCycleCounter cycleCounter = new CPUCycleCounter();
                
                ppu.startRenderingFrame(cycleCounter, renderBuffer, palette);
                
                // Go until screen is done rendering
                cpu.runForXCycles(PPU.PPU_SCREEN_CYCLES*5/15, cycleCounter);
                
                // entering VBlank
                // the crt beam is moving its way back to the top of the TV
                boolean nmi = ppu.enterVBlank();
                ppu.finishRenderingFrame();
                
                // notify and update the emulator ui
                ui.update(renderBuffer);
                
                Thread.sleep(1000/60);
                
                if (nmi) {
                	cpu.interruptNMI();
                }
                
                // Go until VBlank is done
                cpu.runForXCycles(PPU.PPU_VBLANK_CYCLES*5/15);
                
                // leaving VBlank
                ppu.leaveVBlank();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
