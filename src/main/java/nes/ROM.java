package nes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import machine6502.Memory;

public class ROM {
    // PRG ROM (16KiB)
    // CHR ROM (8KiB)
    private static final int PRG_SIZE = 0x4000;
    private static final int CHR_SIZE = 0x2000;
    
    private byte[] romData;
    private Memory romMem;
    private int prgCount, chrCount;
    private int mapper;
    
    public ROM(InputStream input) throws IOException {
        // 16-byte iNES header
        byte[] header = new byte[0x10];
        input.read(header);
        
        boolean validMagic = header[0] == 'N' && header[1] == 'E' &&
                             header[2] == 'S' && header[3] == 0x1A;
        
        if (!validMagic) {
            throw new IOException("Provided ROM is not a valid NES ROM.");
        }
        
        // TODO - other verification
        prgCount = header[4];
        chrCount = header[5];
        int flag6 = header[6];
        int flag7 = header[7];
        int romDataSize = prgCount*PRG_SIZE + chrCount*CHR_SIZE;
        
        romData = new byte[romDataSize];
        final int readSize = IOUtils.read(input, romData, 0, romDataSize);
        if (readSize < romDataSize) {
            throw new IOException("ROM file size (" + readSize + ") is smaller than required (" + romDataSize + ")");
        }
        
        romMem = new memory.ByteConstant(romData);
        
        this.mapper = (flag7&0xF0) | (flag6>>4);
    }
    
    public int getPRGCount() {
        return prgCount;
    }
    
    public int getCHRCount() {
        return chrCount;
    }
    
    public Memory getPRG(int bank) {
        return new memory.Split(romMem, bank*PRG_SIZE, PRG_SIZE);
    }
    
    public Memory getCHR(int bank) {
        return new memory.Split(romMem, prgCount*PRG_SIZE + bank*CHR_SIZE, CHR_SIZE);
    }
    
    public Memory getROMRange(int lo, int length) {
        if (lo + length > romData.length) {
            throw new IllegalArgumentException("Range is too high");
        }
        
        return new memory.Split(romMem, lo, length);
    }
    
    public Memory getROMBank(int bank, int bankLength) {
        return getROMRange(bank*bankLength, bankLength);
    }
    
    public int getROMLength() {
        // not including the header
        return romData.length;
    }
    
    public int getROMBanks(int bankLength) {
        return romData.length / bankLength;
    }
    
    public int getMapper() {
        return this.mapper;
    }
}
