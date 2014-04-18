package nes;

public class NESJoypad implements Joypad {
    public static final int BUTTON_A = 0;
    public static final int BUTTON_B = 1;
    public static final int BUTTON_SELECT = 2;
    public static final int BUTTON_START = 3;
    public static final int BUTTON_U = 4;
    public static final int BUTTON_D = 5;
    public static final int BUTTON_L = 6;
    public static final int BUTTON_R = 7;
    
    private boolean[] buttonPressed;
    
    public NESJoypad() {
        this.buttonPressed = new boolean[8];
    }
    
    @Override
    public NESJoypad clone() {
        NESJoypad j = new NESJoypad();
        j.buttonPressed = this.buttonPressed.clone();
        
        return j;
    }

    public void pressButton(int button) {
        buttonPressed[button] = true;
    }
    
    public void releaseButton(int button) {
        buttonPressed[button] = false;
    }

    @Override
    public int getButtonCount() {
        return 8;
    }

    @Override
    public boolean getButton(int button) {
        return buttonPressed[button];
    }

}
