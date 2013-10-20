package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import machine6502.ByteUtils;
import machine6502.CPU;
import machine6502.MemUtils;
import machine6502.Memory;
import memory.DebugUtil;
import memory.Segmented;
import memory.Zero;

import org.junit.Before;
import org.junit.Test;

public class AllSuiteA {
    private static final String romFile = "test-AllSuiteA/AllSuiteA.bin";
    private static final int romReset = 0x4000;
    
    private Memory mem;
    private CPU cpu;
    

    
    private static final int[][] passes = {
            {0x022A, 0x55},
            {0xA9, 0xAA},
            {0x71, 0xFF},
            {0x01DD, 0x6E},
            {0x40, 0x42},
            {0x40, 0x33},
            {0x30, 0x9D},
            {0x15, 0x7F},
            {0x42, 0xA5},
            {0x80, 0x1F},
            {0x30, 0xCE},
            {0x30, 0x29},
            {0x33, 0x42},
            {0x21, 0x6C},
            {0x60, 0x42},
            
            {0x0210, 0xFF}
    };
    
    @Before
    public void setUp() throws IOException {
        
        Memory ram = new memory.RAM(0x1000);
        memory.Segmented segMem = new memory.Segmented();
        
        File file = new File(romFile);
        int romSize = (int)file.length();
        byte[] romData = new byte[romSize];
        
        try (FileInputStream input = new FileInputStream(file)) {
            input.read(romData);
        }
        
        Memory debugRegs = new Memory() {
            private int testNo = 0;
            @Override
            public void writeByte(int addr, int value) {
                if (addr == 0) {
                    cpu.printDebug(System.out);
                } else if (addr == 1) {
                    // next test
                    checkTest(testNo);
                    testNo++;
                }
            }
            
            @Override
            public int readByte(int addr) {
                return 0;
            }
        };
        
        Memory vector = new memory.RAM(6);

        segMem.addSegment(0x0000, 0x1FFF, ram);
        segMem.addSegment(0x2000, romReset-1, debugRegs);
        segMem.addSegment(romReset, romReset + romSize-1, new memory.ByteConstant(romData));
        segMem.addSegment(romReset + romSize, 0xFFF9, new memory.Zero());
        segMem.addSegment(0xFFFA, 0xFFFF, vector);
        
        MemUtils.writeShort(segMem, 0xFFFC, romReset);
        
        this.mem = segMem;
        this.cpu = new CPU(this.mem);
        this.cpu.reset();
    }
    
    public void checkTest(int testNo) {
        int addr, expected;
        addr = passes[testNo][0];
        expected = passes[testNo][1];
        int actual = mem.readByte(addr);

        System.out.printf("Test %02d: $%04X = #$%02X ", testNo, addr, expected);
        if (expected != actual) {
            System.out.printf(": got #$%02X", actual);
        }
        System.out.println();
        assertEquals(expected, actual);
    }

    @Test
    public void test() {
        cpu.runUntilBreak();
    }
}
