package machine6502.instructions;

import static machine6502.AddressInstruction.*;
import machine6502.ByteUtils;
import machine6502.CPUFlags;
import machine6502.CPUState;
import machine6502.Instruction;
import machine6502.Memory;

public class CompareInstructions {
    public static void assign(Instruction[] insts) {
        Operation cmp, cpx, cpy;
        
        cmp = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int a = regdata.a;
                int b = rw.read();
                
                flagCompare(regdata, a, b);
            }
        };
        
        cpx = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int a = regdata.x;
                int b = rw.read();
                
                flagCompare(regdata, a, b);
            }
        };
        
        cpy = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int a = regdata.y;
                int b = rw.read();
                
                flagCompare(regdata, a, b);
            }
        };
        
        
        // CMP
        insts[0xC9] = imm (cmp, 2);
        insts[0xC5] = zp  (cmp, 3);
        insts[0xD5] = zpx (cmp, 4);
        insts[0xCD] = abs (cmp, 4);
        insts[0xDD] = absx(cmp, 4, true);
        insts[0xD9] = absy(cmp, 4, true);
        insts[0xC1] = indx(cmp, 6);
        insts[0xD1] = indy(cmp, 5, true);
        
        // CPX
        insts[0xE0] = imm(cpx, 2);
        insts[0xE4] = zp (cpx, 3);
        insts[0xEC] = abs(cpx, 4);

        // CPY
        insts[0xC0] = imm(cpy, 2);
        insts[0xC4] = zp (cpy, 3);
        insts[0xCC] = abs(cpy, 4);
    }
    
    private static void flagCompare(CPUState regdata, int a, int b) {
        // comparison is between unsigned numbers
        int src = a - b;
        
        regdata.setFlag(CPUFlags.N, (src & 0x80) != 0);
        regdata.setFlag(CPUFlags.Z, a == b);
        regdata.setFlag(CPUFlags.C, a >= b);
    }
}
