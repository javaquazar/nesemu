package machine6502;

public class AddressInstruction {

    public interface ReadWrite {
        public int read();
        public void write(int value);
    }
    
	public interface Operation {
		public void operate(CPUState regdata, Memory mem, ReadWrite rw);
	}
	
    private static final class ReadWriteImm implements ReadWrite {
        private int value;

        private ReadWriteImm(int value) {
            this.value = value;
        }

        @Override
        public int read() {
            return value;
        }

        @Override
        public void write(int value) {
            throw new IllegalStateException("Cannot write to immediate operand");
        }
    }
	
	private static class ReadWriteAddr implements ReadWrite {
	    private Memory mem;
	    private int addr;

        public ReadWriteAddr(Memory mem, int addr) {
            this.mem = mem;
            this.addr = addr;
        }

        @Override
        public int read() {
            return mem.readByte(addr);
        }

        @Override
        public void write(int value) {
            mem.writeByte(addr, value);
        }
	}
	
	public static Instruction imm(final Operation op, final int cycles) {
	    return new Instruction() {
            @Override
            public int operate(CPUState regdata, Memory mem) {
                final int value = mem.readByte(regdata.pc+1);
                
                op.operate(regdata, mem, new ReadWriteImm(value));
                regdata.pc += 2;
                return cycles;
            }
        };
	}
	
	public static Instruction zp(final Operation op, final int cycles) {
		return new Instruction() {
			@Override
			public int operate(CPUState regdata, Memory mem) {
				int zpaddr = mem.readByte(regdata.pc+1);
				
				op.operate(regdata, mem, new ReadWriteAddr(mem, zpaddr));
				regdata.pc += 2;
				return cycles;
			}
		};
	}
	
	public static Instruction zpx(final Operation op, final int cycles) {
		return new Instruction() {
			@Override
			public int operate(CPUState regdata, Memory mem) {
				int zpaddr = mem.readByte(regdata.pc+1);
				int new_addr = (zpaddr + regdata.x)%0x100;
				
				op.operate(regdata, mem, new ReadWriteAddr(mem, new_addr));
				regdata.pc += 2;
				return cycles;
			}
		};
	}
	
	public static Instruction zpy(final Operation op, final int cycles) {
		return new Instruction() {
			@Override
			public int operate(CPUState regdata, Memory mem) {
				int zpaddr = mem.readByte(regdata.pc+1);
				int new_addr = (zpaddr + regdata.y)%0x100;
				
				op.operate(regdata, mem, new ReadWriteAddr(mem, new_addr));
				regdata.pc += 2;
				return cycles;
			}
		};
	}
	
	public static Instruction abs(final Operation op, final int cycles) {
		return new Instruction() {
			@Override
			public int operate(CPUState regdata, Memory mem) {
				int addr = MemUtils.readShort(mem, regdata.pc+1);
				
				op.operate(regdata, mem, new ReadWriteAddr(mem, addr));
				regdata.pc += 3;
				return cycles;
			}
		};
	}
	
	public static Instruction absx(final Operation op, final int cycles) {
		return absx(op, cycles, false);
	}
	
	public static Instruction absx(final Operation op,
								   final int cycles, final boolean pageCycle) {
		return new Instruction() {
			@Override
			public int operate(CPUState regdata, Memory mem) {
				int addr = MemUtils.readShort(mem, regdata.pc+1);
				int new_addr = addr + regdata.x;
				
				op.operate(regdata, mem, new ReadWriteAddr(mem, new_addr));
				regdata.pc += 3;
				
				if (pageCycle) {
					boolean crosses;
					crosses = MemUtils.crossesPageBoundary(addr, new_addr);
					
					return crosses ? cycles+1:cycles;
				} else {
					return cycles;
				}
			}
		};
	}
	
	public static Instruction absy(final Operation op, final int cycles) {
		return absy(op, cycles, false);
	}
	
	public static Instruction absy(final Operation op,
			   					   final int cycles, final boolean pageCycle) {
		return new Instruction() {
			@Override
			public int operate(CPUState regdata, Memory mem) {
				int addr = MemUtils.readShort(mem, regdata.pc+1);
				int new_addr = addr + regdata.y;
				
				op.operate(regdata, mem, new ReadWriteAddr(mem, new_addr));
				regdata.pc += 3;
				
				if (pageCycle) {
					boolean crosses;
					crosses = MemUtils.crossesPageBoundary(addr, new_addr);
					
					return crosses ? cycles+1:cycles;
				} else {
					return cycles;
				}
			}
		};
	}
	
	public static Instruction indx(final Operation op, final int cycles) {
		return new Instruction() {
			@Override
			public int operate(CPUState regdata, Memory mem) {
				int addr = mem.readByte(regdata.pc+1);
				int new_addr = MemUtils.readShort(mem, addr + regdata.x);
				
				op.operate(regdata, mem, new ReadWriteAddr(mem, new_addr));
				regdata.pc += 2;
				return cycles;
			}
		};
	}
	
	public static Instruction indy(final Operation op, final int cycles) {
		return indy(op, cycles, false);
	}
	
	public static Instruction indy(final Operation op,
			   					   final int cycles, final boolean pageCycle) {
		return new Instruction() {
			@Override
			public int operate(CPUState regdata, Memory mem) {
				int addr = mem.readByte(regdata.pc+1);
				int new_addr = MemUtils.readShort(mem, addr) + regdata.y;
				
				op.operate(regdata, mem, new ReadWriteAddr(mem, new_addr));
				regdata.pc += 2;
				
				if (pageCycle) {
					boolean crosses;
					crosses = MemUtils.crossesPageBoundary(addr, new_addr);
					
					return crosses ? cycles+1:cycles;
				} else {
					return cycles;
				}
			}
		};
	}
}
