package nes.mappers;

import nes.ROM;
import machine6502.Memory;

public abstract class Mapper {
    private String name;

    public Mapper(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public abstract Memory getPRGMemory(ROM rom);
    public abstract Memory getPPUPatternMemory(ROM rom);
}
