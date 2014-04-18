package machine6502;

import java.io.PrintStream;

/**
 * The interrupt methods should only be called:
 * <ul>
 * <li>
 *   While a run method is executing, and on the same thread
 *     (for example, in a read/write operation that implements Memory)
 * </li>
 * <li>
 *   While a run method is not executing
 *     (for example, in between calls of runForXCycles)
 * </li>
 * </ul>
 * 
 * Calling interrupt methods on a different thread while the 6502 CPU is running
 * is undefined.
 */
public class CPU {
    private InstructionTable instTable;
    private CPUState regdata;
    private Memory mem;
    private boolean nmi_flag, irq_flag;
    
    public CPU(Memory mem) {
        this.instTable = new InstructionTable();
        this.regdata = new CPUState();
        regdata.flags = 0x30;
        this.mem = mem;
    }
    
    public void reset() {
        int jump_addr = MemUtils.readResetVector(mem);
        this.regdata.pc = jump_addr;
    }
    
    public void reset(int resetAddr) {
        this.regdata.pc = resetAddr;
    }

    /**
     * Non-Maskable Interrupt
     * - must be dealt with (can't be masked like IRQ)
     * */
    public void interruptNMI() {
        this.nmi_flag = true;
    }
    
    /**
     * Interrupt ReQuest
     * - interrupts only if the interrupt-disable flag is cleared
     */
    public void interruptIRQ() {
        if (!regdata.isFlagSet(CPUFlags.I)) {
            this.irq_flag = true;
        }
    }
    
    /**
     * Runs the 6502 machine for a specified amount of cycles.
     * 
     * @param cycles The requested amount of cycles to run for.
     * @return The actual amount of cycles ran.
     *         Always greater than or equal to cycles.
     */
    public int runForXCycles(int cycles, CPUCycleCounter cycleCounter) {
        int cyclesLeft = cycles;
        
        while (cyclesLeft > 0) {
            if (checkAndRunInterrupts()) {
                cyclesLeft -= 7;
            } else {
                int opcode = mem.readByte(regdata.pc);
                int inst_cycles = runOpcode(opcode);
                cyclesLeft -= inst_cycles;
                cycleCounter.addCycles(inst_cycles);
            }
        }
        
        return cycles - cyclesLeft;
    }
    
    public int runForXCycles(int cycles) {
        int cyclesLeft = cycles;
        
        while (cyclesLeft > 0) {
            if (checkAndRunInterrupts()) {
                cyclesLeft -= 7;
            } else {
                int opcode = mem.readByte(regdata.pc);
                int inst_cycles = runOpcode(opcode);
                cyclesLeft -= inst_cycles;
            }
        }
        
        return cycles - cyclesLeft;
    }
    
    /**
     * Run the 6502 machine until the BRK operator is encountered.
     */
    public void runUntilBreak() {
        boolean running = true;
        
        while (running) {
            checkAndRunInterrupts();
            
            int opcode = mem.readByte(regdata.pc);
            
            if (opcode != 0x00) {
                runOpcode(opcode);
            } else {
                running = false;
            }
        }
    }
    
    public void printDebug(PrintStream out) {
        out.println(regdata);
        
        memory.DebugUtil.printDebug(out, mem);
    }
    
    private boolean checkAndRunInterrupts() {
        boolean interrupt = false;
        int jump_addr = -1;
        
        if (nmi_flag) {
            interrupt = true;
            nmi_flag = false;
            
            jump_addr = MemUtils.readNMIVector(mem);
        } else if (irq_flag) {
            interrupt = true;
            irq_flag = false;
            
            jump_addr = MemUtils.readIRQVector(mem);
        }
        
        if (interrupt) {
            CPUUtils.pushShort(regdata, mem, regdata.pc);
            CPUUtils.pushByte(regdata, mem, regdata.flags);
            
            // set interrupt-disable flag
            regdata.setFlag(CPUFlags.I);
            
            // jump
            regdata.pc = jump_addr;
        }
        
        return interrupt;
    }
    
    private int runOpcode(int opcode) {
        Instruction inst = instTable.getInstructionFromOpcode(opcode);
        
        if (inst != null) {
            return inst.operate(regdata, mem);
        } else {
            invalidOpcode(opcode);
            return 0;
        }
    }

    private void invalidOpcode(int opcode) {
        throw new IllegalStateException(String.format("Invalid opcode (%02X)", opcode));
    }
}
