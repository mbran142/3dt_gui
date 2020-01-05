import javax.swing.*;
import java.util.Random;

//class that holds the AI for the game
public class TTTComputer {

    private static int EMPTY = 0,
                       HUMAN = 1,
                       COMPUTER = 2,
                       CSIZE = 3,
                       SIZE = 4,
                       NOT_DONE = 0;

    //reference to the game's board

    Random rand;
    int[][][] board;

    //constructor takes reference to board
    public TTTComputer(int[][][] boardRef) {
        board = boardRef;
        rand = new Random();
    }

    //get the AI's move; returns a string with the form ### (ex: "012")
    public int[] move() {

        //variables for ai algorithm
        final int MAX_DEPTH = 5,
                  CONSTANT_AMNT = 8;

        int depth = 0,
            status_prediction,
            temp;

        //set up moves info array [6x8]
        //moves[i][0] is used for the current iteration in the 'node'
        //moves[i][1-3] are the i j k board placements
        //moves[i][4] is the minimax score
        //moves[i][5] is the accociated move with the minimax score
        //moves[i][6] always starts at 0
        //moves[i][7] is the current amount of moves made

        int moves[][] = new int[MAX_DEPTH + 1][];
        for (int i = 0; i < MAX_DEPTH + 1; i++) {
            moves[i] = new int[CONSTANT_AMNT];
            moves[i][4] = i % 2 == 1 ? 0x80000000 : 0x7FFFFFFF;
        }

        boolean down = true;

        while (true) {

            if (down) {
                depth++;
                moves[depth][0] = rand.nextInt(64) - 1;
                //moves[depth][0] = -1;
                moves[depth][7] = -1;
            }

            do {

                moves[depth][0] = moves[depth][0] == ((SIZE * SIZE * SIZE) - 1) ? 0 : moves[depth][0] + 1;
                moves[depth][7]++;
                down = !(moves[depth][7] == SIZE * SIZE * SIZE);

                if (down) {
                    moves[depth][1] = moves[depth][0] / (SIZE * SIZE);
                    moves[depth][2] = (moves[depth][0] - (moves[depth][1] * SIZE * SIZE)) / SIZE;
                    moves[depth][3] = moves[depth][0] - (moves[depth][1] * SIZE * SIZE) - (moves[depth][2] * SIZE);
                }

            } while (down && board[moves[depth][1]][moves[depth][2]][moves[depth][3]] != EMPTY);

            if (!down) {

                depth--;

                if (((depth & 0x1) == 0 && moves[depth][4] > moves[depth + 1][4]) ||
                        ((depth & 0x1) == 1 && moves[depth][4] < moves[depth + 1][4])) {
                    moves[depth][4] = moves[depth + 1][4];
                    moves[depth][5] = moves[depth + 1][5];
                }

                moves[depth + 1][4] = ((depth + 1) & 0x1) == 1 ? 0x80000000 : 0x7FFFFFFF;

                if (depth == 0) break;
                board[moves[depth][1]][moves[depth][2]][moves[depth][3]] = 0;
            }

            else {

                board[moves[depth][1]][moves[depth][2]][moves[depth][3]] = (depth & 0x1) == 1 ? COMPUTER : HUMAN;

                status_prediction = TTTGame.checkGameCondition(board);

                //win / loss
                if (status_prediction != NOT_DONE) {

                    board[moves[depth][1]][moves[depth][2]][moves[depth][3]] = 0;
                    depth--;

                    if (depth == 0) {
                        moves[depth][5] = moves[depth + 1][0];
                        break;
                    }

                    board[moves[depth][1]][moves[depth][2]][moves[depth][3]] = 0;
                    depth--;

                    if (depth == 0) {
                        moves[depth][5] = moves[depth + 2][0];
                        break;
                    }

                    board[moves[depth][1]][moves[depth][2]][moves[depth][3]] = 0;
                    moves[depth][4] = (depth & 0x1) == 1 ? 1000000 : -1000000;
                    moves[depth][5] = moves[depth][0];
                    down = false;
                }

                else {

                    temp = this.minimax(board);

                    if (depth == MAX_DEPTH) {

                        if (((depth & 0x1) == 1 && temp > moves[depth][4]) ||
                                ((depth & 0x1) == 0 && temp < moves[depth][4])) {
                            moves[depth][4] = temp;
                            moves[depth][5] = moves[depth][0];
                        }

                        board[moves[depth][1]][moves[depth][2]][moves[depth][3]] = 0;
                        down = false;
                    }

                    else {
                        down = (((depth & 0x1) == 1 && temp > moves[depth][4]) || ((depth & 0x1) == 0 && temp < moves[depth][4]));
                        board[moves[depth][1]][moves[depth][2]][moves[depth][3]] = 0;
                    }
                }
            }
        }
        //end while

        moves[depth][1] = moves[depth][5] / (SIZE * SIZE);
        moves[depth][2] = (moves[depth][5] - (moves[depth][1] * SIZE * SIZE)) / SIZE;
        moves[depth][3] = moves[depth][5] - (moves[depth][1] * SIZE * SIZE) - (moves[depth][2] * SIZE);

        return new int[] { moves[0][1], moves[0][2], moves[0][3] };
    }

    private int minimax(int board[][][]) {

        final int MULTIPLIER = 3;
        final int P_CONST = -3;
        final int C_CONST = 2;
        int psize, temp, line, minimax, c3, p3, i, j, k;
        c3 = p3 = minimax = 0;

        //all 2a 1n
        psize = 3;
        for (i = 0; i < SIZE; i++) {
            for (j = 0; j < SIZE; j++) {

                for (int p = 0; p < psize; p++) {

                    line = 0;

                    for (k = 0; k < SIZE; k++) {

                        switch(p) {
                            case 0 : temp = board[i][j][k]; break;
                            case 1 : temp = board[i][k][j]; break;
                            default: temp = board[k][i][j];
                        }

                        if (line != 0) {
                            if ((line > 0 && temp == COMPUTER) || (line < 0 && temp == HUMAN))
                                line *= MULTIPLIER;

                            else if (temp != 0) {
                                line = 0;
                                break;
                            }
                        }

                        else if (temp != 0)
                            line = temp == COMPUTER ? C_CONST : P_CONST;
                    }

                    minimax += line;

                    if (line == MULTIPLIER * MULTIPLIER * C_CONST) c3++;
                    else if (line == MULTIPLIER * MULTIPLIER * P_CONST) p3++;
                }
            }
        }

        //all 1a 2n
        psize = 6;
        for (i = 0; i < SIZE; i++) {

            for (int p = 0; p < psize; p++) {

                line = 0;

                for (j = 0; j < SIZE; j++) {

                    switch (p) {
                        case 0 : temp = board[i][j][j]; break;
                        case 1 : temp = board[j][i][j]; break;
                        case 2 : temp = board[j][j][i]; break;
                        case 3 : temp = board[i][j][CSIZE - j]; break;
                        case 4 : temp = board[j][i][CSIZE - j]; break;
                        default: temp = board[j][CSIZE - j][i];
                    }

                    if (line != 0) {
                        if ((line > 0 && temp == COMPUTER) || (line < 0 && temp == HUMAN))
                            line *= MULTIPLIER;

                        else if (temp != 0) {
                            line = 0;
                            break;
                        }
                    }

                    else if (temp != 0)
                        line = temp == COMPUTER ? C_CONST : P_CONST;
                }

                minimax += line;

                if (line == MULTIPLIER * MULTIPLIER * C_CONST) c3++;
                else if (line == MULTIPLIER * MULTIPLIER * P_CONST) p3++;
            }
        }

        //all 3n
        psize = 4;
        for (int p = 0; p < psize; p++) {

            line = 0;

            for (i = 0; i < SIZE; i++) {

                switch (p) {
                    case 0 : temp = board[i][i][i]; break;
                    case 1 : temp = board[i][i][CSIZE - i]; break;
                    case 2 : temp = board[i][CSIZE - i][i]; break;
                    default: temp = board[CSIZE - i][i][i];
                }

                if (line != 0) {
                    if ((line > 0 && temp == COMPUTER) || (line < 0 && temp == HUMAN))
                        line *= MULTIPLIER;

                    else if (temp != 0) {
                        line = 0;
                        break;
                    }
                }

                else if (temp != 0)
                    line = temp == COMPUTER ? C_CONST : P_CONST;
            }

            minimax += line;

            if (line == MULTIPLIER * MULTIPLIER * C_CONST) c3++;
            else if (line == MULTIPLIER * MULTIPLIER * P_CONST) p3++;
        }

        if (p3 == 0 && c3 == 2) return 5000;
        else if (c3 == 0 && p3 == 2) return -5000;

        return minimax;
    }

    public int[] testMove() {

        int i = 0;
        while (board[0][0][i] != EMPTY)
            i++;

        return new int[] { 0, 0, i };
    }
}
