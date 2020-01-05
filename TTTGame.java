import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.StringTokenizer;

//the game screen for 3D TicTacToe
public class TTTGame extends JPanel implements ActionListener {

    //constants
    private final static int SIZE = 4,
            EMPTY = 0,
            PLAYER_1 = 1, //O's
            PLAYER_2 = 2, //X's
            HUMAN = PLAYER_1,
            COMPUTER = PLAYER_2,
            NOT_DONE = 0,
            P1_WINS = 1,
            P2_WINS = 2,
            DRAW = 3;

    public final static int LOCAL = 10,
            ONLINE = 11,
            CPU = 12,
            HOST = 0,
            JOIN = 1;

    private final static Color BACKGROUND_BG = TTTDriver.BG;

    private final static String LABEL_HTML_START = "<html><font size='6' color='#906060'>",
            LABEL_HTML_END = "</font></html>";

    public final static String GAME_IDENTIFIER = "game",
            EXIT_COMMAND = "exit",
            START_COMMAND = "start",
            REMATCH_COMMAND = "rematch";

    private final static Color START_FG = new Color(0x005000),
            START_BG = new Color(0x107010);

    //instance
    private volatile boolean myTurn;
    private int board[][][], prevButton[], turn, gameType;
    private JLabel turnLabel, messageLabel;
    private JButton startButton, rematchButton;
    private JPanel startPanel;
    private TTTBoard[] boardDisplay;
    private String player1, player2;
    private TTTSocket socket; //for online play
    private TTTComputer ai; //for playing against computer
    private volatile int ready;

    //global int that determines current player
    public static int currentPlayer;

    //create game panel with input actionlistener
    public TTTGame(TTTDriver action) {

        this.add(createGamePanel(action));
        this.setBackground(BACKGROUND_BG);

        //set up board 3d array
        board = new int[SIZE][][];
        for (int i = 0; i < SIZE; i++) {
            board[i] = new int[SIZE][];
            for (int j = 0; j < SIZE; j++)
                board[i][j] = new int[SIZE];
        }

        prevButton = new int[3];
    }

    //create the panel with the game display
    private JPanel createGamePanel(TTTDriver action) {

        JPanel infoPanel, boardPanel, gamePanel;
        BorderlessButton exitButton;
        Dimension size;

        //setting up info panel components
        turnLabel = new JLabel();
        turnLabel.setFont(new Font(turnLabel.getFont().getName(), Font.BOLD, 18));
        messageLabel = new JLabel();
        messageLabel.setFont(new Font(messageLabel.getFont().getName(), Font.ITALIC, 16));
        size = new Dimension(200, 55);
        messageLabel.setMinimumSize(size);
        messageLabel.setPreferredSize(size);
        messageLabel.setMaximumSize(size);
        turnLabel.setMinimumSize(size);
        turnLabel.setPreferredSize(size);
        turnLabel.setMaximumSize(size);
        startButton = new JButton();
        startButton.setText("<html><font size='6' color'#107010'> Start </font></html>");
        startButton.setActionCommand(GAME_IDENTIFIER + "_" + START_COMMAND);
        startButton.setForeground(START_FG);
        startButton.setBackground(START_BG);
        startButton.addActionListener(action);
        startButton.setPreferredSize(new Dimension(0, 80));
        rematchButton = new JButton();
        rematchButton.setText("<html><font size='5' color'#107010'> Rematch </font></html>");
        rematchButton.setActionCommand(GAME_IDENTIFIER + "_" + REMATCH_COMMAND);
        rematchButton.setForeground(START_FG);
        rematchButton.setBackground(START_BG);
        rematchButton.addActionListener(action);
        rematchButton.setPreferredSize(new Dimension(0, 80));
        startPanel = new JPanel();
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.setPreferredSize(new Dimension(80, 300));
        startPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        startPanel.setOpaque(false);
        startPanel.add(Box.createRigidArea(new Dimension(0, 180)));
        exitButton = new BorderlessButton("Exit", GAME_IDENTIFIER + "_" + EXIT_COMMAND);
        exitButton.addActionListener(action);

        //putting together info panel
        infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(turnLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(messageLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(startPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(exitButton);

        //setting up components and putting together board panel
        boardPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 70, 10, 10));
        boardPanel.setOpaque(false);
        boardDisplay = new TTTBoard[SIZE];
        for (int i = 0; i < SIZE; i++) {
            boardDisplay[i] = new TTTBoard(i, this);
            boardPanel.add(boardDisplay[i]);
        }

        //putting everything together
        //gamePanel = new JPanel(new GridLayout(1,2,20,0));
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.X_AXIS));
        gamePanel.setOpaque(false);
        gamePanel.add(infoPanel);
        gamePanel.add(boardPanel);

        return gamePanel;
    }

    //sets type of game then sets up board
    public void setGameType(int type) {

        //this clients turn unless it's an online game and you're not the host
        gameType = type;
        myTurn = gameType != ONLINE || socket.isHost();

        switch (gameType) {

            case LOCAL:
                this.cleanAndPrepareBoard("Player O", "Player X");
                break;

            case CPU:
                ai = new TTTComputer(board);
                this.cleanAndPrepareBoard("Human", "Computer");
                break;

            case ONLINE: {
                ready = 0;
                requestOpponentReady();
                String s1 = "You", s2 = "Opponent";
                if (!socket.isHost()) {
                    String temp = s1;
                    s1 = s2;
                    s2 = temp;
                }
                this.cleanAndPrepareBoard(s1, s2);
                break;
            }
        }
    }

    //clean up the board and set each player name
    private void cleanAndPrepareBoard(String p1, String p2) {

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                for (int k = 0; k < SIZE; k++) {
                    board[i][j][k] = 0;
                    boardDisplay[i].getButton(j, k).setStatus(EMPTY);
                    boardDisplay[i].getButton(j, k).setHighlight(false);
                }

        //player 1 goes first
        currentPlayer = PLAYER_1;

        turn = 0;
        player1 = p1;
        player2 = p2;

        //set labels
        if (gameType == ONLINE && myTurn)
            messageLabel.setText(LABEL_HTML_START + "You go first" + LABEL_HTML_END);
        else
            messageLabel.setText(LABEL_HTML_START + "Opponent goes first" + LABEL_HTML_END);

        if (gameType != ONLINE)
            turnLabel.setText(LABEL_HTML_START + "Prepare to start" + LABEL_HTML_END);
        else
            turnLabel.setText(LABEL_HTML_START + (myTurn ? "You are O\'s" : "You are X\'s") + LABEL_HTML_END);

        startPanel.remove(rematchButton);
        startPanel.add(startButton);
        this.movesEnabled(false);

        this.revalidate();
        this.repaint();
    }

    //begins the game
    public void beginGame() {

        startPanel.remove(startButton);
        movesEnabled(myTurn);
        updateLabels(PLAYER_1);

        //for online joining
        if (gameType == ONLINE && !myTurn)
            getOpponentWorker().execute();
    }

    //each move made
    private void makeMove(int i, int j, int k) {

        //decide what to do based on game type
        switch (gameType) {

            case LOCAL: {

                board[i][j][k] = currentPlayer;
                updateBoardDisplay(i, j, k, currentPlayer);
                swapPlayer();
                updateLabels(currentPlayer);

                //game finished
                int gameCondition = checkGameCondition(board);
                if (gameCondition != NOT_DONE)
                    displayGameResults(gameCondition);

                else {
                    movesEnabled(true);
                    myTurn = true;
                }
            }
            break;

            case CPU: {

                board[i][j][k] = HUMAN;
                updateBoardDisplay(i, j, k, HUMAN);
                updateLabels(COMPUTER);
                int gameCondition = checkGameCondition(board);

                //player win
                if (gameCondition != NOT_DONE) {
                    displayGameResults(gameCondition);
                    break;
                }

                getAiWorker().execute();
                this.revalidate();
                this.repaint();
                break;
            }

            case ONLINE: {

                board[i][j][k] = currentPlayer;
                updateBoardDisplay(i, j, k, currentPlayer);
                //maybe this needs a swingworker ?
                socket.sendMove(Integer.toString(i) + Integer.toString(j) + Integer.toString(k));
                swapPlayer();

                int gameCondition = checkGameCondition(board);

                //this client wins
                if (gameCondition != NOT_DONE)
                    displayGameResults(gameCondition);

                    //get opponents move
                else {
                    myTurn = false;
                    updateLabels(currentPlayer);
                    movesEnabled(false);
                    getOpponentWorker().execute();
                    this.revalidate();
                    this.repaint();
                }
                break;
            }
        }
    }

    //make move and update/redraw labels
    private void updateBoardDisplay(int i, int j, int k, int player) {

        //make and highlight move
        boardDisplay[i].getButton(j, k).setStatus(player);
        boardDisplay[i].getButton(j, k).setHighlight(true);

        //unhighlight previous move
        if (turn != 1)
            boardDisplay[prevButton[0]].getButton(prevButton[1], prevButton[2]).setHighlight(false);
        prevButton[0] = i; prevButton[1] = j; prevButton[2] = k;

        //repaint board
        this.revalidate();
        this.repaint();
    }

    private void updateLabels(int player) {

        turn++;

        //update labels
        String currentPlayerName = player == PLAYER_1 ? player1 : player2;
        turnLabel.setText(LABEL_HTML_START + "Turn: " + turn + LABEL_HTML_END);
        if (currentPlayerName.equals("You"))
            messageLabel.setText(LABEL_HTML_START + "Your turn" + LABEL_HTML_END);
        else messageLabel.setText(LABEL_HTML_START + currentPlayerName + "\'s turn" + LABEL_HTML_END);
    }

    //swaps players
    private void swapPlayer() {
        currentPlayer = currentPlayer == PLAYER_1 ? PLAYER_2 : PLAYER_1;
    }

    //check to see if there's a winner
    public static int checkGameCondition(int[][][] board) {

        final int CSIZE = SIZE - 1;
        int[] prod = new int[6];
        int psize, i, j, k;

        //a => static values 0-3, n => circle though 0-3

        //all 2a 1n
        psize = 3;
        for (i = 0; i < SIZE; i++) {
            for (j = 0; j < SIZE; j++) {

                for (int p = 0; p < psize; p++)
                    prod[p] = 1;

                for (k = 0; k < SIZE; k++) {
                    prod[0] *= board[i][j][k];
                    prod[1] *= board[i][k][j];
                    prod[2] *= board[k][i][j];
                }

                for (int p = 0; p < psize; p++) {
                    if (prod[p] == 1) return P1_WINS;
                    if (prod[p] == 16) return P2_WINS;
                }
            }
        }

        //all 1a 2n
        psize = 6;
        for (i = 0; i < SIZE; i++) {

            for (int p = 0; p < psize; p++)
                prod[p] = 1;

            for (j = 0; j < SIZE; j++) {
                prod[0] *= board[i][j][j];
                prod[1] *= board[j][i][j];
                prod[2] *= board[j][j][i];
                prod[3] *= board[i][j][CSIZE - j];
                prod[4] *= board[j][i][CSIZE - j];
                prod[5] *= board[j][CSIZE - j][i];
            }

            for (int p = 0; p < psize; p++) {
                if (prod[p] == 1) return P1_WINS;
                if (prod[p] == 16) return P2_WINS;
            }
        }

        //all 3n
        psize = 4;
        {
            for (int p = 0; p < psize; p++)
                prod[p] = 1;

            for (i = 0; i < SIZE; i++) {
                prod[0] *= board[i][i][i];
                prod[1] *= board[i][i][CSIZE - i];
                prod[2] *= board[i][CSIZE - i][i];
                prod[3] *= board[CSIZE - i][i][i];
            }

            for (int p = 0; p < psize; p++) {
                if (prod[p] == 1) return P1_WINS;
                if (prod[p] == 16) return P2_WINS;
            }
        }

        //check full board
        for (i = 0; i < SIZE; i++)
            for (j = 0; j < SIZE; j++)
                for (k = 0; k < SIZE; k++)
                    if (board[i][j][k] == 0)
                        return NOT_DONE;

        return DRAW;
    }

    //when game is over, display the results and ask for a rematch
    private void displayGameResults(int condition) {

        movesEnabled(false);
        ai = null;

        turnLabel.setText(LABEL_HTML_START + "Finished!" + LABEL_HTML_END);

        if (condition == DRAW)
            messageLabel.setText(LABEL_HTML_START + "Draw!" + LABEL_HTML_END);

        else {
            String winner = condition == PLAYER_1 ? player1 : player2;
            messageLabel.setText(LABEL_HTML_START + winner + " wins!" + LABEL_HTML_END);
            this.highlightWin(condition);
        }

        this.revalidate();
        this.repaint();

        startPanel.add(rematchButton);
    }

    //highlights the winning combination
    private void highlightWin(int winner) {

        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                for (int k = 0; k < SIZE; k++)
                    boardDisplay[i].getButton(j, k).setHighlight(false);

        final int CSIZE = SIZE - 1;
        int psize, temp, i, j, k;

        //all 2a 1n
        psize = 3;
        for (i = 0; i < SIZE; i++) {
            for (j = 0; j < SIZE; j++) {
                for (int p = 0; p < psize; p++) {
                    for (k = 0; k < SIZE; k++) {

                        switch (p) {
                            case 0:
                                temp = board[i][j][k];
                                break;
                            case 1:
                                temp = board[i][k][j];
                                break;
                            default:
                                temp = board[k][i][j];
                        }

                        if (temp != winner) break;
                        if (k == SIZE - 1) {
                            for (k = 0; k < SIZE; k++) {

                                switch (p) {
                                    case 0:
                                        boardDisplay[i].getButton(j, k).setHighlight(true);
                                        break;
                                    case 1:
                                        boardDisplay[i].getButton(k, j).setHighlight(true);
                                        break;
                                    default:
                                        boardDisplay[k].getButton(i, j).setHighlight(true);
                                }
                            }

                            return;
                        }
                    }
                }
            }
        }

        //all 1a 2n
        psize = 6;
        for (i = 0; i < SIZE; i++) {
            for (int p = 0; p < psize; p++) {
                for (j = 0; j < SIZE; j++) {

                    switch (p) {
                        case 0:
                            temp = board[i][j][j];
                            break;
                        case 1:
                            temp = board[j][i][j];
                            break;
                        case 2:
                            temp = board[j][j][i];
                            break;
                        case 3:
                            temp = board[i][j][CSIZE - j];
                            break;
                        case 4:
                            temp = board[j][i][CSIZE - j];
                            break;
                        default:
                            temp = board[j][CSIZE - j][i];
                    }


                    if (temp != winner) break;
                    if (j == SIZE - 1) {
                        for (j = 0; j < SIZE; j++) {

                            switch (p) {
                                case 0:
                                    boardDisplay[i].getButton(j, j).setHighlight(true);
                                    break;
                                case 1:
                                    boardDisplay[j].getButton(i, j).setHighlight(true);
                                    break;
                                case 2:
                                    boardDisplay[j].getButton(j, i).setHighlight(true);
                                    break;
                                case 3:
                                    boardDisplay[i].getButton(j, CSIZE - j).setHighlight(true);
                                    break;
                                case 4:
                                    boardDisplay[j].getButton(i, CSIZE - j).setHighlight(true);
                                    break;
                                default:
                                    boardDisplay[j].getButton(CSIZE - j, i).setHighlight(true);
                            }
                        }

                        return;
                    }
                }
            }
        }

        //all 3n
        psize = 4;
        for (int p = 0; p < psize; p++) {
            for (i = 0; i < SIZE; i++) {

                switch (p) {
                    case 0:
                        temp = board[i][i][i];
                        break;
                    case 1:
                        temp = board[i][i][CSIZE - i];
                        break;
                    case 2:
                        temp = board[i][CSIZE - i][i];
                        break;
                    default:
                        temp = board[CSIZE - i][i][i];
                }

                if (temp != winner) break;
                if (i == SIZE - 1) {
                    for (i = 0; i < SIZE; i++) {

                        switch (p) {
                            case 0:
                                boardDisplay[i].getButton(i, i).setHighlight(true);
                                break;
                            case 1:
                                boardDisplay[i].getButton(i, CSIZE - i).setHighlight(true);
                                break;
                            case 2:
                                boardDisplay[i].getButton(CSIZE - i, i).setHighlight(true);
                                break;
                            default:
                                boardDisplay[CSIZE - i].getButton(i, i).setHighlight(true);
                        }
                    }

                    return;
                }
            }
        }
    }

    //goes through every button and enables moves
    private void movesEnabled(boolean b) {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE * SIZE; j++)
                boardDisplay[i].getButton(j / SIZE, j % SIZE).setActive(b);
    }

    //returns the type of game (local, online, or cpu)
    public int getGameType() {
        return gameType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //get location
        int i, j, k;
        StringTokenizer st = new StringTokenizer(e.getActionCommand(), "_");
        i = Integer.parseInt(st.nextToken());
        j = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());

        //make sure move hasn't been made already
        if (board[i][j][k] == EMPTY) {

            myTurn = false;
            SwingWorker<Void, Void> buttonTask = new SwingWorker<>() {

                @Override
                public Void doInBackground() {
                    //immediately disable buttons
                    movesEnabled(false);
                    return null;
                }

                @Override
                public void done() {
                    //enable moves if its your turn
                    movesEnabled(myTurn);
                }
            };

            buttonTask.execute();

            //make the move
            makeMove(i, j, k);
        }
    }

    //tries to set up a host
    public boolean setupHost(String portString) throws IOException {

        //try to set up a server to host the game.
        try {

            //check for correct port input
            int port = Integer.parseInt(portString);
            if (port < 0 || port > 0xFFFF) //0xFFFF is max port
                throw new NumberFormatException();

            closeSocket();
            socket = new TTTServer(port);

            return true;

        } catch (NumberFormatException e) {
            return false; //input port is formatted incorrectly
        }
    }

    //tries to join a host
    public boolean setupJoin(String portString, String ip) throws IOException {

        //try to join a server
        try {

            //check for correct port input
            int port = Integer.parseInt(portString);
            if (port < 0 || port > 0xFFFF) //0xFFFF is max port
                throw new NumberFormatException();

            closeSocket();
            socket = new TTTClient(port, ip);

            return true;

        } catch (NumberFormatException e) {
            return false; //input port is formatted incorrectly
        }
    }

    //closes socket if open
    public void closeSocket() {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    //makes sure both online opponents are ready
    public synchronized void checkReady(boolean thisClient) {

        ready++;

        if (ready == 1 && thisClient) {
            startButton.setText("<html><font size='5' color'#107010'> Waiting... </font></html>");
            startButton.setEnabled(false);
        }

        else if (ready == 2) {
            startButton.setText("<html><font size='6' color'#107010'> Start </font></html>");
            startButton.setEnabled(true);
            this.beginGame();
        }

        if (thisClient)
            socket.sendMove("ready");
    }

    private void requestOpponentReady() {

        SwingWorker<Integer, Void> readyWorker = new SwingWorker<>() {

            @Override
            protected Integer doInBackground() {
                return socket.requestMove();
            }

            @Override
            protected void done() {
                try {
                    if (get() == TTTSocket.DISCONNECT)
                        cancelGame();
                    else checkReady(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        readyWorker.execute();
    }

    //cancel game if opponent disconnects
    private void cancelGame() {
        startPanel.remove(startButton);
        movesEnabled(false);
        turnLabel.setText("");
        messageLabel.setText(LABEL_HTML_START + "Click exit to return to main menu" + LABEL_HTML_END);
        turnLabel.setText(LABEL_HTML_START + "Your opponent has disconnected" + LABEL_HTML_END);
    }

    private SwingWorker<int[], Void> getAiWorker() {

        return new SwingWorker<>() {

            @Override
            protected int[] doInBackground() {
                //process AI move
                return ai.move();
            }

            @Override
            protected void done() {
                try {

                    //make computer move
                    int[] move = get();
                    board[move[0]][move[1]][move[2]] = COMPUTER;
                    updateBoardDisplay(move[0], move[1], move[2], COMPUTER);

                    int gameCondition = checkGameCondition(board);

                    //check computer win
                    if (gameCondition != NOT_DONE)
                        displayGameResults(gameCondition);

                        //continue game
                    else {
                        updateLabels(HUMAN);
                        movesEnabled(true);
                        myTurn = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private SwingWorker<int[], Void> getOpponentWorker() {

        return new SwingWorker<>() {

            @Override
            protected int[] doInBackground() {

                int move = socket.requestMove();

                if (move == TTTSocket.DISCONNECT)
                    return null;

                return new int[]{ move / 100, (move / 10) % 10, move % 10};
            }

            @Override
            protected void done() {
                try {

                    int[] move = get();

                    if (move == null)
                        cancelGame();

                    else {

                        board[move[0]][move[1]][move[2]] = currentPlayer;
                        updateBoardDisplay(move[0], move[1], move[2], currentPlayer);

                        int gameCondition = checkGameCondition(board);

                        //opponent wins
                        if (gameCondition != NOT_DONE)
                            displayGameResults(gameCondition);

                            //continue game
                        else {
                            swapPlayer();
                            updateLabels(currentPlayer);
                            myTurn = true;
                            movesEnabled(true);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}

//individual layer of the 3D board
class TTTBoard extends JPanel {

    //constants
    private final int SIZE = 4;
    private BoardButton[] button;

    //create one grid
    public TTTBoard(int index, TTTGame action) {

        this.setLayout(new GridLayout(SIZE, SIZE));
        this.setPreferredSize(new Dimension(140,140));
        button = new BoardButton[SIZE * SIZE];

        for (int i = 0; i < SIZE * SIZE; i++) {
            button[i] = new BoardButton(index + "_" + i / 4 + "_" + i % 4, action);
            this.add(button[i]);
        }
    }

    //gets button at location (j, k)
    public BoardButton getButton(int j, int k) {
        return button[j * 4 + k];
    }
}

//individual space for the tic tac toe board
class BoardButton extends JButton {

    //static color constants
    private final static Color BG = TTTDriver.BG,
                               DARK_X = new Color(0x902010),
                               DARK_O = new Color(0x204090);

    //static status constants
    private final static int EMPTY = 0,
                             PLAYER_1 = 1, //O's
                             PLAYER_2 = 2; //X's

    //instance
    private int status;
    private boolean active, highlight;

    //default constructor (for help tutorial)
    public BoardButton() {

        super.setContentAreaFilled(false);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.setFocusPainted(false);

        status = EMPTY;
        active = false;
        highlight = false;
    }

    //constructor takes action command and actionlistener
    public BoardButton(String actionCommand, TTTGame action) {

        super.setContentAreaFilled(false);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.setFocusPainted(false);
        this.setActionCommand(actionCommand);
        this.addActionListener(action);

        status = EMPTY;
        active = false;
        highlight = false;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setActive(boolean b) {
        this.setEnabled(b);
        active = b;
    }

    public void setHighlight(boolean b) {
        highlight = b;
    }

    //overriden to set custom mouseover and mouseclick color fills
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(BG);
        g2.fillRect(0, 0, getWidth(), getHeight());

        //determine color based on mouseover and permanently selected
        int drawStatus = this.status;
        if (drawStatus == EMPTY) {
            if (active && (this.getModel().isRollover() || this.getModel().isPressed()))
                drawStatus = TTTGame.currentPlayer;
            g2.setColor(drawStatus == PLAYER_1 ? DARK_O : DARK_X);
        }
        else {
            Color tempColor = status == PLAYER_1 ? DARK_O : DARK_X;
            g2.setColor(tempColor.brighter());
        }

        if (highlight)
            g2.setColor(g2.getColor().brighter().brighter());

        switch (drawStatus) {

            case EMPTY: //draw nothing
                break;

            case PLAYER_1:
                g2.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
                g2.setColor(BG);
                g2.fillOval(8, 8, getWidth() - 16, getHeight() - 16);
                break;

            case PLAYER_2:
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(6,6,getWidth() - 6, getHeight() - 6);
                g2.drawLine(6,getHeight() - 6,getWidth() - 6,6);
                break;
        }

        super.paintComponent(g);
    }

    //overrided to ensure the default colors are not filled
    @Override
    public void setContentAreaFilled(boolean b) { }
}