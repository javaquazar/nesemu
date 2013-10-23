package gui;

import java.io.FileInputStream;

import javax.swing.SwingUtilities;

import nes.GameRunnable;


public class Driver {

    public static void main(String[] args) throws Exception {
        final EmulatorFrame frame = new EmulatorFrame();
        
        FileInputStream input = new FileInputStream(args[0]);
        GameRunnable.UIUpdate ui = new GameRunnable.UIUpdate() {
			@Override
			public void update(int[] buffer) {
				frame.updateBuffer(buffer);
			}
		};
		
        final GameRunnable nes = new GameRunnable(input, ui);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // linux-specific hack
                new RepeatingReleasedEventsFixer().install();
                
                frame.setVisible(true);
                new Thread(nes).start();
            }
        });
    }

}
