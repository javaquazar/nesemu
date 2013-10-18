package machine6502;

public class CPUUtils {
    private static final int STACK_PAGE = 0x100;
    
    public static int pullByte(CPUState regdata, Memory mem) {
        regdata.sp = (regdata.sp+1)%0x100;
        return mem.readByte(STACK_PAGE + regdata.sp);
    }
    
    public static void pushByte(CPUState regdata, Memory mem, int v) {
        mem.writeByte(STACK_PAGE + regdata.sp, v);
        regdata.sp = (regdata.sp-1 + 0x100) % 0x100;
    }
    
    public static int pullShort(CPUState regdata, Memory mem) {
        int lo, hi;
        
        lo = pullByte(regdata, mem);
        hi = pullByte(regdata, mem);
        
        return lo | (hi << 8);
    }
    
    public static void pushShort(CPUState regdata, Memory mem, int v) {
        int lo, hi;
        
        hi = v>>8;
        lo = v&0xFF;
        pushByte(regdata, mem, hi);
        pushByte(regdata, mem, lo);
    }
}
