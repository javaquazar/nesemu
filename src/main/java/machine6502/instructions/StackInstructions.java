package machine6502.instructions;

import machine6502.CPUState;
import machine6502.CPUUtils;
import machine6502.Instruction;
import machine6502.Memory;

public class StackInstructions {
    public static void assign(Instruction[] insts) {
        // TXS
        insts[0x9A] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                regdata.sp = regdata.x;
                
                regdata.pc += 1;
                return 2;
            }
        };
        
        // TSX
        insts[0xBA] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                regdata.x = regdata.sp;
                regdata.flagsNZ(regdata.x);
                
                regdata.pc += 1;
                return 2;
            }
        };
        
        // PHA
        insts[0x48] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                CPUUtils.pushByte(regdata, mem, regdata.a);
                
                regdata.pc += 1;
                return 3;
            }
        };
        
        // PLA
        insts[0x68] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                regdata.a = CPUUtils.pullByte(regdata, mem);
                regdata.flagsNZ(regdata.a);
                
                regdata.pc += 1;
                return 4;
            }
        };
        
        // PHP
        insts[0x08] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                CPUUtils.pushByte(regdata, mem, regdata.flags | 0x30);
                
                regdata.pc += 1;
                return 3;
            }
        };
        
        // PLP
        insts[0x28] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                // always set the reserved and break flags
                regdata.flags = CPUUtils.pullByte(regdata, mem);
                
                regdata.pc += 1;
                return 4;
            }
        };
    }
}
