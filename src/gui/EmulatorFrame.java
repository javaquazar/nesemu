package gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nes.NESJoypad;

public class EmulatorFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private NESJoypad joypad;

    public EmulatorFrame() {
        joypad = new NESJoypad();
        
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = 1L;
            
            private BufferedImage image;
            private int x;
            
            {
                x = 0;
                image = new BufferedImage(256, 240, BufferedImage.TYPE_3BYTE_BGR);
                
                new javax.swing.Timer(1000/60, new ActionListener() {
                    
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        repaint();
                    }
                }).start();
            }
            
            @Override
            public void paintComponent(Graphics g) {
                
                int[] arr = {0x000000FF, 0x00FFFFFF};
                // 0x00RRGGBB
                image.setRGB(x++, 100, 2, 1, arr, 0, 256);
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
