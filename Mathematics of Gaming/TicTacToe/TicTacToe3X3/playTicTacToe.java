// Statement of Authorship:
// I Drake Storm Hackett, 000783796 certify that this material is my original work. No other person's work has been used without due acknowledgement. I have not made my work available to anyone else.

import java.util.*;

public class playTicTacToe {

    static boolean DEBUG = false; // not final to allow local debug toggles, ie use debug in 1 method only
    public static ArrayList<TicTacToeNode> allTheNodes = new ArrayList<>();

    /**
     * A function which will rotate each board
     * @param first the original 3 boards to be rotated
     * @return a new version of the game with all 3 boards rotated
     */
    public static char[][][] rotateBoard(char[][][] first){
        char[][][] second = new char[3][3][3];
        for (int l = 0; l < 3; l++) {
            for (int m = 0; m < 3; m++) {
                for (int n = 0; n < 3; n++) {
                    second[l][m][n] = first[l][3 - n - 1][m];
                }
            }
        }
        return second;
    }

    /**
     * This function will handle the state space reductions
     * @param root = the node we are currently working on
     * @param symbol = who's turn it would be
     */
    public static void buildTheBoards(TicTacToeNode root, char symbol){
        DEBUG = false;

        for ( int i = 0; i < 3; i++ ) {
            if(!root.isBoardFull(i)){
                for ( int j = 0; j < 3; j++ ) {
                    for (int k = 0; k < 3; k++) {
                        char[][][] newBoard = root.copyBoard();
                        if ( newBoard[i][j][k] == Character.MIN_VALUE) {

                            newBoard[i][j][k] = symbol;

                            //Rotate boards
                            char[][][] newerBoard = rotateBoard(newBoard);

                            char[][][] evenNewerBoard = rotateBoard(newerBoard);

                            char[][][] newestBoard = rotateBoard(evenNewerBoard);



                            //Flip the boards
                            char[][][] newFlip = new char[3][3][3];

                            newFlip[0] = newBoard[2];
                            newFlip[1] = newBoard[1];
                            newFlip[2] = newBoard[0];


                            char[][][] newerFlip = new char[3][3][3];
                            for (int l = 0; l < 3; l++) {
                                for (int m = 0; m < 3; m++) {
                                    newerFlip[l][m][0] = newBoard[l][m][2];
                                    newerFlip[l][m][1] = newBoard[l][m][1];
                                    newerFlip[l][m][2] = newBoard[l][m][0];
                                }
                            }
                            char[][][] newestFlip = new char[3][3][3];
                            for (int l = 0; l < 3; l++) {

                                newestFlip[l][0] = newBoard[l][2];
                                newestFlip[l][1] = newBoard[l][1];
                                newestFlip[l][2] = newBoard[l][0];
                            }

                            boolean copy = false;
                            for (TicTacToeNode node : allTheNodes) {
                                if (node.equals(newBoard) || node.equals(newerBoard) ||node.equals(evenNewerBoard) ||
                                        node.equals(newestBoard) || node.equals(newFlip) || node.equals(newerFlip) ||
                                        node.equals(newestFlip)){
                                    copy = true;
                                    root.children.add(node);
                                    break;
                                }
                            }
                            if(!copy){
                                TicTacToeNode child = new TicTacToeNode( );
                                child.board = newBoard;
                                root.children.add( child );
                                allTheNodes.add(child);
                                if ( DEBUG )
                                    System.out.println( child );
                            }

                        }

                    }
                }
            }

        }

        return;
    }

    public static void buildBoards( TicTacToeNode root, char symbol, int depth) {
        //I added depth to this method because without a depth limit the full recursion takes too long, however you can try taking it out if you wish


        if(!allTheNodes.contains(root)){
            allTheNodes.add(root);
        }
        buildTheBoards(root, symbol);

        boolean hasWon = false;
        for ( TicTacToeNode child : root.children ) {
            if ( child.isGameWon( ) ) {
                hasWon = true;
                // stop creating branches, we are at a win/loss state
                // do not return from here so that the other siblings
                // will be correctly generated, just calculate the
                // minimax score
                if ( symbol == 'X' )
                    child.minimaxScore = 100-child.depth();
                else
                    child.minimaxScore = -100-child.depth();
            } else if ( child.isFull() ) {
                child.minimaxScore = 0;
            } else {
                // If the board is not a win, nor a tie then there are
                // more children to create, thus the recursive step
                // appears here
                boolean winFound = false;
                if(hasWon){
                    for (TicTacToeNode sibling : root.children) {
                        if(sibling != child){
                            if(sibling.isGameWon()){
                                if(symbol == 'X' && sibling.minimaxScore > 0){
                                    winFound = true;
                                }
                                if(symbol == 'O' && sibling.minimaxScore < -1){
                                    winFound = true;
                                }
                            }
                        }
                    }
                }
                if(!winFound){
                    if(depth != 0 && !root.isFull()){
                        if(child.children.size() == 0 && !child.isFull()){
                            buildBoards( child, symbol=='O'? 'X' : 'O', depth - 1);
                        }
                    }
                    /*      Use this if you decide to try taking away depth
                    if(child.children.size() == 0 && !child.isFull()){
                        buildBoards( child, symbol=='O'? 'X' : 'O');
                    }*/

                }
            }
        }

        
        // Once all children are processed we can decide our minimaxScore
        // based on their scores. Note that all subtrees are processed
        // by this point.
        if ( symbol == 'X' ) { // the next turn is X
            // find maximum scored child
            int max = Integer.MIN_VALUE;
            for ( TicTacToeNode child : root.children )
                if ( child.minimaxScore > max )
                    max = child.minimaxScore;
            root.minimaxScore = max;
        } else { // the next turn is O
            // find minimum scored child
            int min = Integer.MAX_VALUE;
            for ( TicTacToeNode child : root.children )
                if ( child.minimaxScore < min )
                    min = child.minimaxScore;
            root.minimaxScore = min;
        }

        DEBUG = true;

        if(DEBUG){
            System.out.println(" " + root.depth() + " : " + allTheNodes.size());
        }

        return;
    }

    public static void main( String[] args ) {
        TicTacToeNode tttTree = new TicTacToeNode();


        buildBoards( tttTree, 'X', 3);
        //Use the line below if you decide to try buildboards without depth
        //buildBoards( tttTree, 'X');
        TicTacToeNode treeRoot = tttTree;
        System.out.println(TicTacToeNode.nodes + " nodes created.");
/*
        System.out.printf("Parent MM: %d\n", tttTree.minimaxScore );
        
        // Show minimax scores for children of the first move ...
        // Answer the question - corner or center???!?!??!?
        for( TicTacToeNode child : tttTree.children ) {
            System.out.println( child );
            System.out.printf("Child MM: %d\n", child.minimaxScore );
        }*/

        Scanner kin = new Scanner( System.in );
        boolean gameOn = true;
        char symbol = 'X';
        char[][][] gameBoard = new char[3][3][3];

        while ( gameOn ) {
            // the user will be X, AI is O
            System.out.println( tttTree );

            boolean positionInvalid = true;
            int row, col, board;
            while ( positionInvalid ) {

                if ( symbol == 'X' ) {
                    System.out.print("Input board 1-3: ");
                    board = kin.nextInt();

                    System.out.print("Input row 1-3: ");
                    row = kin.nextInt();

                    System.out.print("Input col 1-3: ");
                    col = kin.nextInt();
                    // assume the user picks a correct unoccupied position
                    // to do: add data validation
                    gameBoard[board-1][row-1][col-1] = symbol;
                    boolean foundTheBoard = false;
                    for ( TicTacToeNode child : tttTree.children ) {
                        if (child.equals(gameBoard)) {
                            foundTheBoard = true;
                            if (!child.isFull()){
                                if(child.children.size() > 0){
                                    tttTree = child;
                                }else {
                                    buildBoards(child, 'O', 3);
                                    //Without depth: buildBoards( tttTree, 'O');
                                }
                            }else{
                                tttTree = child;
                            }

                            break;

                        }
                    } // find board in tree
                    if(!foundTheBoard){
                        buildBoards(tttTree, 'X', 3);
                        //Without Depth: buildBoards( tttTree, 'X');
                        for ( TicTacToeNode child : tttTree.children ) {
                            if (child.equals(gameBoard)) {
                                if (!child.isFull()){
                                    if(child.children.size() > 0){
                                        tttTree = child;
                                    }else {
                                        buildBoards(child, 'O', 3);
                                        //Without depth: buildBoards( tttTree, 'O');
                                    }
                                }else{
                                    tttTree = child;
                                }

                                break;

                            }
                        } // find board in tree
                    }
                } else {
                    // ah ha! AI time! pick the child node with the minimum minimax score
                    int min = Integer.MAX_VALUE;
                    TicTacToeNode tempBoard=null;

                    for ( TicTacToeNode child : tttTree.children ) {

                        if (child.minimaxScore < min) {
                            min = child.minimaxScore;
                            tempBoard = child;
                        }
                    }

                    System.out.println(TicTacToeNode.nodes + " nodes created.");

                    gameBoard = tempBoard.copyBoard();
                    tttTree = tempBoard;
                }

                positionInvalid = false;
            } // user input loop
            if ( tttTree.isGameWon() || tttTree.isFull() )
                gameOn = false; // to do : message with win or tie
            else
                symbol = ( symbol == 'X' ? 'O' : 'X' );

        } // gameOn loop
        System.out.println("done!");
        // to do: play again
    }
}
