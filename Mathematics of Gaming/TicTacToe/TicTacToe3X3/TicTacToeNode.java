// Statement of Authorship:
// I Drake Storm Hackett, 000783796 certify that this material is my original work. No other person's work has been used without due acknowledgement. I have not made my work available to anyone else.

import java.util.ArrayList;
import java.util.Arrays;

public class TicTacToeNode {
    static int nodes = 0; // just for curiosity
    char[][][] board;
    ArrayList<TicTacToeNode> children;
    int minimaxScore = -1; // 0 is meaningful, -1 won't be calculated in our results

    public TicTacToeNode() {
        nodes++;
        board = new char[3][3][3];
        children = new ArrayList<>();
    }
    
    @Override
    public String toString() {
        String boardStr;
        
        boardStr = "---\n";
        boardStr += boardToString(board[0]) + "\n";
        boardStr += boardToString(board[1]) + "\n";
        boardStr += boardToString(board[2]) + "\n";
        boardStr += "---\n";
        
        
        return boardStr;
    }

    public String boardToString(char [][] theBoard){
        String boardStr;
        boardStr = Arrays.toString( theBoard[0] ) + "\n";
        boardStr += Arrays.toString( theBoard[1] ) + "\n";
        boardStr += Arrays.toString( theBoard[2] ) + "\n";
        return boardStr;
    }
    
    public boolean isGameWon( ) {
        return isGameWon( 'X' ) || isGameWon( 'O' );
    }
    
    public boolean isGameWon( char symbol ) {
        boolean won = false;

        //for each board
        for (int j = 0; j < 3; j++) {
            // 2 diagonals per board
            won = won || (board[j][1][1]==symbol) &&
                    (( board[j][0][0]==board[j][1][1] && board[j][1][1]==board[j][2][2] ) ||
                            ( board[j][0][2]==board[j][1][1] && board[j][1][1]==board[j][2][0]) );

            for ( int i = 0; i < 3; i++ ) {
                // 3 rows
                won = won || (board[j][i][0]==symbol && board[j][i][0] == board[j][i][1] && board[j][i][1] == board[j][i][2]);
                // 3 columns
                won = won || (board[j][0][i]==symbol && board[j][0][i] == board[j][1][i] && board[j][1][i] == board[j][2][i]);
            }
        }

        //Across all boards
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                //Three in a row vertically across boards
                won = won || (board[0][i][j] == symbol) && (board[1][i][j] == board[0][i][j]) && (board[2][i][j] == board[0][i][j]);
            }
        }

        //Diagonals across all boards
        won = won || (board[1][0][1] == symbol) &&
                ((board[0][0][0] == board[1][0][1] && board[2][0][2] == board[1][0][1]) ||
                        (board[0][0][2] == board[1][0][1] && board[2][0][1] == board[1][0][1])); //When middle is in first row, second column of second board
        won = won || (board[1][1][2] == symbol) &&
                ((board[0][0][2] == board[1][1][2] && board[2][2][2] == board[1][1][2]) ||
                        (board[0][2][2] == board[1][1][2] && board[2][0][2] == board[1][1][2])); //When middle is in second row, third column of second board
        won = won || (board[1][2][1] == symbol) &&
                ((board[0][2][0] == board[1][2][1] && board[2][2][2] == board[1][2][1]) ||
                        (board[0][2][2] == board[1][2][1] && board[2][2][0] == board[1][2][1])); //When middle is in third row, second column of second board
        won = won || (board[1][1][0] == symbol) &&
                ((board[0][0][0] == board[1][1][0] && board[2][2][0] == board[1][1][0]) ||
                        (board[0][2][2] == board[1][1][0] && board[2][0][0] == board[1][1][0])); //When middle is in second row, first column of second board

        won = won || (board[1][1][1] == symbol) &&
                ((board[0][0][0] == board[1][1][1] && board[2][2][2] == board[1][1][1]) ||
                        (board[0][2][2] == board[1][1][1] && board[2][0][0] == board[1][1][1]) ||
                        (board[0][0][1] == board[1][1][1] && board[2][2][1] == board[1][1][1]) ||
                        (board[0][2][1] == board[1][1][1] && board[2][0][1] == board[1][1][1]) ||
                        (board[0][0][2] == board[1][1][1] && board[2][2][0] == board[1][1][1]) ||
                        (board[0][2][0] == board[1][1][1] && board[2][0][2] == board[1][1][1]) ||
                        (board[0][1][0] == board[1][1][1] && board[2][1][2] == board[1][1][1]) ||
                        (board[0][2][2] == board[1][1][1] && board[2][1][0] == board[1][1][1])); //When middle is in second row, second column of second board

        return won;
    }

    public boolean isBoardFull(int boardNum){
        boolean full = true;
        for(char[] row : board[boardNum]){
            for (char pos: row){
                full &= (pos != Character.MIN_VALUE);
            }
        }
        return full;
    }
    
    public boolean isFull() {
        boolean full = true;
        for (char[][] theBoard : board) {
            for ( char[] row : theBoard )
                for ( char pos : row )
                    full &= ( pos != Character.MIN_VALUE );
        }
        
        return full;
    }
    
    public char[][][] copyBoard() {
        // Java's .clone() does not handle 2D arrays
        // by copying values, rather we get a clone of refs
        char[][][] boardCopy = new char[3][3][3];

        for (int i = 0; i < 3; i++) {
            for ( int j = 0; j < 3; j ++ )
                System.arraycopy(board[i][j], 0, boardCopy[i][j], 0, 3);
        }
        
        return boardCopy;
    }

    public int depth () {
        int depth = 0;
        for (char[][] b : board) {
            for( char[] r : b )
                for( char c : r )
                    if ( c != Character.MIN_VALUE ) depth++;
        }

        return depth;
    }

    public boolean equals(char[][][] newboard){
        boolean isSame = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if(board[i][j][k] != newboard[i][j][k]){
                        isSame = false;
                    }
                }
            }
        }
        return isSame;
    }

}
