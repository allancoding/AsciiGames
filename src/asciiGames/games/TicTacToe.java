package asciiGames.games;

import org.fusesource.jansi.AnsiConsole;
import asciiGames.ascii;

public class TicTacToe {
    
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Game.start(true, true);
    }

    public static class Game {
        public static final String Name = "Tic Tac Toe";
        public static final String Description = "A simple version of Tic Tac Toe.";
        public static boolean shutdownHookAdded = false;

        public static void start(boolean instructions, boolean main) {
            System.setProperty("file.encoding", "UTF-8");
            if (!shutdownHookAdded && main) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(200);
                            ascii.println("Exiting Tic Tac Toe...");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                shutdownHookAdded = true;
            }
            ascii.clear();
            Game_Board game = new Game_Board();		//new game
            int p1r,p1c,p2r,p2c;
            boolean isOver = false;	//set to true when game over
            
            //print game title
            ascii.println("Welcome to Tic-Tac-Toe");
            ascii.println("======================");
            
            //do loop while game is not over
            do {
                //do until user chooses valid input
                //ie. invalid input = position already taken
                do {			
                    ascii.println("Player 1 turn 'X'");
                    game.printBoard();
                    ascii.println("Which Row would you like?");
                        p1r = Integer.parseInt(System.console().readLine());
                    ascii.println("Which Col would you like?");
                        p1c = Integer.parseInt(System.console().readLine());
                } while (game.legalMove(p1r, p1c) != true);
                
                //after getting valid input, place move on board, and increment move value
                game.setBoard(p1r,p1c, 1);
                game.moveIncrement();
                
                //check if win, or draw
                //If there are less than 5 moves, skip win / draw checks
                if (game.getMoveCounter() >= 4) {
                    if (game.winByColumn(1) == true) {
                        isOver = true;
                    } else if (game.winByDiagonal(1) == true) {
                        isOver = true;
                    } else if (game.winByRow(1) == true) {
                        isOver = true;
                    } else if (game.draw() == true) {
                        isOver = true;
                    }
                }
                
                //if game is already over, skip player 2
                if (isOver != true) {	
                    //do until user chooses valid input
                    do {
                        ascii.println("Player 2 turn 'O'");
                        game.printBoard();
                        ascii.println("Which Row would you like?");
                        p2r = Integer.parseInt(System.console().readLine());
                        ascii.println("Which Col would you like?");
                        p2c = Integer.parseInt(System.console().readLine());
                    } while (game.legalMove(p2r, p2c) != true);
                    
                    //after getting valid input, place move on board, increment move value
                    game.setBoard(p2r,p2c, 2);
                    game.moveIncrement();
                    
                    //check if win, or draw
                    //If there are less than 5 moves, skip win / draw checks
                    if (game.getMoveCounter() >= 4) {
                        if (game.winByColumn(2) == true) {
                            isOver = true;
                        } else if (game.winByDiagonal(2) == true) {
                            isOver = true;
                        } else if (game.winByRow(2) == true) {
                            isOver = true;
                        } else if (game.draw() == true) {
                            isOver = true;
                        }
                    }
                }
            } while (isOver == false);
            ascii.waitForEnter("Press enter to continue to the main menu...");
        }
        
        public static class Game_Board {
            final int ROW = 3;	//number of rows
            final int COL = 3;	//number of cols
            char [][] board = new char[ROW][COL];	//board
            private int moveCounter = 0;	//counts game moves
            
            public Game_Board () {
                //initialize board with ' ' 
                for (int row = 0; row < ROW; row++) {
                    for (int col = 0; col < COL; col++) {
                        board[row][col] = ' ';
                    }
                }
            }
            
            public void printBoard() {
                for (int row = 0; row < ROW; row++) {
                    ascii.println(board[row][0] + "|" + board[row][1] + "|" + board[row][2]);
                    if (row < 2 )
                        ascii.println("-----");
                }
            }

            public void moveIncrement() {
                moveCounter += 1;
            }

            public int getMoveCounter () {
                return moveCounter;
            }

            public boolean draw() { 	
                if (moveCounter == 9) {	//max moves on a board = 9
                    ascii.println("Cats Game!");
                    printBoard();
                    return true;
                }
                else {
                    return false;
                }
            }

            public boolean winByRow(int player) { 
                for (int i=0; i < 3; i++) { 
                    if (board[i][0] == board[i][1] && 
                        board[i][1] == board[i][2] &&  
                        board[i][0] != ' ') {
                        if (player == 1) {
                            ascii.println("X player wins!");
                            printBoard();
                            return true;
                        }
                        else {
                            ascii.println("O player wins!");
                            printBoard();
                            return true;
                        }
                    }
                } 
                return false; 
            }
            public boolean winByColumn(int player) {
                for (int i = 0; i < 3; i++) { 
                    if (board[0][i] == board[1][i] && 
                        board[1][i] == board[2][i] &&  
                        board[0][i] != ' ') {
                        if (player == 1) {
                            ascii.println("X player wins!");
                            printBoard();
                            return true;
                        }
                        else {
                            ascii.println("O player wins!");
                            printBoard();
                            return true;
                        }
                    }
                }
                return false; 
            }
            
            public boolean winByDiagonal(int player) {
                if (board[0][0] == board[1][1] && 
                    board[1][1] == board[2][2] &&  
                    board[0][0] != ' ') {
                    if (player == 1) {
                        ascii.println("X player wins!");
                        printBoard();
                        return true;
                    }
                    else {
                        ascii.println("O player wins!");
                        printBoard();
                        return true;
                    }
                }
                if (board[0][2] == board[1][1] && 
                    board[1][1] == board[2][0] && 
                    board[0][2] != ' ') {
                    if (player == 1) {
                        ascii.println("X player wins!");
                        printBoard();
                        return true;
                    }
                    else {
                        ascii.println("O player wins!");
                        printBoard();
                        return true;
                    }
                }
                return false; 
            }
            //set board with player move
            public void setBoard(int r, int c, int p) {
                if (p == 1) {
                    board[r][c] = 'X';
                }
                if (p == 2) {
                    board[r][c] = 'O';
                }
            }
            //check if legal move
            boolean legalMove (int r, int c) {
                if (r > 2 && c > 2) {
                    ascii.println("Invalid Row & Column Input - Choose 0,1, or 2");
                    return false;
                } else if (r > 2 ) {
                    ascii.println("Invalid Row Input - Choose 0,1, or 2");
                    return false;
                } else if (c > 2) {
                    ascii.println("Invalid Column Input - Choose 0,1, or 2");
                    return false;
                }
                if (board[r][c] != ' ') {
                    ascii.println("Illegal Move");
                    return false;
                } else {
                    return true;
                }
            }
        }
    }
}
