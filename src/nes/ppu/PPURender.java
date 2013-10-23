package nes.ppu;

public class PPURender {
    private PPURenderData renderData;
    private int[] buffer;
    private int[] palette;
    
    private int currentScanline;
    private int currentScanlineCycle;
    
    public PPURender(PPURenderData data, int[] buffer, int[] palette) {
        this.renderData = data;
        
        assert buffer.length == 256*240;
        this.buffer = buffer;
        assert palette.length == 64;
        this.palette = palette;
        
        this.currentScanline = 0;
        this.currentScanlineCycle = 0;
    }

    public void advance(int ppuCycles) {
        // 242..261 - VINT period (not included here)
        // 0 - used to calculate first visible scanline
        // 1..240 - draw to screen
        // 241 - dummy
        
        // vertial+horizontal scroll counters are updated at cc 256
    	
    	while (ppuCycles > 0) {
    		if (currentScanline < 240 && currentScanlineCycle < 256) {
    			int color = renderData.paletteBG;
    			int value = palette[color];
    			buffer[currentScanline*256 + currentScanlineCycle] = value;
    		}
    		ppuCycles -= 1;
    		updateScanlineCycle(1);
    	}
    }
    
    private void updateScanlineCycle(int ppuCycles) {
		currentScanlineCycle += ppuCycles;
		while (currentScanlineCycle >= PPU.PPU_CYCLES_PER_SCANLINE) {
			currentScanline++;
			currentScanlineCycle -= PPU.PPU_CYCLES_PER_SCANLINE;
		}
    }
}
