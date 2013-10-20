package memory;

import machine6502.Memory;

public class FixedSegmented implements Memory {
    private Memory[] segments;
    private int segmentLength;

    public FixedSegmented(int segments, int segmentLength) {
        this.segments = new Memory[segments];
        this.segmentLength = segmentLength;
    }
    
    public void setSegment(int segment, Memory mem) {
        segments[segment] = mem;
    }

    @Override
    public int readByte(int addr) {
        int segment = addr / segmentLength;
        int off = addr % segmentLength;
        
        return segments[segment].readByte(off);
    }

    @Override
    public void writeByte(int addr, int value) {
        int segment = addr / segmentLength;
        int off = addr % segmentLength;
        
        segments[segment].writeByte(off, value);
    }
}
