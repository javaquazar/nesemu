package nes.ppu;

class PCR1 {
    private int reg;
    
    public int getNameTable() {
        switch (reg & 0x03) {
        case 0: return 0x2000;
        case 1: return 0x2400;
        case 2: return 0x2800;
        case 3: return 0x2C00;
        default: throw new IllegalStateException();
        }
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