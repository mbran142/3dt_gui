import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class OnlineTextField extends JTextField {

    //constants
    private final static Color BG = new Color(30,30,40),
                               FG = new Color(0x703030),
                               BORDER_UP = new Color(20, 20, 30),
                               BORDER_DOWN = new Color(10, 10, 20);

    //constructs a text field with the input text string
    public OnlineTextField(String text) {

        super(text);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, BORDER_UP, BORDER_DOWN));
        this.setBackground(BG);
        this.setForeground(FG);
        this.setFont(new Font(this.getFont().getName(), Font.ITALIC, 18));
    }
}
