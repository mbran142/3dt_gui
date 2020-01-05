import javax.swing.*;
import java.awt.*;

//the main menu screen for 3D TicTacToe game
public class TTTMenu extends JPanel {

    //constants
    private final static int LOCAL = 0,
                             ONLINE = 1,
                             CPU = 2,
                             HELP = 3,
                             EXIT = 4,
                             BUTTON_AMNT = 5;

    private final static Color PANEL_BG = TTTDriver.BG;

    private final static String MAIN_PANEL = "main",
                                ONLINE_PANEL = "online",
                                TITLE_HTML_START = "<html><font size='6' color='#C06060'>",
                                TITLE_HTML_END = "</font></html>";

    public final static String MENU_IDENTIFIER = "menu",
                               LOCAL_COMMAND = "local",
                               ONLINE_COMMAND = "online",
                               CPU_COMMAND = "cpu",
                               HELP_COMMAND = "help",
                               HOST_COMMAND = "host",
                               JOIN_COMMAND = "join",
                               BACK_COMMAND = "back",
                               EXIT_COMMAND = "exit";

    //instance
    private CardLayout layout;
    private OnlineTextField ipField, portField;
    private JLabel title;

    //Send in driver that implements actionListener to create a menu screen
    public TTTMenu(TTTDriver action) {

        layout = new CardLayout();
        this.setLayout(layout);
        this.add(createMainPanel(action), MAIN_PANEL);
        this.add(createOnlinePanel(action), ONLINE_PANEL);
        layout.show(this, MAIN_PANEL);
    }

    //puts together the main panel on the main screen - startup menu
    private JPanel createMainPanel(TTTDriver action) {

        JPanel mainPanel, buttonPanel;
        JLabel title;

        //top label
        title = new JLabel("<html><font size='10' color='#C06060'>3<font color='#6060C0'>D" +
                "<font color='#606060'> Tic Tac Toe </font></html>");
        title.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        Box titleBox = Box.createHorizontalBox();
        titleBox.add(title);
        titleBox.add(Box.createHorizontalGlue());

        //buttons
        {
            buttonPanel = new JPanel(new GridLayout(0, 1, 0, 20));
            buttonPanel.setPreferredSize(new Dimension(580, 200));
            buttonPanel.setOpaque(false);
            String actionCommand, text;
            BorderlessButton button;

            for (int i = 0; i < BUTTON_AMNT; i++) {

                switch (i) {
                    case LOCAL:
                        actionCommand = LOCAL_COMMAND;
                        text = "Local Multiplayer";
                        break;
                    case ONLINE:
                        actionCommand = ONLINE_COMMAND;
                        text = "Online Multiplayer";
                        break;
                    case CPU:
                        actionCommand = CPU_COMMAND;
                        text = "Versus Computer";
                        break;
                    case HELP:
                        actionCommand = HELP_COMMAND;
                        text = "Help / Tutorial";
                        break;
                    case EXIT:
                        actionCommand = EXIT_COMMAND;
                        text = "Exit";
                        break;
                    default:
                        actionCommand = "ERROR";
                        text = "ERROR";
                        break;
                }

                button = new BorderlessButton(text, MENU_IDENTIFIER + "_" + actionCommand);
                button.addActionListener(action);

                //add each button to buttonPanel
                buttonPanel.add(button);
            }
        }

        //putting main panel together
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 60, 100));
        mainPanel.add(titleBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        mainPanel.add(buttonPanel);
        mainPanel.setBackground(PANEL_BG);

        return mainPanel;
    }

    //puts together the panel that allows for online game connection
    private JPanel createOnlinePanel(TTTDriver action) {

        JPanel onlinePanel, interactionPanel, ipPanel, portPanel, titlePanel;
        JLabel ipLabel, portLabel;
        JButton hostButton, joinButton, backButton;

        //top label
        title = new JLabel();
        titlePanel = new JPanel();
        titlePanel.add(title);
        //titlePanel.setPreferredSize(new Dimension(0, 180));
        titlePanel.setOpaque(false);

        //setting up text fields
        hostButton = new BorderlessButton("Host", MENU_IDENTIFIER + "_" + HOST_COMMAND);
        hostButton.addActionListener(action);
        hostButton.setPreferredSize(new Dimension(580,24));
        joinButton = new BorderlessButton("Join", MENU_IDENTIFIER + "_" + JOIN_COMMAND);
        joinButton.addActionListener(action);
        backButton = new BorderlessButton("Back", MENU_IDENTIFIER + "_" + BACK_COMMAND);
        backButton.addActionListener(action);
        ipLabel = new JLabel("<html><font size='5' color='#606060'> IP Address " +
                "<br><font size='4' color='#404040'> (only for join) </font></html");
        ipField = new OnlineTextField("");
        portLabel = new JLabel("<html><font size='5' color='#606060'> Port </font></html");
        portField = new OnlineTextField("");

        //setting up sub panels
        ipPanel = new JPanel(new GridLayout(1,2,20, 0));
        ipPanel.setOpaque(false);
        ipPanel.add(ipLabel);
        ipPanel.add(ipField);
        portPanel = new JPanel(new GridLayout(1,2,20,0));
        portPanel.setOpaque(false);
        portPanel.add(portLabel);
        portPanel.add(portField);

        //putting together interaction panel
        interactionPanel = new JPanel(new GridLayout(0, 1, 0, 20));
        interactionPanel.setPreferredSize(new Dimension(580, 200));
        interactionPanel.setOpaque(false);
        interactionPanel.add(hostButton);
        interactionPanel.add(joinButton);
        interactionPanel.add(ipPanel);
        interactionPanel.add(portPanel);
        interactionPanel.add(backButton);

        //putting online panel together
        onlinePanel = new JPanel();
        onlinePanel.setLayout(new BoxLayout(onlinePanel, BoxLayout.Y_AXIS));
        onlinePanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 60, 100));
        onlinePanel.add(titlePanel);
        onlinePanel.add(Box.createRigidArea(new Dimension(0, 100)));
        onlinePanel.add(interactionPanel);
        onlinePanel.setBackground(PANEL_BG);

        return onlinePanel;
    }

    //displays the main menu
    public void displayMainCard() {
        layout.show(this, MAIN_PANEL);
    }

    //displays the online connection panel
    public void displayOnlineCard() {
        layout.show(this, ONLINE_PANEL);
    }

    public void resetPanel() {
        title.setText(TITLE_HTML_START + "Host or Join online" + TITLE_HTML_END);
        ipField.setText("<IP address>");
        portField.setText("5432");
    }

    public String getPortField() {
        return portField.getText();
    }

    public String getIpField() {
        return ipField.getText();
    }

    public void setMessage(String s) {
        title.setText(TITLE_HTML_START + s + TITLE_HTML_END);
    }
}
