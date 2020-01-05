import javax.swing.*;
import java.awt.*;

public class BorderlessButton extends JButton {

    //constants
    private final static Color BG = new Color(30,30,40);

    private final static String BUTTON_HTML_START = "<html><font size='5' color='#805050'> ",
                                BUTTON_HTML_END = "</font></html>";

    //construct borderless button with input text displayed and action command
    public BorderlessButton(String text, String actionCommand) {

        super(BUTTON_HTML_START + text + BUTTON_HTML_END);
        super.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setActionCommand(actionCommand);
    }

    //overrided to set custom mouseover and mouseclick color fills
    @Override
    protected void paintComponent(Graphics g) {

        if (getModel().isPressed())
            g.setColor(BG.brighter().brighter());
        else if (getModel().isRollover())
            g.setColor(BG.brighter());
        else
            g.setColor(BG);

        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    //overrided to ensure the default colors are not filled
    @Override
    public void setContentAreaFilled(boolean b) { }
}
