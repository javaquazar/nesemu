package test;

import java.io.FileInputStream;
import java.io.IOException;

import nes.GameRunnable;

public class NESTest {
    public static void main(String[] args)
    	throws IOException, InterruptedException
    {

        FileInputStream input = new FileInputStream(args[0]);
        GameRunnable nes = new GameRunnable(input);
        input.close();
        
        nes.run();
    }
}
