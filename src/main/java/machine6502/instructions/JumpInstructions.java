package machine6502.instructions;

import machine6502.ByteUtils;
import machine6502.CPUFlags;
import machine6502.CPUState;
import machine6502.CPUUtils;
import machine6502.Instruction;
import machine6502.MemUtils;
import machine6502.Memory;

public class JumpInstructions {
    private static class BranchInstruction implements Instruction {
        private int flag;
        private boolean onSet;
        
        public BranchInstruction(int flag, boolean onSet)
        {
            this.flag = flag;
            this.onSet = onSet;
        }
    
        @Override
        public int operate(CPUState regdata, Memory mem) {
            boolean flagSet, takeBranch;
            
            flagSet = regdata.isFlagSet(flag);
            
            takeBranch = (flagSet == onSet);
            
            if (takeBranch) {
                int offset, new_addr;
                boolean crossesPage;
                
                offset = ByteUtils.unsignedToSigned(mem.readByte(regdata.pc+1));
                new_addr = (regdata.pc+2) + offset;
                
                crossesPage = MemUtils.crossesPageBoundary(regdata.pc, new_addr);
                
                regdata.pc = new_addr;
                return crossesPage ? 4:3;
            } else {
                regdata.pc += 2;
                return 2;
            }
        }
    }

    public static void assign(Instruction[] insts) {
        // Branches
        // BPL/BMI
        insts[0x10] = new BranchInstruction(CPUFlags.N, false);
        insts[0x30] = new BranchInstruction(CPUFlags.N, true);
        // BVC/BVS
        insts[0x50] = new BranchInstruction(CPUFlags.V, false);
        insts[0x70] = new BranchInstruction(CPUFlags.V, true);
        // BCC/BCS
        insts[0x90] = new BranchInstruction(CPUFlags.C, false);
        insts[0xB0] = new BranchInstruction(CPUFlags.C, true);
        // BNE/BEQ
        insts[0xD0] = new BranchInstruction(CPUFlags.Z, false);
        insts[0xF0] = new BranchInstruction(CPUFlags.Z, true);
        
        // JSR
        insts[0x20] = new Instruction() {
            public int operate(CPUState regdata, Memory mem) {
                int jump_addr = MemUtils.readShort(mem, regdata.pc+1);
                
                // push return address-1
                CPUUtils.pushShort(regdata, mem, regdata.pc+2);
                
                regdata.pc = jump_addr;
                return 6;
            }
        };
        
        // RTI
        insts[0x40] = new Instruction() {
            public int operate(CPUState regdata, Memory mem) {
                int jump_addr;
                
                regdata.flags = CPUUtils.pullByte(regdata, mem);
                jump_addr = CPUUtils.pullShort(regdata, mem);
                
                regdata.pc = jump_addr;
                return 6;
            }
        };
        
        // RTS
        insts[0x60] = new Instruction() {
            public int operate(CPUState regdata, Memory mem) {
                int jump_addr;
                
                jump_addr = CPUUtils.pullShort(regdata, mem)+1;
                
                regdata.pc = jump_addr;
                return 6;
            }
        };
        
        // JMP
        // absolute
        insts[0x4C] = new Instruction() {
            public int operate(CPUState regdata, Memory mem) {
                int jump_addr = MemUtils.readShort(mem, regdata.pc+1);
                
                regdata.pc = jump_addr;
                return 3;
            }
        };
        // indirect
        insts[0x6C] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                int ind_addr = MemUtils.readShort(mem, regdata.pc+1);
                int jump_addr = MemUtils.readShortPageWrap(mem, ind_addr);
                
                regdata.pc = jump_addr;
                return 5;
            }
        };
    }
}
