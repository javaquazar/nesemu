package test;

import java.io.FileInputStream;
import java.io.IOException;

import nes.Joypad;
import nes.GameRunnable;
import nes.PPU;
import nes.Registers2A03Memory;
import nes.RegistersPPUMemory;
import machine6502.CPU;
import machine6502.CPUState;
import memory.Mirrored;

public class NESTest {
    public static void main(String[] args) throws IOException, InterruptedException {

        FileInputStream input = new FileInputStream(args[0]);
        GameRunnable nes = new GameRunnable(input);
        input.close();
        
        nes.run();
    }
}
