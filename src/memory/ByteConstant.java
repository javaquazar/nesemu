package memory;

import machine6502.Memory;

public class ByteConstant implements Memory {
    private byte[] memory;

    public ByteConstant(byte[] arr) {
        this.memory = arr;
    }

    @Override
    public int readByte(int addr) {
        return (memory[addr] + 0x100) & 0xFF;
    }

    @Override
    public void writeByte(int addr, int value) {
        // no-op
    }
}
