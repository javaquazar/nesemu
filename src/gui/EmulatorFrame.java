package gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;
import javax.swing.JPanel;

import nes.NESJoypad;

public class EmulatorFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private NESJoypad joypad;
    private JPanel panel;
    private BufferedImage image;

    public EmulatorFrame() {
        joypad = new NESJoypad();
        image = new BufferedImage(256, 240, BufferedImage.TYPE_3BYTE_BGR);

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                // 0x00RRGGBB
                g.drawImage(image, 0, 0, null);
            }
        };
        
        this.setContentPane(panel);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(256*2, 240*2);
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent arg0) {
            }
            
            @Override
            public void keyReleased(KeyEvent arg) {
                int button = keyCodeToButton(arg.getKeyCode());
                if (button >= 0) {
                    joypad.releaseButton(button);
                }
            }
            
            @Override
            public void keyPressed(KeyEvent arg) {
                int button = keyCodeToButton(arg.getKeyCode());
                if (button >= 0) {
                    joypad.pressButton(button);
                }
            }
        });
    }
    
    public void updateBuffer(int[] buffer) {
    	WritableRaster r;
    	r = image.getRaster();
    	r.setSamples(0, 0, 256, 240, 2, buffer);
    	panel.repaint();
    }
    
    private static int keyCodeToButton(int code) {
        // TODO - make customizable
        switch (code) {
        case 38: return NESJoypad.BUTTON_U;
        case 40: return NESJoypad.BUTTON_D;
        case 37: return NESJoypad.BUTTON_L;
        case 39: return NESJoypad.BUTTON_R;
        case 90: return NESJoypad.BUTTON_B;
        case 88: return NESJoypad.BUTTON_A;
        case 10: return NESJoypad.BUTTON_START;
        case 16: return NESJoypad.BUTTON_SELECT;
        default: return -1;
        }
    }
    
}
