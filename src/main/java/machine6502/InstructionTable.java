package machine6502;

import machine6502.instructions.*;

class InstructionTable {
    private Instruction[] insts;
    
    public InstructionTable() {
        insts = new Instruction[256];
        
        // NOP (no operation)
        insts[0xEA] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                regdata.pc += 1;
                return 2;
            }
        };
        
        // BIT
        // zp
        insts[0x24] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                int m = mem.readByte(regdata.pc+1);
                int value = mem.readByte(m);
                
                regdata.setFlag(CPUFlags.Z, (value & regdata.a) == 0);
                regdata.setFlag(CPUFlags.V, (value & 0x40) != 0);
                regdata.setFlag(CPUFlags.N, (value & 0x80) != 0);
                
                regdata.pc += 2;
                return 3;
            }
        };
        // abs
        insts[0x2C] = new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                int m = MemUtils.readShort(mem, regdata.pc+1);
                int value = mem.readByte(m);
                
                regdata.setFlag(CPUFlags.Z, (value & regdata.a) == 0);
                regdata.setFlag(CPUFlags.V, (value & 0x40) != 0);
                regdata.setFlag(CPUFlags.N, (value & 0x80) != 0);
                
                regdata.pc += 3;
                return 4;
            }
        };
        
        // ASL/LSR/ROL/ROR
        BitshiftInstructions.assign(insts);
        
        // CMP/CPX/CPY
        CompareInstructions.assign(insts);
        
        // Set/clear flags
        FlagInstructions.assign(insts);
        
        // INC/DEC, INX/DEX, INY/DEY
        IncDecInstructions.assign(insts);
        
        // Branches, JMP, JSR, RTS, RTI
        JumpInstructions.assign(insts);
        
        // LDA/LDX/LDY
        LoadInstructions.assign(insts);
        
        // AND/ORA/EOR, ADC/SBC
        LogicalInstructions.assign(insts);
        
        // TSX/TXS, PHA/PLA, PHP/PLP
        StackInstructions.assign(insts);
        
        // STA/STX/STY
        StoreInstructions.assign(insts);
        
        // TAX/TXA, TAY/TYA
        TransferInstructions.assign(insts);
    }
    
    public Instruction getInstructionFromOpcode(int opcode) {
        return insts[opcode];
    }
}
