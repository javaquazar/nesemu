package machine6502.instructions;

import machine6502.CPUFlags;
import machine6502.CPUState;
import machine6502.Instruction;
import machine6502.Memory;

public class FlagInstructions {
    private static class FlagSetClearInstruction implements Instruction {
        private int flag;
        private boolean set;

        public FlagSetClearInstruction(int flag, boolean set) {
            this.flag = flag;
            this.set = set;
        }

        @Override
        public int operate(CPUState regdata, Memory mem) {
            regdata.setFlag(flag, set);
            
            regdata.pc += 1;
            return 2;
        }
    }
    
    public static void assign(Instruction[] insts) {
        insts[0x18] = new FlagSetClearInstruction(CPUFlags.C, false);
        insts[0x38] = new FlagSetClearInstruction(CPUFlags.C, true);
        
        insts[0x58] = new FlagSetClearInstruction(CPUFlags.I, false);
        insts[0x78] = new FlagSetClearInstruction(CPUFlags.I, true);
        
        insts[0xB8] = new FlagSetClearInstruction(CPUFlags.V, false);
        
        insts[0xD8] = new FlagSetClearInstruction(CPUFlags.D, false);
        insts[0xF8] = new FlagSetClearInstruction(CPUFlags.D, true);
    }
}
