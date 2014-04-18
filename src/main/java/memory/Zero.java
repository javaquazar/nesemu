package memory;

import machine6502.Memory;

public class Zero implements Memory {
    @Override
    public int readByte(int addr) {
        return 0;
    }

    @Override
    public void writeByte(int addr, int value) {
    }
}
