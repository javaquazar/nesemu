package gui;

import javax.swing.SwingUtilities;


public class Driver {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // linux-specific hack
                new RepeatingReleasedEventsFixer().install();
                
                EmulatorFrame frame = new EmulatorFrame();
                frame.setVisible(true);
            }
        });
    }

}
