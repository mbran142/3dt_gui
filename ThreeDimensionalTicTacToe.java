import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.StringTokenizer;

//main driver for 3D TicTacToe game
class TTTDriver extends JPanel implements ActionListener {

    //constants
    private final static String MENU_PANEL = "menu",
                                GAME_PANEL = "game",
                                HELP_PANEL = "help";

    public final static Color BG = new Color(10,10,20);

    //local variables
    private TTTMenu menu;
    private TTTGame game;
    private TTTHelp help;
    private CardLayout layout;
    private OnlineWorker onlineWorker;

    //Put together pieces of game
    public TTTDriver() {

        //setting up each main part of the game
        menu = new TTTMenu(this);
        game = new TTTGame(this);
        help = new TTTHelp(this);

        //putting everything together
        layout = new CardLayout();
        this.setLayout(layout);
        this.add(menu, MENU_PANEL);
        this.add(game, GAME_PANEL);
        this.add(help, HELP_PANEL);

        //show menu screen
        layout.show(this, MENU_PANEL);
    }

    //main action method - all buttons pressed are handled in this method
    public void actionPerformed(ActionEvent e) {

        StringTokenizer st = new StringTokenizer(e.getActionCommand(), "_");

        switch (st.nextToken()) {
            case TTTMenu.MENU_IDENTIFIER: menuAction(st.nextToken()); break;
            case TTTGame.GAME_IDENTIFIER: gameAction(st.nextToken()); break;
            case TTTHelp.HELP_IDENTIFIER: helpAction(st.nextToken()); break;
        }
    }

    //handles menu button presses
    private void menuAction(String action) {

        switch (action) {
            case TTTMenu.LOCAL_COMMAND:
                game.setGameType(TTTGame.LOCAL);
                layout.show(this, GAME_PANEL);
                break;

            case TTTMenu.ONLINE_COMMAND:
                menu.resetPanel();
                menu.displayOnlineCard();
                break;

            case TTTMenu.CPU_COMMAND:
                game.setGameType(TTTGame.CPU);
                layout.show(this, GAME_PANEL);
                break;

            case TTTMenu.HELP_COMMAND:
                help.prepare();
                layout.show(this, HELP_PANEL);
                break;

            case TTTMenu.HOST_COMMAND: {
                onlineWorker = new HostWorker(this, menu, game);
                onlineWorker.attemptConnection();
                break;
            }

            case TTTMenu.JOIN_COMMAND: {
                onlineWorker = new JoinWorker(this, menu, game);
                onlineWorker.attemptConnection();
                break;
            }

            case TTTMenu.BACK_COMMAND:
                cancelConnection();
                menu.displayMainCard();
                break;

            case TTTMenu.EXIT_COMMAND:
                System.exit(0);
        }
    }

    //handles game button presses
    private void gameAction(String action) {

        switch (action) {
            case TTTGame.EXIT_COMMAND:
                game.closeSocket();
                menu.displayMainCard();
                layout.show(this, MENU_PANEL);
                break;

            case TTTGame.START_COMMAND:
                if (game.getGameType() == TTTGame.ONLINE)
                    game.checkReady(true);
                else game.beginGame();
                break;

            case TTTGame.REMATCH_COMMAND:
                game.setGameType(game.getGameType());
                break;
        }
    }

    //handles help button presses
    private void helpAction(String action) {

        switch (action) {
            case TTTHelp.EXIT_COMMAND:
                layout.show(this, MENU_PANEL);
                break;
        }
    }

    //cancels server / client connections if any are running
    private void cancelConnection() {
        if (onlineWorker != null) {
            onlineWorker.cancel(true);
            onlineWorker = null;
        }
    }

    public CardLayout getCardLayout() {
        return layout;
    }
}

abstract class OnlineWorker extends SwingWorker<Integer, Void> {

    //static constants
    public static final int
            SUCCESS = 0,
            FAIL = 1,
            TYPO = 2;

    //references to game and menu
    protected TTTDriver driver;
    protected TTTMenu menu;
    protected TTTGame game;

    public OnlineWorker(TTTDriver driverRef, TTTMenu menuRef, TTTGame gameRef) {
        driver = driverRef;
        menu = menuRef;
        game = gameRef;
    }

    @Override
    protected void done() {

        int status;

        try {
            status = get();
        } catch (Exception e) {
            status = FAIL;
        }

        switch (status) {
            case OnlineWorker.SUCCESS:
                game.setGameType(TTTGame.ONLINE);
                driver.getCardLayout().show(driver, "game"); //show game panel
                break;
            case OnlineWorker.FAIL:
                menu.setMessage("Connection failed");
                break;
            case OnlineWorker.TYPO:
                menu.setMessage("Invalid port / IP address");
                break;
        }
    }

    public void attemptConnection() {
        menu.setMessage("Waiting for connection...");
        game.closeSocket();
        this.execute();
    }
}

class HostWorker extends OnlineWorker {

    public HostWorker(TTTDriver driverRef, TTTMenu menuRef, TTTGame gameRef) {
        super(driverRef, menuRef, gameRef);
    }

    @Override
    protected Integer doInBackground() {

        //try to set up host
        try {
            if (!game.setupHost(menu.getPortField())) {
                return TYPO;
            }
            else return SUCCESS;
        }
        catch (IOException e) {
            return FAIL;
        }
    }
}

class JoinWorker extends OnlineWorker {

    public JoinWorker(TTTDriver driverRef, TTTMenu menuRef, TTTGame gameRef) {
        super(driverRef, menuRef, gameRef);
    }

    @Override
    protected Integer doInBackground() {

        try {
            if (!game.setupJoin(menu.getPortField(), menu.getIpField())) {
                return TYPO;
            }
            else return SUCCESS;
        }
        catch (IOException e) {
            return FAIL;
        }
    }
}

//startup class for TicTacToe
public class ThreeDimensionalTicTacToe {

    private final static String version = "Beta";

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {

        JFrame frame = new JFrame("3D Tic Tac Toe - " + version);
        frame.getContentPane().add(new TTTDriver());
        frame.setSize(new Dimension(600,650));
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}