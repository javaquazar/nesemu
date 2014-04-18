package machine6502;

/**
 * Data structure describing the 6502 CPU state (registers).
 *
 */
public class CPUState {
    public static final int A = 0;
    public static final int X = 1;
    public static final int Y = 2;
    /**
     * General-purpose registers
     * <p>
     * a: accumulator<br>
     * x/y: index
     */
    public int a, x, y;
    /**
     * sp: Stack pointer
     */
    public int sp;
    /**
     * pc: Process counter (current address of code)
     */
    public int pc;
    /**
     * flags: Bits set by various instructions
     * 
     * @see CPUFlags
     */
    public int flags;
    
    public void setRegister(int reg, int value) {
        switch (reg) {
        case A: this.a = value; break;
        case X: this.x = value; break;
        case Y: this.y = value; break;
        default: throw new IllegalArgumentException();
        }
    }
    
    public int getRegister(int reg) {
        switch (reg) {
        case A: return this.a;
        case X: return this.x;
        case Y: return this.y;
        default: throw new IllegalArgumentException();
        }
    }
    
    public void setFlag(int f) {
        flags |= f;
    }
    
    public void clearFlag(int f) {
        flags &= ~f;
    }
    
    public void setFlag(int f, boolean set) {
        if (set) {
            setFlag(f);
        } else {
            clearFlag(f);
        }
    }
    
    public boolean isFlagSet(int f) {
        return (flags & f) != 0;
    }

    public void flagsNZ(int value) {
        setFlag(CPUFlags.N, (value & 0x80) != 0);
        setFlag(CPUFlags.Z, value == 0);
    }
    
    public String toString() {
        String registers, other;
        
        registers = String.format("A: %02X (%d)\nX: %02X (%d)\nY: %02X (%d)\n",
                                  a, a, x, x, y, y);
        
        other = String.format("PC: %04X\nSP: %02X\n", pc, sp);
        /* N - Negative<br>
        * V - Overflow<br>
        * B - Break<br>
        * D - Decimal (unused on NES)<br>
        * I - Interrupt-disable<br>
        * Z - Zero<br>
        * C - Carry
        */
        
        other += "Flags: ";
        other += isFlagSet(CPUFlags.N)?"N":"n";
        other += isFlagSet(CPUFlags.V)?"V":"v";
        other += isFlagSet(CPUFlags.RESERVED)?"-":"_";
        other += isFlagSet(CPUFlags.B)?"B":"b";
        other += isFlagSet(CPUFlags.D)?"D":"d";
        other += isFlagSet(CPUFlags.I)?"I":"i";
        other += isFlagSet(CPUFlags.Z)?"Z":"z";
        other += isFlagSet(CPUFlags.C)?"C":"c";
        other += String.format(" (%02X)", flags);
        other += "\n";
        
        return registers + other;
    }
}
