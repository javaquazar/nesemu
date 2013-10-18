package machine6502.instructions;

import static machine6502.AddressInstruction.*;
import machine6502.CPUFlags;
import machine6502.CPUState;
import machine6502.Instruction;
import machine6502.Memory;

public class BitshiftInstructions {    
    private static class Acc implements Instruction {
        private Operation op;
        
        public Acc(Operation op) {
            this.op = op;
        }

        @Override
        public int operate(final CPUState regdata, Memory mem) {
            op.operate(regdata, mem, new ReadWrite() {
                @Override
                public void write(int value) {
                    regdata.a = value;
                }
                
                @Override
                public int read() {
                    return regdata.a;
                }
            });
            regdata.pc += 1;
            return 2;
        }
    }
    
    public static void assign(Instruction[] insts) {
        Operation asl_op, lsr_op, rol_op, ror_op;
        
        asl_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                boolean carry = (old_value & 0x80) != 0;
                int value = (old_value << 1) & 0xFF;
                
                regdata.flagsNZ(value);
                regdata.setFlag(CPUFlags.C, carry);
                
                rw.write(value);
            }
        };
        
        lsr_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                boolean carry = (old_value & 0x01) != 0;
                int value = (old_value >> 1) & 0xFF;
                
                regdata.flagsNZ(value);
                regdata.setFlag(CPUFlags.C, carry);
                
                rw.write(value);
            }
        };
        
        rol_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                boolean carry = (old_value & 0x80) != 0;
                int value = (old_value << 1) & 0xFF;
                
                if (regdata.isFlagSet(CPUFlags.C)) {
                    value |= 0x01;
                }
                
                regdata.flagsNZ(value);
                regdata.setFlag(CPUFlags.C, carry);

                rw.write(value);
            }
        };
        
        ror_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                boolean carry = (old_value & 0x01) != 0;
                int value = (old_value >> 1) & 0xFF;
                
                if (regdata.isFlagSet(CPUFlags.C)) {
                    value |= 0x80;
                }
                
                regdata.flagsNZ(value);
                regdata.setFlag(CPUFlags.C, carry);

                rw.write(value);
            }
        };
        
        Operation[] ops = {asl_op, lsr_op, rol_op, ror_op};
        int[][] opcodes = {{0x0A, 0x06, 0x16, 0x0E, 0x1E},
                           {0x4A, 0x46, 0x56, 0x4E, 0x5E},
                           {0x2A, 0x26, 0x36, 0x2E, 0x3E},
                           {0x6A, 0x66, 0x76, 0x6E, 0x7E}};
        
        for (int i = 0; i < ops.length; i++) {
            Operation op = ops[i];
            int[] opc = opcodes[i];
            
            insts[opc[0]] = new Acc(op);
            insts[opc[1]] = zp  (op, 5);
            insts[opc[2]] = zpx (op, 6);
            insts[opc[3]] = abs (op, 6);
            insts[opc[4]] = absx(op, 7);
        }
    }
}
