package nes.mappers;

import machine6502.Memory;
import nes.ROM;

public class MMC1 extends Mapper {
    public MMC1() {
        super("MMC1");
    }

    @Override
    public Memory getPRGMemory(ROM rom) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Memory getPPUPatternMemory(ROM rom) {
        // TODO Auto-generated method stub
        return null;
    }
}
