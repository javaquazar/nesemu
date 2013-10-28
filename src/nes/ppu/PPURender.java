package nes.ppu;

public class PPURender {
    private static final int SCREEN_W = 256;
    private static final int SCREEN_H = 240;
    private static final int TILE_SIZE = 8;
    private static final int MAX_SPRITES_PER_SCANLINE = 8;
    
    private static final int NT_W = SCREEN_W/TILE_SIZE;
    private static final int NT_H = SCREEN_H/TILE_SIZE;
    
    private PPURenderData renderData;
    private int[] buffer;
    private int[] palette;
    
    private int currentScanline;
    private int currentScanlineCycle;
    private int scroll_x, scroll_y;
    
    private static class Sprite {
        public int x, y;
        public int tile;
        public boolean vertFlip, horzFlip;
        public boolean behind;
        public int color;
    }
    
    private Sprite[] scanlineSprite;
    private int scanlineSpriteCount;
    private boolean scanlineHasSprite0;
    
    public PPURender(PPURenderData data, int[] buffer, int[] palette) {
        this.renderData = data;
        
        assert buffer.length == SCREEN_W*SCREEN_H;
        this.buffer = buffer;
        assert palette.length == 64;
        this.palette = palette;
        
        this.currentScanline = 0;
        this.currentScanlineCycle = 0;
        this.scanlineSprite = new Sprite[MAX_SPRITES_PER_SCANLINE];
        
        // create Sprite objects once
        for (int i = 0; i < MAX_SPRITES_PER_SCANLINE; i++) {
            this.scanlineSprite[i] = new Sprite();
        }
    }
    
    private void readSprite(int id, Sprite spr) {
        int attr = renderData.sprram[id*4+2];

        spr.x    = renderData.sprram[id*4+3];
        spr.y    = renderData.sprram[id*4+0]+1;
        spr.tile = renderData.sprram[id*4+1];
        
        spr.vertFlip = (attr & 0x80) != 0;
        spr.horzFlip = (attr & 0x40) != 0;
        spr.behind   = (attr & 0x20) != 0;
        spr.color    = attr & 0x03;
    }
    
    private void calculateSpritesOnScanline(int y) {
        Sprite spr = new Sprite();
        int spr_index = 0;
        boolean moreThan8 = false;
        
        this.scanlineHasSprite0 = false;
        
        // calculate sprites on scanline
        for (int i = 0; i < 64; i++) {
            readSprite(i, spr);
            int spr_top = spr.y;
            int spr_bot = spr.y + 7;
            
            // check if sprite is on scanline
            if (y >= spr_top && y <= spr_bot) {
                if (spr_index == MAX_SPRITES_PER_SCANLINE) {
                    moreThan8 = true;
                    // don't process any more sprites
                    break;
                }
                
                if (i == 0) {
                    this.scanlineHasSprite0 = true;
                }
                
                readSprite(i, scanlineSprite[spr_index]);
                spr_index++;
            }
        }
        
        this.scanlineSpriteCount = spr_index;
        this.renderData.moreThan8 = moreThan8;
    }
    
    private int getColor(int sprTable, int bgTable, int sx, int sy, int x, int y,
                         boolean sprite8x16)
    {
        int spriteW = 8;
        int spriteH = 8;
        
        int pattern = getNametablePattern(sx/TILE_SIZE, sy/TILE_SIZE);
        
        int palGroup = getNametablePaletteGroup(sx/TILE_SIZE, sy/TILE_SIZE);
        int bgPixel = getPatternPixel(bgTable, pattern, sx%TILE_SIZE, sy%TILE_SIZE);
        
        int pixel = bgPixel;

        for (int i = scanlineSpriteCount-1; i >= 0; i--) {
            Sprite spr = scanlineSprite[i];
            
            if (x >= spr.x && x <= spr.x+spriteW-1) {
                int sprX = x - spr.x;
                int sprY = y - spr.y;
                
                if (spr.horzFlip) {
                    sprX = spriteW-1 - sprX;
                }
                if (spr.vertFlip) {
                    sprY = spriteH-1 - sprY;
                }
                // rendering within sprite
                int sprPixel = getPatternPixel(sprTable, spr.tile, sprX, sprY);

                if (!spr.behind || (spr.behind && bgPixel == 0)) {
                    if (sprPixel != 0) {
                        palGroup = spr.color+4;
                        pixel = sprPixel;
                    }
                }
                
                if (scanlineHasSprite0 && i == 0 && spr.behind &&
                        bgPixel != 0 && sprPixel != 0)
                {
                    // turn on sprite #0 flag
                    renderData.sprite0Occurance = true;
                }
            }
        }
        
        if (pixel == 0) {
            return renderData.paletteBG;
        } else {
            return renderData.palette[pixel-1 + palGroup*3];
        }
    }

    public void advance(int ppuCycles) {
        // 242..261 - VINT period (not included here)
        // 0 - used to calculate first visible scanline
        // 1..240 - draw to screen
        // 241 - dummy
        
        // vertical+horizontal scroll counters are updated at cc 256
        
        while (ppuCycles > 0) {
            int sprTable = renderData.pcr1.getSpritePatternTable();
            int bgTable = renderData.pcr1.getBackgroundPatternTable();
            
            if (currentScanline < SCREEN_H && currentScanlineCycle < SCREEN_W) {
                if (currentScanlineCycle == 0) {
                    calculateSpritesOnScanline(currentScanline);
                    int nt = renderData.pcr1.getNameTable();
                    scroll_x = renderData.getCoarseXScroll()*TILE_SIZE + renderData.fineXScroll;
                    scroll_y = renderData.getCoarseYScroll()*TILE_SIZE + renderData.getFineYScroll();
                    
                    if ((nt & 1) != 0) {
                        //scroll_x += SCREEN_W;
                    }
                    if ((nt & 2) == 0) {
                        scroll_y += SCREEN_H;
                    }
                }
                
                int sx, sy, x, y;
                sx = (currentScanlineCycle+scroll_x)%(SCREEN_W*2);
                sy = (currentScanline+scroll_y)%(SCREEN_H*2);
                
                x = currentScanlineCycle;
                y = currentScanline;
                
                int color = getColor(sprTable, bgTable, sx, sy, x, y, false);
                
                int value = palette[color];
                buffer[currentScanline*SCREEN_W + currentScanlineCycle] = value;
            }
            ppuCycles -= 1;
            updateScanlineCycle(1);
        }
    }
    
    private int getPatternPixel(int tableAddr, int pattern, int x, int y) {
        int lo_y = renderData.patternTableMem.readByte(tableAddr + pattern*16 + y);
        int hi_y = renderData.patternTableMem.readByte(tableAddr + pattern*16 + y + 8);
        
        x = (TILE_SIZE-1)-x;
        
        return ((lo_y >> x)&1) | (((hi_y >> x)&1)<<1);
    }
    
    /**
     * @param x
     * @param y
     * @return 0..3
     */
    private int getNametablePaletteGroup(int x, int y) {
        int nt_x = x / NT_W;
        int nt_y = y / NT_H;
        x = x % NT_W;
        y = y % NT_H;
        
        int nt = nt_y*2 + nt_x;
        
        int attribute = renderData.nametable[nt][0x3C0 + (y/4)*(NT_W/4) + x/4];
        
        int attr_x = (x % 4)/2;
        int attr_y = (y % 4)/2;
        
        int attr_n = attr_y*2 + attr_x;

        int attr_shift = attr_n * 2;
        
        return (attribute & (0x3 << attr_shift)) >> attr_shift;
    }
    
    private int getNametablePattern(int x, int y) {
        int nt_x = x / NT_W;
        int nt_y = y / NT_H;
        x = x % NT_W;
        y = y % NT_H;
        
        int nt = nt_y*2 + nt_x;
        
        return renderData.nametable[nt][y*NT_W + x];
    }
    
    private void updateScanlineCycle(int ppuCycles) {
        currentScanlineCycle += ppuCycles;
        while (currentScanlineCycle >= PPU.PPU_CYCLES_PER_SCANLINE) {
            currentScanline++;
            currentScanlineCycle -= PPU.PPU_CYCLES_PER_SCANLINE;
        }
    }
}
