package machine6502.instructions;

import static machine6502.AddressInstruction.*;
import machine6502.CPUFlags;
import machine6502.CPUState;
import machine6502.Instruction;
import machine6502.Memory;

public class LogicalInstructions {
    public static void assign(Instruction[] insts) {
        Operation and_op, ora_op, eor_op, adc_op, sbc_op;
        
        and_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                
                int new_value = regdata.a & old_value;
                regdata.a = new_value;
                regdata.flagsNZ(new_value);
            }
        };
        
        ora_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                
                int new_value = regdata.a | old_value;
                regdata.a = new_value;
                regdata.flagsNZ(new_value);
            }
        };
        
        eor_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                
                int new_value = regdata.a ^ old_value;
                regdata.a = new_value;
                regdata.flagsNZ(new_value);
            }
        };
        
        adc_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                int value = regdata.a + old_value;
                
                if (regdata.isFlagSet(CPUFlags.C)) {
                    value++;
                }
                
                boolean carry = value >= 0x100;
                value &= 0xFF;
                boolean overflow = (((regdata.a ^ old_value) & 0x80) == 0) &&
                                   (((regdata.a ^ value)     & 0x80) != 0);
                
                regdata.a = value;
                regdata.flagsNZ(value);
                regdata.setFlag(CPUFlags.C, carry);
                regdata.setFlag(CPUFlags.V, overflow);
            }
        };
        
        sbc_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                int old_value = rw.read();
                int value = regdata.a - old_value;
                
                if (regdata.isFlagSet(CPUFlags.C)) {
                    value--;
                }
                
                boolean carry = value >= 0;
                value &= 0xFF;
                boolean overflow = (((regdata.a ^ old_value) & 0x80) != 0) &&
                                   (((regdata.a ^ value)     & 0x80) != 0);
                
                regdata.a = value;
                regdata.flagsNZ(value);
                regdata.setFlag(CPUFlags.C, carry);
                regdata.setFlag(CPUFlags.V, overflow);
            }
        };
        
        Operation[] ops = {and_op, ora_op, eor_op, adc_op, sbc_op};
        int[][] opcodes = {{0x29, 0x25, 0x35, 0x2D, 0x3D, 0x39, 0x21, 0x31},
                           {0x09, 0x05, 0x15, 0x0D, 0x1D, 0x19, 0x01, 0x11},
                           {0x49, 0x45, 0x55, 0x4D, 0x5D, 0x59, 0x41, 0x51},
                           
                           {0x69, 0x65, 0x75, 0x6D, 0x7D, 0x79, 0x61, 0x71},
                           {0xE9, 0xE5, 0xF5, 0xED, 0xFD, 0xF9, 0xE1, 0xF1}};
        
        for (int i = 0; i < ops.length; i++) {
            Operation op = ops[i];
            int[] opc = opcodes[i];
            
            insts[opc[0]] = imm (op, 2);
            insts[opc[1]] = zp  (op, 3);
            insts[opc[2]] = zpx (op, 4);
            insts[opc[3]] = abs (op, 4);
            insts[opc[4]] = absx(op, 4, true);
            insts[opc[5]] = absy(op, 4, true);
            insts[opc[6]] = indx(op, 6);
            insts[opc[7]] = indy(op, 5, true);
        }
    }
}
