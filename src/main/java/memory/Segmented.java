package memory;

import java.util.List;
import java.util.ArrayList;

import machine6502.Memory;

public class Segmented implements Memory {
    private static class Segment implements Comparable<Segment> {
        public int lower, upper;
        public Memory mem;

        public Segment(int lower, int upper, Memory mem) {
            this.lower = lower;
            this.upper = upper;
            this.mem = mem;
        }

        @Override
        public int compareTo(Segment seg) {
            return this.lower - seg.lower;
        }
    }
    
    // TODO (maybe) - use some sort of BST instead of a list
    private List<Segment> segments;
    
    public Segmented() {
        this.segments = new ArrayList<>();
    }
    
    /**
     * @param lower The first addressable byte
     * @param upper The last addressable byte
     * @param mem
     */
    public void addSegment(int lower, int upper, Memory mem) {
        segments.add(new Segment(lower, upper, mem));
    }
    
    private Segment findSegment(int addr) {
        for (Segment seg: segments) {
            if (addr >= seg.lower && addr <= seg.upper) {
                return seg;
            }
        }
        return null;
    }

    @Override
    public int readByte(int addr) {
        Segment seg = findSegment(addr);
        
        return seg.mem.readByte(addr - seg.lower);
    }

    @Override
    public void writeByte(int addr, int value) {
        Segment seg = findSegment(addr);
        
        seg.mem.writeByte(addr - seg.lower, value);
    }
}
