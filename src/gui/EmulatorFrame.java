package gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;
import javax.swing.JPanel;

import nes.Joypad;
import nes.NESJoypad;

public class EmulatorFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JPanel panel;
    private BufferedImage image;
    
    private static class JoypadSwitch {
        public NESJoypad joypad;
    }
    
    private JoypadSwitch j;

    public EmulatorFrame() {
        j = new JoypadSwitch();
        j.joypad = new NESJoypad();
        image = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);

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
                    synchronized (j) {
                        j.joypad.releaseButton(button);
                    }
                }
            }
            
            @Override
            public void keyPressed(KeyEvent arg) {
                int button = keyCodeToButton(arg.getKeyCode());
                if (button >= 0) {
                    synchronized (j) {
                        j.joypad.pressButton(button);
                    }
                }
            }
        });
    }
    
    public void updateBuffer(int[] buffer) {
        WritableRaster r;
        r = image.getRaster();
        r.setDataElements(0, 0, 256, 240, buffer);
        panel.repaint();
    }
    
    public Joypad getJoypad() {
        NESJoypad oldJoypad;
        
        synchronized (j) {
            oldJoypad = j.joypad;
            j.joypad = oldJoypad.clone();
        }
        
        return oldJoypad;
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
