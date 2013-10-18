package machine6502.instructions;

import static machine6502.AddressInstruction.*;
import machine6502.CPUState;
import machine6502.Instruction;
import machine6502.Memory;

public class LoadInstructions {
    public static void assign(Instruction[] insts)
    {
        Operation lda_op, ldx_op, ldy_op;
        
        lda_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                regdata.a = rw.read();
                regdata.flagsNZ(regdata.a);
            }
        };
        
        ldx_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                regdata.x = rw.read();
                regdata.flagsNZ(regdata.x);
            }
        };
        
        ldy_op = new Operation() {
            @Override
            public void operate(CPUState regdata, Memory mem, ReadWrite rw) {
                regdata.y = rw.read();
                regdata.flagsNZ(regdata.y);
            }
        };
        
        Operation[] ops = {lda_op, ldx_op, ldy_op};
        int[][] opcodes
                = {{0xA9, 0xA5, 0xB5,   -1, 0xAD, 0xBD, 0xB9, 0xA1, 0xB1},
                   {0xA2, 0xA6,   -1, 0xB6, 0xAE,   -1, 0xBE,   -1,   -1},
                   {0xA0, 0xA4, 0xB4,   -1, 0xAC, 0xBC,   -1,   -1,   -1}};
        
        for (int i = 0; i < opcodes.length; i++) {
            Operation op = ops[i];
            int[] opc = opcodes[i];
            
            assignInst(insts, opc[0], imm (op, 2));
            assignInst(insts, opc[1], zp  (op, 3));
            assignInst(insts, opc[2], zpx (op, 4));
            assignInst(insts, opc[3], zpy (op, 4));
            assignInst(insts, opc[4], abs (op, 4));
            assignInst(insts, opc[5], absx(op, 4, true));
            assignInst(insts, opc[6], absy(op, 4, true));
            assignInst(insts, opc[7], indx(op, 6));
            assignInst(insts, opc[8], indy(op, 5, true));
        }
    }
    
    private static void assignInst(Instruction[] insts, int opcode,
                                   Instruction inst) {
        if (opcode != -1) {
            insts[opcode] = inst;
        }
    }
}