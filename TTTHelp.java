import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TTTHelp extends JPanel implements ActionListener {

    //constants
    public final static String HELP_IDENTIFIER = "help",
                               EXIT_COMMAND = "back";

    //instance
    private int cardID;
    private CardLayout layout;
    private JPanel helpPanel;
    BorderlessButton backButton, nextButton;

    public TTTHelp(TTTDriver action) {

        JPanel controlPanel, exitPanel, titlePanel;
        BorderlessButton exitButton;
        JLabel title;

        //help panel
        layout = new CardLayout();
        helpPanel = new JPanel(layout);
        helpPanel.add(createHelpPanel_1(), "1");
        helpPanel.add(createHelpPanel_2(), "2");
        helpPanel.add(createHelpPanel_3(), "3");
        helpPanel.add(createHelpPanel_4(), "4");
        helpPanel.setOpaque(false);

        //control panel buttons
        backButton = new BorderlessButton("<html> &lt; </html>", "back");
        backButton.addActionListener(this);
        nextButton = new BorderlessButton("<html> > </html>", "next");
        nextButton.addActionListener(this);

        //add buttons to control panel
        controlPanel = new JPanel(new GridLayout(1,2,60,0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0,140,20,140));
        controlPanel.add(backButton);
        controlPanel.add(nextButton);
        controlPanel.setOpaque(false);

        //title panel
        title = new JLabel("<html><font size='6' color='#C0C090'> How to play </font></html>");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel = new JPanel(new GridLayout(1,1,0,0));
        titlePanel.add(title);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20,100,20,100));
        titlePanel.setOpaque(false);

        //exit panel
        exitButton = new BorderlessButton("Back", HELP_IDENTIFIER + "_" + EXIT_COMMAND);
        exitButton.addActionListener(action);
        exitPanel = new JPanel();
        exitPanel.setLayout(new BoxLayout(exitPanel, BoxLayout.Y_AXIS));
        exitPanel.add(Box.createRigidArea(new Dimension(0,20)));
        exitPanel.add(exitButton);
        exitPanel.add(Box.createVerticalGlue());
        exitPanel.add(Box.createRigidArea(new Dimension(100,0)));
        exitPanel.setOpaque(false);

        //put everything together on main panel (this)
        this.setLayout(new BorderLayout());
        this.add(titlePanel, BorderLayout.NORTH);
        this.add(helpPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);
        this.add(exitPanel, BorderLayout.WEST);
        this.add(Box.createRigidArea(new Dimension(40,0)), BorderLayout.EAST);
        this.setBackground(TTTDriver.BG);
    }

    private JPanel createHelpPanel_1() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        //text
        JLabel helpLabel = new JLabel("<html><font size='5' color='#C0C090'>" +
                "3D TicTacToe is just like regular 2D TicTacToe! But instead of the two-dimensional 3x3 board " +
                "you're used to, this game is played with a <b>three</b>-dimensional 4x4x4 board. However, " +
                "since the game is played on a 2D screen, the 3D board is displayed as four separate 2D boards." +
                "</font></html>");

        helpLabel.setPreferredSize(new Dimension(190,410));
        JPanel helpPanel = new JPanel();
        helpPanel.add(helpLabel);
        helpPanel.setOpaque(false);
        panel.add(helpPanel);

        //picture display
        JPanel boardDisplay, tempPanel;
        boardDisplay = new JPanel(new GridLayout(4,1,0,10));

        for (int i = 0; i < 4; i++) {

            tempPanel = new JPanel(new GridLayout(4,4,0,0));
            for (int j = 0; j < 16; j++)
                tempPanel.add(new BoardButton());

            boardDisplay.add(tempPanel);
        }

        boardDisplay.setOpaque(false);
        panel.add(boardDisplay);

        panel.setOpaque(false);
        return panel;
    }

    private JPanel createHelpPanel_2() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        //text
        JLabel helpLabel = new JLabel("<html><font size='5' color='#C0C090'> " +
                "It's weird at first, but try to think of the four 2D boards stacking on top of each other to form a " +
                "4x4x4 cube. For example, the blue <font color='#9595F0'>O<font color='#C0C090'>'s are a valid win! " +
                "Again, if you think about it as one big cube, then the <font color='#9595F0'>O" +
                "<font color='#C0C090'>'s would make a line through the 3D board. Of course, you can also win on a " +
                "single board, shown by the <font color='#F08080'>X<font color='#C0C090'>'s.</font></html>");

        helpLabel.setPreferredSize(new Dimension(190,410));
        JPanel helpPanel = new JPanel();
        helpPanel.add(helpLabel);
        helpPanel.setOpaque(false);
        panel.add(helpPanel);

        //picture display
        JPanel boardDisplay, tempPanel;
        BoardButton boardButton;
        boardDisplay = new JPanel(new GridLayout(4,1,0,10));

        for (int i = 0; i < 4; i++) {

            tempPanel = new JPanel(new GridLayout(4,4,0,0));
            for (int j = 0; j < 16; j++) {

                //four in a row through multiple boards example
                boardButton = new BoardButton();
                if (j == 2) {
                    boardButton.setStatus(1); //1 = O
                    boardButton.setHighlight(true);
                }

                //four in a row through one board example
                else if (i == 1 && j % 5 == 0) {
                    boardButton.setStatus(2); //2 = X
                    boardButton.setHighlight(true);
                }

                tempPanel.add(boardButton);
            }

            boardDisplay.add(tempPanel);
        }

        boardDisplay.repaint();
        boardDisplay.revalidate();
        boardDisplay.setOpaque(false);
        panel.add(boardDisplay);

        panel.setOpaque(false);
        return panel;
    }

    private JPanel createHelpPanel_3() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        //text
        JLabel helpLabel = new JLabel("<html><font size='5' color='#C0C090'> Now for a few more examples. " +
                "The <font color='#9595F0'>O<font color='#C0C090'>'s are a valid win since they form a diagonal " +
                "through the 3D board. Again, think about it as if the four boards are stacked on top of each other, " +
                "and it's not <i>too</i> hard to see. Finally, the <font color='#F08080'>X<font color='#C0C090'>'s " +
                "are a win as well. This one can be pretty hard to see, but it's a win since the " +
                "<font color='#F08080'>X<font color='#C0C090'>'s connect the corners of the cube.</font></html>");

        helpLabel.setPreferredSize(new Dimension(190,410));
        JPanel helpPanel = new JPanel();
        helpPanel.add(helpLabel);
        helpPanel.setOpaque(false);
        panel.add(helpPanel);

        //picture display
        JPanel boardDisplay, tempPanel;
        BoardButton boardButton;
        boardDisplay = new JPanel(new GridLayout(4,1,0,10));

        for (int i = 0; i < 4; i++) {

            tempPanel = new JPanel(new GridLayout(4,4,0,0));
            for (int j = 0; j < 16; j++) {

                //diagonal four in a row through multiple boards example
                boardButton = new BoardButton();
                if (i + j == 3) {
                    boardButton.setStatus(1); //1 = O
                    boardButton.setHighlight(true);
                }

                //diagonal across the whole cube example
                else if (i * 3 + j == 12) {
                    boardButton.setStatus(2); //2 = X
                    boardButton.setHighlight(true);
                }

                tempPanel.add(boardButton);
            }

            boardDisplay.add(tempPanel);
        }

        boardDisplay.repaint();
        boardDisplay.revalidate();
        boardDisplay.setOpaque(false);
        panel.add(boardDisplay);

        panel.setOpaque(false);
        return panel;
    }

    private JPanel createHelpPanel_4() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        //text
        JLabel helpLabel = new JLabel("<html><font size='5' color='#C0C090'> And that's it! See if you can see " +
                "why the <font color='#F08080'>X<font color='#C0C090'>'s and <font color='#9595F0'>O" +
                "<font color='#C0C090'>'s are wins. <br><br> But besides that, <br><font size='6'> " +
                "Enjoy the game!</font></html>");

        helpLabel.setPreferredSize(new Dimension(190,200));
        JPanel helpPanel = new JPanel();
        helpPanel.add(helpLabel);
        helpPanel.setOpaque(false);
        panel.add(helpPanel);

        //picture display
        JPanel boardDisplay, tempPanel;
        BoardButton boardButton;
        boardDisplay = new JPanel(new GridLayout(4,1,0,10));

        for (int i = 0; i < 4; i++) {

            tempPanel = new JPanel(new GridLayout(4,4,0,0));
            for (int j = 0; j < 16; j++) {

                //second diagonal four in a row through multiple boards example
                boardButton = new BoardButton();
                if (i + 4 == j) {
                    boardButton.setStatus(2); //2 = X
                    boardButton.setHighlight(true);
                }

                //second diagonal across the whole cube example
                else if ((i + 1) * 3 == j) {
                    boardButton.setStatus(1); //1 = O
                    boardButton.setHighlight(true);
                }

                tempPanel.add(boardButton);
            }

            boardDisplay.add(tempPanel);
        }

        boardDisplay.repaint();
        boardDisplay.revalidate();
        boardDisplay.setOpaque(false);
        panel.add(boardDisplay);

        panel.setOpaque(false);
        return panel;
    }

    public void prepare() {
        cardID = 1;
        layout.show(helpPanel, "1");
    }

    public void actionPerformed(ActionEvent e) {

        //change the helpPanel card and disable / enable buttons where needed
        String command = e.getActionCommand();

        if (command.equals("next") && cardID != 4)
            cardID++;

        else if (command.equals("back") && cardID != 1)
            cardID--;

        layout.show(helpPanel, Integer.toString(cardID));
    }
}
