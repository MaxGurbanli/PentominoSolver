// Brute-force backtracking algorithm with optimizations
// Made by: Max Gurbanli
// Sources used: ChatGPT
// Optimizations: Dead spot detection, early termination, mutation order, constant time access to pentomino IDs

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * This class includes the methods to support the search of a solution.
 */
public class Search
{
    public static final int horizontalGridSize = 5;
    public static final int verticalGridSize = 3;

	public static boolean[][][][] bigmemo;
    
//     public static final char[] input = { 'W', 'Y', 'I', 'T', 'Z', 'L', 'N', 'F', 'P', 'V'};
	public static ArrayList<Character> input = new ArrayList<>();
	//	public static final char[] input = {'X','I','Z','T', 'U','V','W','Y','L','P','N','F'};
//	public static final char[] input = {'F','N','P','L','Y','W','V','U','T','Z','I','X'};

    //Static UI class to display the board
    public static UI ui = new UI(horizontalGridSize, verticalGridSize, 50);


	/**
	 * Helper function which starts a basic search algorithm
	 */
    public static void search()
    {
		getUserInput();

		boolean[][][][] memoTable = new boolean[12][12][12][12];
		// Initialize the memoization table with false values (not solved)
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 12; j++) {
				for (int k = 0; k < 12; k++) {
					for (int l = 0; l < 12; l++) {
						memoTable[i][j][k][l] = false;
					}
				}
			}
		}
		bigmemo = memoTable;
        // Initialize an empty board
        int[][] field = new int[horizontalGridSize][verticalGridSize];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = -1;
            }
        }
        //Start the basic search
//        basicSearch(field);
		recursiveSearch(field, 0);
    }

	private static boolean recursiveSearch(int[][] field, int inputIndex) {
		boolean solution = true;
		if (inputIndex == input.size()){
			inputIndex = 0;
		}
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if(field[i][j] == -1)
					solution = false;
			}
		}
		if (solution){
			ui.setState(field);
			return true;
		}
		int pentID = characterToID(input.get(inputIndex));
		int mutation = PentominoDatabase.data[pentID].length;
		for (int i = 0; i < mutation; i++) {
			int[][] piece = PentominoDatabase.data[pentID][i];
			for (int j = 0; j < field.length; j++) {
				for (int k = 0; k < field[j].length; k++) {
					if (canPlace(field, piece, j, k)) {
						if (isSolved(pentID,mutation,j,k))
							return true;
						addPiece(field, piece, pentID, j, k);
						memoize(pentID, i, j, k, true);
						ui.setState(field);
						if (recursiveSearch(field, inputIndex + 1)) {
							return true;
						}
						removePiece(field, piece, j, k);
					}
				}
			}
		}
		return false;
	}

	public static boolean isSolved(int pentID, int mutation, int row, int col) {
		// Check if a state has been memoized (solved)
		return bigmemo[pentID][mutation][row][col];
	}

	public static void memoize(int pentID, int mutation, int row, int col, boolean solved) {
		// Store the result of solving a state in the memoization table
		bigmemo[pentID][mutation][row][col] = solved;
	}

	public static void getUserInput() {
		Scanner reader = new Scanner(System.in);
		char c = 0;
		int index = 0;
		while (reader.hasNext() && index < 12){
			String ok = reader.next().toUpperCase().trim();
			if (ok.equals("END"))
				break;
			c = ok.trim().charAt(0);
			input.add(c);
			index++;
		}
		System.out.println("end");
	}

	/**
	 * Get as input the character representation of a pentomino and translate it into its corresponding numerical value (ID)
	 * @param character a character representating a pentomino
	 * @return	the corresponding ID (numerical value)
	 */
    private static int characterToID(char character) {
    	int pentID = -1; 
    	if (character == 'X') {
    		pentID = 0;
    	} else if (character == 'I') {
    		pentID = 1;
    	} else if (character == 'Z') {
    		pentID = 2;
    	} else if (character == 'T') {
    		pentID = 3;
    	} else if (character == 'U') {
    		pentID = 4;
     	} else if (character == 'V') {
     		pentID = 5;
     	} else if (character == 'W') {
     		pentID = 6;
     	} else if (character == 'Y') {
     		pentID = 7;
    	} else if (character == 'L') {
    		pentID = 8;
    	} else if (character == 'P') {
    		pentID = 9;
    	} else if (character == 'N') {
    		pentID = 10;
    	} else if (character == 'F') {
    		pentID = 11;
    	} 
    	return pentID;
    }
	
	/**
	 * Basic implementation of a search algorithm. It is not a bruto force algorithms (it does not check all the posssible combinations)
	 * but randomly takes possible combinations and positions to find a possible solution.
	 * The solution is not necessarily the most efficient one
	 * This algorithm can be very time-consuming
	 * @param field a matrix representing the board to be fulfilled with pentominoes
	 */
    private static void basicSearch(int[][] field){
    	Random random = new Random();
    	boolean solutionFound = false;
    	
    	while (!solutionFound) {
    		solutionFound = true;
    		
    		//Empty board again to find a solution
			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field[i].length; j++) {
					field[i][j] = -1;
				}
			}
    		
    		//Put all pentominoes with random rotation/flipping on a random position on the board
    		for (int i = 0; i < input.size(); i++) {
    			
    			//Choose a pentomino and randomly rotate/flip it
    			int pentID = characterToID(input.get(i));
    			int mutation = random.nextInt(PentominoDatabase.data[pentID].length);
    			int[][] pieceToPlace = PentominoDatabase.data[pentID][mutation];
    		
    			//Randomly generate a position to put the pentomino on the board
    			int x;
    			int y;
    			if (horizontalGridSize < pieceToPlace.length) {
    				//this particular rotation of the piece is too long for the field
    				x=-1;
    			} else if (horizontalGridSize == pieceToPlace.length) {
    				//this particular rotation of the piece fits perfectly into the width of the field
    				x = 0;
    			} else {
    				//there are multiple possibilities where to place the piece without leaving the field
    				x = random.nextInt(horizontalGridSize-pieceToPlace.length+1);
    			}

    			if (verticalGridSize < pieceToPlace[0].length) {
    				//this particular rotation of the piece is too high for the field
    				y=-1;
    			} else if (verticalGridSize == pieceToPlace[0].length) {
    				//this particular rotation of the piece fits perfectly into the height of the field
    				y = 0;
    			} else {
    				//there are multiple possibilities where to place the piece without leaving the field
    				y = random.nextInt(verticalGridSize-pieceToPlace[0].length+1);
    			}
    		
    			//If there is a possibility to place the piece on the field, do it
    			if (x >= 0 && y >= 0) {
	    			addPiece(field, pieceToPlace, pentID, x, y);
	    		} 
    		}
    		//Check whether complete field is filled

			// draft 1
			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field[i].length; j++) {
					if (field[i][j] == -1) {
						solutionFound = false;
					}
				}
			}

    		
    		if (solutionFound) {
    			//display the field
    			ui.setState(field); 
    			System.out.println("Solution found");
    			break;
    		}
    	}
    }

    private static boolean optimizedRecursiveSearch(int[][] field, int pentominoIndex, UI ui) {
        if (pentominoIndex == input.length) {
            return true;  // All pentominoes have been placed successfully
        }

        int pentID = pentIDMap.get(input[pentominoIndex]);
        
        for (int mutation = 0; mutation < PentominoDatabase.data[pentID].length; mutation++) {
            int[][] pieceToPlace = PentominoDatabase.data[pentID][mutation];
            for (int x = 0; x <= horizontalGridSize - pieceToPlace.length; x++) {
                for (int y = 0; y <= verticalGridSize - pieceToPlace[0].length; y++) {
                    if (canPlace(field, pieceToPlace, x, y)) {
                        addPiece(field, pieceToPlace, pentID, x, y);
                        ui.setState(field);
                        if (!hasDeadSpot(field) && optimizedRecursiveSearch(field, pentominoIndex + 1, ui)) {
                            return true;  // Found a solution
                        }
                        removePiece(field, pieceToPlace, x, y);  // Backtrack
                    }
                }
            }
        }
        return false;  // Couldn't place this pentomino
    }

    private static boolean hasDeadSpot(int[][] field) {
        boolean[][] visited = new boolean[field.length][field[0].length];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == -1 && !visited[i][j]) {
                    int size = floodFill(field, i, j, visited);
                    if (size % 5 != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static int floodFill(int[][] field, int i, int j, boolean[][] visited) {
        if (i < 0 || i >= field.length || j < 0 || j >= field[0].length || visited[i][j] || field[i][j] != -1) {
            return 0;
        }
        visited[i][j] = true;
        return 1 + floodFill(field, i + 1, j, visited) + floodFill(field, i - 1, j, visited) +
               floodFill(field, i, j + 1, visited) + floodFill(field, i, j - 1, visited);
    }
    
    // Check if a piece can be placed at a given position
    private static boolean canPlace(int[][] field, int[][] piece, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] == 1 && (x + i >= horizontalGridSize || y + j >= verticalGridSize || field[x + i][y + j] != -1)) {
                    return false;  // Piece goes out of bounds or overlaps another piece
                }
            }
        }
        return true;
    }

    // Remove a piece from the board
    private static void removePiece(int[][] field, int[][] piece, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] == 1) {
                    field[x + i][y + j] = -1;  // Remove the piece
                }
            }
        }
    }

	public static boolean canPlace(int[][] field, int[][] pentominoe, int row, int col) {
		// Get the dimensions
		int boardHeight = field.length;
		int boardWidth = field[0].length;

		int PentHeight = pentominoe.length;
		int PentWidth = pentominoe[0].length;

		// Check if the shape exceeds the boundaries of the board
		if (row + PentHeight > boardHeight || col + PentWidth > boardWidth) {
			return false;
		}

		// Check for collisions
		for (int i = 0; i < PentHeight; i++) {
			for (int j = 0; j < PentWidth; j++) {
				if (pentominoe[i][j] == 1 && field[row + i][col + j] != -1) {
					return false;
				}
			}
		}
		return true;
	}

	public static void removePiece(int[][] field, int[][] piece, int row, int col) {
		int PentHeight = piece.length;
		int PentWidth = piece[0].length;

		for (int i = 0; i < PentHeight; i++) {
			for (int j = 0; j < PentWidth; j++) {
				if (piece[i][j] == 1) {
					field[row + i][col + j] = -1;
				}
			}
		}
	}



	/**
	 * Main function. Needs to be executed to start the basic search algorithm
	 */
    public static void main(String[] args)
    {
        search();
    }
}
