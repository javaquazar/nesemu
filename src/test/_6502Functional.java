package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import machine6502.CPU;
import machine6502.Memory;
import memory.Segmented;

public class _6502Functional {
    private static final String binFile = "test-6502functional/6502_functional_test.bin";
    private static final int romOrg = 0x000A;
    private static final int romReset = 0x0400;
    
    public static void main(String[] args) throws Exception {
        File file = new File(binFile);
        int binSize = (int)file.length();
        byte[] binData = new byte[binSize];
        
        try (FileInputStream input = new FileInputStream(file)) {
            input.read(binData);
        }
        
        int[] ram = new int[0x8000];
        
        for (int i = 0; i < binSize; i++) {
            ram[romOrg+i] = (binData[i] + 0x100) & 0xFF;
        }
        
        Segmented mem = new Segmented();
        mem.addSegment(0, 0x5FFF, new memory.RAM(ram));
        mem.addSegment(0x8000, 0x9000, new Memory() {
            @Override
            public void writeByte(int addr, int value) {
                System.out.write(value);
            }
            
            @Override
            public int readByte(int addr) {
                try {
                    return System.in.read();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        CPU cpu = new CPU(mem);
        cpu.reset(romReset);
        
        try {
            cpu.runUntilBreak();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpu.printDebug(System.err);
    }
}
