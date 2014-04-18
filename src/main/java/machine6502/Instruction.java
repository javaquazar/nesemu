package machine6502;

/**
 * Represents a 6502 instruction.
 *
 */
public interface Instruction {
    /**
     * Performs the instruction operation.
     * <p>
     * Note: regdata.pc is addressed at the opcode byte.
     * 
     * @param regdata
     * @param mem
     * @return The number of cycles consumed by the operation
     */
    public int operate(CPUState regdata, Memory mem);
}
