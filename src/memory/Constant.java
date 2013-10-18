package memory;

import machine6502.Memory;

public class Constant implements Memory {
    private byte[] memory;

    public Constant(byte[] prg_rom) {
        this.memory = prg_rom;
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
