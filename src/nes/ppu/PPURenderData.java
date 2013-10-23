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
    
    public PPURenderData() {
    	nametable = new int[4][];
        pcr1 = new PCR1();
        pcr2 = new PCR2();
        palette = new int[PALETTE_SIZE];
        sprram = new int[256];
    }
}
