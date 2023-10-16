package search;

/**
 * @author Department of Data Science and Knowledge Engineering (DKE)
 * @version 2022.0
 */

import java.util.ArrayList;
import java.util.Scanner;

import common.UI;
import pentominoes.PentominoDatabase;

/**
 * This class includes the methods to support the search of a solution.
 */
public class BacktrackingSearch {
	public static final int horizontalGridSize = 5;
	public static final int verticalGridSize = 3;

	public static boolean[][][][] bigmemo;

	// public static final char[] input = { 'W', 'Y', 'I', 'T', 'Z', 'L', 'N', 'F',
	// 'P', 'V'};
	public static ArrayList<Character> input = new ArrayList<>();
	// public static final char[] input = {'X','I','Z','T',
	// 'U','V','W','Y','L','P','N','F'};
	// public static final char[] input =
	// {'F','N','P','L','Y','W','V','U','T','Z','I','X'};

	// Static UI class to display the board
	public static UI ui = new UI(horizontalGridSize, verticalGridSize, 50);

	/**
	 * Helper function which starts a basic search algorithm
	 */
	public static void search() {
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
				// -1 in the state matrix corresponds to empty square
				// Any positive number identifies the ID of the pentomino
				field[i][j] = -1;
			}
		}
		// Start the basic search
		// basicSearch(field);
		recursiveSearch(field, 0);
	}

	/*
	 * Recursive backtracking function that iterates over all available pentominoes
	 * and their mutations, attempts to place them on the grid, and uses recursion
	 * to explore all the possible configurations. It takes the game grid and the
	 * index of the pentominoe as parameters, returns "true" if a solution has been
	 * found
	 * and "false" otherwise. It uses the memoization table to keep track of states
	 * that have already been solved to avoid redundant work. When a dead end is
	 * reached,
	 * it backtracks and continues to explore other possibilities.
	 */
	private static boolean recursiveSearch(int[][] field, int inputIndex) {
		boolean solution = true;
		if (inputIndex == input.size()) {
			inputIndex = 0; // if inpitIndex has reached the end of the list, it is set back to 0 so it can
							// cycle through all the available pentominoes.
		}
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j] == -1)
					solution = false; /*
										 * Iterates through all grid cells to check if the value is equal to -1, meaning
										 * that the field is empty. If an empty
										 * field is found, the solution is set to "false", meaning no solution has been
										 * found.
										 */
			}
		}
		if (solution) {
			ui.setState(field);
			return true; // If a solution has been found, the UI is updated accordingly and the method
							// returns "true".
		}
		int pentID = characterToID(input.get(inputIndex)); // Retrieves the ID of a pentominoe.
		int mutation = PentominoDatabase.data[pentID].length; // Retrieves the amount of possible mutations of a
																// pentominoe.
		for (int i = 0; i < mutation; i++) {
			int[][] piece = PentominoDatabase.data[pentID][i]; // Iterates through all mutations of a pentominoe and
																// stores them in "piece".
			for (int j = 0; j < field.length; j++) {
				for (int k = 0; k < field[j].length; k++) {
					if (canPlace(field, piece, j, k)) { // Calls the canPlace method to check if a piece can be placed
														// in a given position
						if (isSolved(pentID, mutation, j, k))
							return true; // Calls the isSolved method to check if a state has been memoized, and returns
											// "true" if it has.
						addPiece(field, piece, pentID, j, k); // Calls the addPiece method to add a pentominoe to the
																// grid.
						memoize(pentID, i, j, k, true); // Memoizes the state as solved.
						ui.setState(field); // Updates the UI.
						if (recursiveSearch(field, inputIndex + 1)) {
							return true; /*
											 * Calls the recursiveSearch method recursively. Returns "true" if a
											 * solution has been found.
											 */
						}
						removePiece(field, piece, j, k); // If the recursive call doesn't return "true", the pentominoe
															// is removed from the grid.
					}
				}
			}
		}
		return false; // If no placement for the pentominoe is found and the state does not lead to a
						// solution, the method returns "false".
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
		while (reader.hasNext() && index < 12) { // Enters a loop that continues as long as the user keeps entering
													// input ans the index is smaller than 12.
			String ok = reader.next().toUpperCase().trim(); // Reads the string "ok", converts it to uppercase and
															// removes whitespaces.
			if (ok.equals("END"))
				break; // The loop breaks if the user inputs "END"
			c = ok.trim().charAt(0); // Extracts the first character from "ok"
			input.add(c); // Adds "c" to the input array list
			index++; // Increments the "index" variable when a character has been added.
		}
		System.out.println("end");
		reader.close();
	}

	/**
	 * Get as input the character representation of a pentomino and translate it
	 * into its corresponding numerical value (ID)
	 * 
	 * @param character a character representating a pentomino
	 * @return the corresponding ID (numerical value)
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
	 * Adds a pentomino to the position on the field (overriding current board at
	 * that position)
	 * 
	 * @param field   a matrix representing the board to be fulfilled with
	 *                pentominoes
	 * @param piece   a matrix representing the pentomino to be placed in the board
	 * @param pieceID ID of the relevant pentomino
	 * @param x       x position of the pentomino
	 * @param y       y position of the pentomino
	 */
	public static void addPiece(int[][] field, int[][] piece, int pieceID, int x, int y) {
		for (int i = 0; i < piece.length; i++) // loop over x position of pentomino
		{
			for (int j = 0; j < piece[i].length; j++) // loop over y position of pentomino
			{
				if (piece[i][j] == 1) {
					// Add the ID of the pentomino to the board if the pentomino occupies this
					// square
					field[x + i][y + j] = pieceID;
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
					return false; /*
									 * If the field in the "pentominoe" array is equal to '1' (not empty), and its
									 * corresponding field in the "field" array
									 * is not equal to '-1' (not empty), there is a collision.
									 */
				}
			}
		}
		return true;
	}

	public static void removePiece(int[][] field, int[][] piece, int row, int col) {
		int PentHeight = piece.length;
		int PentWidth = piece[0].length;

		for (int i = 0; i < PentHeight; i++) {
			for (int j = 0; j < PentWidth; j++) { // Iterates over the rowns and columns of the "piece" array
				if (piece[i][j] == 1) {
					field[row + i][col + j] = -1; /*
													 * Checks if the current field in "piece" has the value '1', meaning
													 * that it is occupied by a piece.
													 * if that is the case, it changes the corresponding cell in the
													 * "field" array, meaning that it is now empty.
													 */
				}
			}
		}
	}

	/**
	 * Main function. Needs to be executed to start the basic search algorithm
	 */
	public static void main(String[] args) {
		search();
	}
}
