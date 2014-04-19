package gui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import nes.GameRunnable;
import nes.Joypad;

public class Driver {
    public static void main(String[] args) {
        final EmulatorFrame frame = new EmulatorFrame();
        
        GameRunnable.UIUpdate ui = new GameRunnable.UIUpdate() {
            @Override
            public void update(int[] buffer) {
                frame.updateBuffer(buffer);
            }

            @Override
            public Joypad getJoypad(int player) {
                return frame.getJoypad();
            }
        };
        
        final GameRunnable nes;
        
        try {
            if (args.length == 0) {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    nes = GameRunnable.fromBestGuess(file, ui);
                } else {
                    return;
                }
            } else {
                nes = GameRunnable.fromBestGuess(new File(args[0]), ui);
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // linux-specific hack
                    new RepeatingReleasedEventsFixer().install();
                    
                    frame.setVisible(true);
                    new Thread(nes).start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            // Make sure Swing exits fully (I hate this)
            System.exit(0);
        }
    }
}
