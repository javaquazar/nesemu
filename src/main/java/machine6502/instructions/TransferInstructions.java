package machine6502.instructions;

import machine6502.CPUState;
import machine6502.Instruction;
import machine6502.Memory;

public class TransferInstructions {
    private static class TransferInstruction implements Instruction {
        private int regfrom;
        private int regto;

        public TransferInstruction(int regfrom, int regto) {
            this.regfrom = regfrom;
            this.regto = regto;
        }
        
        @Override
        public int operate(CPUState regdata, Memory mem) {
            int value = regdata.getRegister(regfrom);
            regdata.setRegister(regto, value);
            regdata.flagsNZ(value);
            
            regdata.pc += 1;
            return 2;
        }
        
    }
    
    public static void assign(Instruction[] insts) {
        // TAX/TXA
        insts[0xAA] = new TransferInstruction(CPUState.A, CPUState.X);
        insts[0x8A] = new TransferInstruction(CPUState.X, CPUState.A);
        
        // TAY/TYA
        insts[0xA8] = new TransferInstruction(CPUState.A, CPUState.Y);
        insts[0x98] = new TransferInstruction(CPUState.Y, CPUState.A);
    }
}
