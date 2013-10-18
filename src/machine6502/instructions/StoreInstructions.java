package machine6502.instructions;

import static machine6502.AddressInstruction.*;
import machine6502.CPUState;
import machine6502.Instruction;
import machine6502.Memory;

public class StoreInstructions {
    public static void assign(Instruction[] insts)
    {
        Operation sta_op, stx_op, sty_op;
        
        sta_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                rw.write(regdata.a);
            }
        };
        
        stx_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                rw.write(regdata.x);
            }
        };
        
        sty_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                rw.write(regdata.y);
            }
        };

        insts[0x85] = zp  (sta_op, 3);
        insts[0x95] = zpx (sta_op, 4);
        insts[0x8D] = abs (sta_op, 4);
        insts[0x9D] = absx(sta_op, 5);
        insts[0x99] = absy(sta_op, 5);
        insts[0x81] = indx(sta_op, 6);
        insts[0x91] = indy(sta_op, 6);
        
        insts[0x86] = zp (stx_op, 3);
        insts[0x96] = zpy(stx_op, 4);
        insts[0x8E] = abs(stx_op, 4);
        
        insts[0x84] = zp (sty_op, 3);
        insts[0x94] = zpx(sty_op, 4);
        insts[0x8C] = abs(sty_op, 4);
    }
}
