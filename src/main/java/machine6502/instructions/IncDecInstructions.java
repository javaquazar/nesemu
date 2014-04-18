package machine6502.instructions;

import static machine6502.AddressInstruction.*;
import machine6502.CPUState;
import machine6502.Instruction;
import machine6502.Memory;

public class IncDecInstructions {
    public static class IncDecInstruction implements Instruction {
        private int register;
        private int change;

        public IncDecInstruction(int register, int change) {
            this.register = register;
            this.change = change;
        }

        @Override
        public int operate(CPUState regdata, Memory mem) {
            int value = (regdata.getRegister(register) + change)&0xFF;
            regdata.setRegister(register, value);
            regdata.flagsNZ(value);
            
            regdata.pc += 1;
            return 2;
        }
    }
    
    public static void assign(Instruction[] insts) {
        Operation inc_op, dec_op;
        
        inc_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int value = (rw.read() + 1)&0xFF;
                regdata.flagsNZ(value);
                rw.write(value);
            }
        };
        
        dec_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int value = (rw.read() - 1)&0xFF;
                regdata.flagsNZ(value);
                rw.write(value);
            }
        };
        
        // INC
        insts[0xE6] = zp  (inc_op, 5);
        insts[0xF6] = zpx (inc_op, 6);
        insts[0xEE] = abs (inc_op, 6);
        insts[0xFE] = absx(inc_op, 7);
        
        // DEC
        insts[0xC6] = zp  (dec_op, 5);
        insts[0xD6] = zpx (dec_op, 6);
        insts[0xCE] = abs (dec_op, 6);
        insts[0xDE] = absx(dec_op, 7);
        
        // DEX/INX
        insts[0xCA] = new IncDecInstruction(CPUState.X, -1);
        insts[0xE8] = new IncDecInstruction(CPUState.X, +1);
        
        // DEY/INY
        insts[0x88] = new IncDecInstruction(CPUState.Y, -1);
        insts[0xC8] = new IncDecInstruction(CPUState.Y, +1);
    }
}
