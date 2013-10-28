package nes.ppu;

import machine6502.Memory;

public class PPURenderData {
    public static final int PALETTE_SIZE = 24;

    public Memory patternTableMem;
    public int[][] nametable;
    public int[] sprram;
    public int paletteBG;
    public int[] palette;
    
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
    public int vramAddr;
    public int fineXScroll;
    
    public PCR1 pcr1;
    public PCR2 pcr2;

    public boolean sprite0Occurance;
    public boolean moreThan8;
    
    public PPURenderData() {
        nametable = new int[4][];
        pcr1 = new PCR1();
        pcr2 = new PCR2();
        palette = new int[PALETTE_SIZE];
        sprram = new int[256];
        sprite0Occurance = false;
    }
    
    public int getVRAMInt(int bits_wide, int shift) {
        final int MASK = (1<<bits_wide)-1;
        
        return (vramAddr & (MASK<<shift)) >> shift;
    }
    
    public void setVRAMInt(int bits_wide, int shift, int value) {
        final int MASK = (1<<bits_wide)-1;
        
        vramAddr = (vramAddr & ~(MASK<<shift)) | ((value&MASK)<<shift);
    }
    
    public int getCoarseXScroll() {
        return getVRAMInt(5, 0);
    }
    
    public int getCoarseYScroll() {
        return getVRAMInt(5, 5);
    }
    
    public void setCoarseXScroll(int x) {
        setVRAMInt(5, 0, x);
    }
    
    public void setCoarseYScroll(int y) {
        setVRAMInt(5, 5, y);
    }
    
    public int getFineYScroll() {
        return getVRAMInt(3, 12);
    }

    public void setFineYScroll(int y) {
        setVRAMInt(3, 12, y);
    }
}
