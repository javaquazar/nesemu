package memory;

import java.io.PrintStream;

import machine6502.Memory;

public class DebugUtil {
    public static void printMemoryPage(PrintStream out, Memory mem, int page) {
        for (int y = 0; y < 0x10; y++) {
            out.printf("%02X: ", y<<4);
            for (int x = 0; x < 0x10; x++) {
                int addr = (page<<8) | (y<<4) | x;
                int value = mem.readByte(addr);
                out.printf("%02X ", value);
            }
            out.println();
        }
    }
    
    public static void printDebug(PrintStream out, Memory mem) {
        out.println("Zero Page:");
        printMemoryPage(out, mem, 0);
        
        out.println("Stack Page:");
        printMemoryPage(out, mem, 1);
    }
}
