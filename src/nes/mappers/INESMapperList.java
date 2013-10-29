package nes.mappers;

import machine6502.CPU;
import nes.ROM;
import nes.ppu.PPU;

public class INESMapperList {
    private static interface MapperInit {
        public Mapper newMapper(ROM rom, CPU cpu, PPU ppu);
    }
    
    private MapperInit[] mappers;
    
    public INESMapperList() {
        mappers = new MapperInit[256];
        
        mappers[0] = new MapperInit() {
            public Mapper newMapper(ROM rom, CPU cpu, PPU ppu) {
                return new NROM(rom);
            }
        };
        mappers[1] = new MapperInit() {
            public Mapper newMapper(ROM rom, CPU cpu, PPU ppu) {
                return new MMC1(rom, ppu);
            }
        };
    }
    
    public Mapper getMapper(ROM rom, CPU cpu, PPU ppu) {
        MapperInit m = mappers[rom.getMapper()];
        
        if (m != null) {
            return m.newMapper(rom, cpu, ppu);
        } else {
            return null;
        }
    }
}
