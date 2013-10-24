package nes.ppu;

public class PPURender {
    private PPURenderData renderData;
    private int[] buffer;
    private int[] palette;
    
    private int currentScanline;
    private int currentScanlineCycle;
    private int scroll_x, scroll_y;
    
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
    		    int x, y;
    		    x = currentScanlineCycle;
    		    y = currentScanline;
    		    
    		    int xp, yp;
    		    xp = x % 8;
    		    yp = y % 8;
    		    
    		    int paletteGroup = getNametablePaletteGroup(x/8, x/8);
    		    int pattern = getNametablePattern(x/8, y/8);
    		    int patternPixel = getPatternPixel(pattern, xp, yp);
    		    
    		    int color;
    		    if (patternPixel == 0) {
    		        color = renderData.paletteBG;
    		    } else {
                    color = renderData.palette[patternPixel-1 + paletteGroup*3];
    		    }
    			int value = palette[color];
    			buffer[currentScanline*256 + currentScanlineCycle] = value;
    		}
    		ppuCycles -= 1;
    		updateScanlineCycle(1);
    	}
    }
    
    private int getPatternPixel(int pattern, int x, int y) {
        int lo_y = renderData.patternTableMem.readByte(256*16 + pattern*16 + y);
        int hi_y = renderData.patternTableMem.readByte(256*16 + pattern*16 + y + 8);
        
        x = 7-x;
        
        return ((lo_y >> x)&1) | (((hi_y >> x)&1)<<1);
    }
    
    /**
     * @param x
     * @param y
     * @return 0..3
     */
    private int getNametablePaletteGroup(int x, int y) {
    	int nt_x = x / 32;
    	int nt_y = y / 30;
    	x = x % 32;
    	y = y % 30;
    	
    	int nt = nt_x*2 + nt_y;
    	
    	int attribute = renderData.nametable[nt][0x3C0 + (y/4)*8 + x/4];
    	
    	int attr_x = (x % 4)/2;
    	int attr_y = (y % 4)/2;
    	
    	int attr_n = attr_x*2 + attr_y;
    	
    	int attr_mask = 0x3 << attr_n;
    	int attr_shift = attr_n * 2;
    	
    	return (attribute & (attr_mask)) >> attr_shift;
    }
    
    private int getNametablePattern(int x, int y) {
    	int nt_x = x / 32;
    	int nt_y = y / 30;
    	x = x % 32;
    	y = y % 30;
    	
    	int nt = nt_x*2 + nt_y;
    	
    	return renderData.nametable[nt][y*32 + x];
    }
    
    private void updateScanlineCycle(int ppuCycles) {
		currentScanlineCycle += ppuCycles;
		while (currentScanlineCycle >= PPU.PPU_CYCLES_PER_SCANLINE) {
			currentScanline++;
			currentScanlineCycle -= PPU.PPU_CYCLES_PER_SCANLINE;
		}
    }
}
