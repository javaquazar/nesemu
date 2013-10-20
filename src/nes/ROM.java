package nes;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import machine6502.Memory;

public class ROM {
    private byte[][] prg_rom;
    private byte[][] chr_rom;
    
    public ROM(InputStream input) throws IOException {
        int prg_count, chr_count;
        
        // 16-byte iNES header
        byte[] header = new byte[0x10];
        input.read(header);
        
        // TODO - other verification
        prg_count = header[4];
        chr_count = header[5];
        
        prg_rom = new byte[prg_count][];
        chr_rom = new byte[chr_count][];
        
        // PRG ROM
        for (int i = 0; i < prg_count; i++) {
            prg_rom[i] = new byte[0x4000];
            int r = input.read(prg_rom[i]);
            if (r != 0x4000) {
                throw new EOFException();
            }
        }

        // CHR ROM
        for (int i = 0; i < chr_count; i++) {
            chr_rom[i] = new byte[0x2000];
            int r = input.read(chr_rom[i]);
            if (r != 0x2000) {
                throw new EOFException();
            }
        }
    }
    
    public int getPRGCount() {
        return prg_rom.length;
    }
    
    public int getCHRCount() {
        return chr_rom.length;
    }
    
    public Memory getPRG(int bank) {
        return new memory.ByteConstant(prg_rom[bank]);
    }
    
    public Memory getCHR(int bank) {
        return new memory.ByteConstant(chr_rom[bank]);
    }
}
