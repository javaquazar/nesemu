package nes.ppu;

class PCR2 {
    private int reg;
    
    public boolean isColorDisplay() {
        return (reg & 0x01) == 0;
    }
    
    public boolean isBackgroundClipping() {
        return (reg & 0x02) == 0;
    }
    
    public boolean isSpriteClipping() {
        return (reg & 0x04) == 0;
    }
    
    public boolean isBackgroundVisible() {
        return (reg & 0x08) != 0;
    }
    
    public boolean isSpriteVisible() {
        return (reg & 0x10) != 0;
    }
    
    public int getFullBGColor() {
        return (reg & 0xE0) >> 5;
    }
    
    public int getRegister() {
        return reg;
    }
    
    public void setRegister(int value) {
        reg = value;
    }
}