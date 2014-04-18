package nes.mappers;

import machine6502.Memory;

public interface Mapper {
    public Memory getSRAMMemory();
    public Memory getPRGMemory();
    public Memory getCHRMemory();
}
