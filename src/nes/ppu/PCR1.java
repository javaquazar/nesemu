package nes.ppu;

class PCR1 {
    private int reg;

    public int getNameTable() {
        return reg & 0x03;
    }
    
    public int getVRAMIncrement() {
        return ((reg & 0x04) == 0) ? 1:32;
    }
    
    public int getSpritePatternTable() {
        return ((reg & 0x08) == 0) ? 0x0000:0x1000;
    }
    
    public int getBackgroundPatternTable() {
        return ((reg & 0x10) == 0) ? 0x0000:0x1000;
    }
    
    public boolean isSprite8x8() {
        return (reg & 0x20) == 0;
    }
    
    public boolean isNMIVBlankOn() {
        return (reg & 0x80) != 0;
    }
    
    public int getRegister() {
        return reg;
    }
    
    public void setRegister(int value) {
        reg = value;
    }
}