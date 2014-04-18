package nes;

import machine6502.Memory;

public class Registers2A03Memory implements Memory {
    private static final int OAM_DMA = 0x14;
    private static final int JOY1 = 0x16;
    private static final int JOY2 = 0x17;
    
    private static class JoypadState {
        private boolean strobe;
        private int button;
        private Joypad joy;
        
        public JoypadState(Joypad joy) {
            this.strobe = false;
            this.button = 0;
            this.joy = joy;
        }
        
        public int read() {
            if (button < joy.getButtonCount()) {
                boolean on;
                on = joy.getButton(button);
                if (!strobe) {
                    button++;
                }
                return on ? 1 : 0;
            } else {
                return 1;
            }
        }
        
        public void write(int value) {
            if (value == 0 || value == 1) {
                strobe = (value == 1); 
                if (strobe) {
                    button = 0;
                }
            }
        }
    }

    private Memory mem;
    private JoypadState joy1, joy2;
    
    /**
     * Mimics a standard NES joypad that has no buttons pressed
     */
    private static class DummyJoypad implements Joypad {
        public int getButtonCount() {
            return  8;
        }

        public boolean getButton(int button) {
            return false;
        }
    }
    
    public Registers2A03Memory(Memory mem, Joypad joy1, Joypad joy2) {
        this.mem = mem;
        
        if (joy1 == null) {
            joy1 = new DummyJoypad();
        }
        if (joy2 == null) {
            joy2 = new DummyJoypad();
        }
        
        this.joy1 = new JoypadState(joy1);
        this.joy2 = new JoypadState(joy2);
    }

    @Override
    public int readByte(int addr) {
        switch (addr) {
        // read back each button, one at a time
        case JOY1: return joy1.read();
        case JOY2: return joy2.read();
        default: return 0;
        }
    }

    @Override
    public void writeByte(int addr, int value) {
        switch (addr) {
        case OAM_DMA:
            /* Writing $xx copies 256 bytes by reading from $xx00-$xxFF and
             * writing to $2004 (OAM data) */
            int start = value<<8;
            for (int i = start; i <= start+0xFF; i++) {
                int r = mem.readByte(i);
                mem.writeByte(0x2004, r);
            }
            
            break;
        case JOY1:
            // probe all joypads
            joy1.write(value);
            joy2.write(value);
            break;
        case JOY2:
            // APU frame interrupt
            break;
        }
    }

}
