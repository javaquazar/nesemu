package machine6502;

public class MemUtils {
    public static class IndY {
        public int value;
        public boolean crossesPage;
    }
    /**
     * Read at a zero-page address that's been offseted.
     * Ensures that the read address stays within the zero-page.
     * 
     * @param mem
     * @param addr
     * @param offset
     * @return
     */
    public static int readByteZP(Memory mem, int addr, int offset) {
        int new_addr = (addr + offset) % 0x100;
        return mem.readByte(new_addr);
    }
    
    /**
     * Reads the short at "addr + ind", and then reads the address it just read.
     * 
     * @param mem
     * @param addr
     * @param ind
     * @return The read value
     */
    public static int readByteInd(Memory mem, int addr, int ind) {
        int new_addr = MemUtils.readShort(mem, addr + ind);
        
        return mem.readByte(new_addr);
    }
    
    public static IndY readByteIndY(Memory mem, int addr, int indY) {
        IndY data = new IndY();
        int base_addr = MemUtils.readShort(mem, addr);
        int new_addr = base_addr + indY;
        
        data.value = mem.readByte(new_addr);
        data.crossesPage = MemUtils.crossesPageBoundary(base_addr, new_addr);
        
        return data;
    }
    
    /**
     * Reads a little-endian short. Typically used for reading addresses.
     * 
     * @param mem
     * @param addr
     * @return A 16-bit short (0x0000..0xFFFF)
     */
    public static int readShort(Memory mem, int addr) {
        return mem.readByte(addr) | (mem.readByte(addr+1)<<8);
    }
    
    /**
     * Reads a little-endian short, and ensures it never reads outside the page.
     * <p>
     * For example, reading at $42FF will read the bytes at $42FF and $4200.
     * It won't read $4300 as one might expect.
     * 
     * @param mem
     * @param addr
     * @return The read value
     */
    public static int readShortPageWrap(Memory mem, int addr) {
        int lo_addr, hi_addr;
        
        lo_addr = addr;
        // if addr is 0x??FF, then hi_addr is 0x??00
        hi_addr = ((addr&0xFF) == 0xFF) ? (addr&0xFF00):(addr+1);
        return mem.readByte(lo_addr) | (mem.readByte(hi_addr)<<8);
    }

    public static void writeByteZP(Memory mem, int addr, int offset, int val) {
        int new_addr = (addr + offset) % 0x100;
        mem.writeByte(new_addr, val);
    }

    public static void writeByteInd(Memory mem, int addr, int ind, int value) {
        int new_addr = MemUtils.readShort(mem, addr + ind);
        
        mem.writeByte(new_addr, value);
    }

    public static boolean writeByteIndY(Memory mem, int addr, int indY, int value) {
        int base_addr = MemUtils.readShort(mem, addr);
        int new_addr = base_addr + indY;
        
        mem.writeByte(new_addr, value);
        
        return MemUtils.crossesPageBoundary(base_addr, new_addr);
    }
    
    public static boolean crossesPageBoundary(int addr1, int addr2) {
        return (addr1 & 0xFF00) != (addr2 & 0xFF00);
    }

    public static int readNMIVector(Memory mem) {
        return MemUtils.readShort(mem, 0xFFFA);
    }
    
    public static int readResetVector(Memory mem) {
        return MemUtils.readShort(mem, 0xFFFC);
    }
    
    public static int readIRQVector(Memory mem) {
        return MemUtils.readShort(mem, 0xFFFE);
    }
}
