package nes.ppu;

public class PPURender {
    private PPURenderData renderData;
    private int lastPPUCycle;
    private int[] buffer;
    
    public PPURender(PPURenderData data, int[] buffer) {
        this.renderData = data;
        this.lastPPUCycle = 0;
        
        assert buffer.length >= 256*240;
        this.buffer = buffer;
    }

    public void advance(int ppuCycles) {
        int currentPPUCycle = lastPPUCycle + ppuCycles;
        
        for (int i = lastPPUCycle; i < currentPPUCycle; i++) {
            
        }
        
        this.lastPPUCycle = currentPPUCycle;
    }
}
