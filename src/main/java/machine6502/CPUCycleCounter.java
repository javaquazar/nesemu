package machine6502;

public class CPUCycleCounter {
    private int cycles;
    
    public CPUCycleCounter() {
        cycles = 0;
    }
    
    public int getCycles() {
        return cycles;
    }
    
    public void addCycles(int cycles) {
        this.cycles += cycles;
    }
}
