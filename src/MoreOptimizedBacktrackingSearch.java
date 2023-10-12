// Brute-force backtracking algorithm with optimizations
// Optimizations: Recursive backtrack search, dead spot detection, constant time access to pentomino IDs
// Made by: Max Gurbanli

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class MoreOptimizedBacktrackingSearch {

    public int horiGridSize;
    public int vertGridSize;
    public char[] input;

    // This key-value store maps a pentomino letter to its ID
    // This is used to get the ID of a pentomino in constant time compared to using
    // if-else statements
    private static final Map<Character, Integer> pentominoKeyToID = new HashMap<>();
    static {
        pentominoKeyToID.put('X', 0);
        pentominoKeyToID.put('I', 1);
        pentominoKeyToID.put('Z', 2);
        pentominoKeyToID.put('T', 3);
        pentominoKeyToID.put('U', 4);
        pentominoKeyToID.put('V', 5);
        pentominoKeyToID.put('W', 6);
        pentominoKeyToID.put('Y', 7);
        pentominoKeyToID.put('L', 8);
        pentominoKeyToID.put('P', 9);
        pentominoKeyToID.put('N', 10);
        pentominoKeyToID.put('F', 11);
    }

    // Create the UI object
    public UI ui;

    public void search() {
        int[][] field = new int[horiGridSize][vertGridSize];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = -1;
            }
        }
        long startTime = System.currentTimeMillis();
        boolean foundSolution = optimizedRecursiveSearch(field, 0, ui);
        long endTime = System.currentTimeMillis();
        if (foundSolution) {
            System.out.println("Solution found");
        } else {
            System.out.println("No solution found");
        }
        System.out.println("Found a solution in " + (endTime - startTime) + " ms");
    }

    /**
     * Performs an optimized recursive search.
     * Iterates through all possible mutations of the pentomino and tries to place
     * it on the field.
     * Once a pentomino can be placed, the method recursively places the next
     * pentomino.
     * If a dead spot is found on the field, the method backtracks.
     * 
     * @param field          a matrix representing the board to be fulfilled with
     *                       pentominoes
     * @param pentominoIndex the index of the pentomino to be placed
     * @param ui             the UI object
     * @return true if a solution is found, false otherwise
     */
    private boolean optimizedRecursiveSearch(int[][] field, int pentominoIndex, UI ui) {
        if (pentominoIndex == input.length) {
            return true; // all pentominos have been placed, the solution is found
        }

        int pentominoID = pentominoKeyToID.get(input[pentominoIndex]);

        for (int mutation = 0; mutation < PentominoDatabase.data[pentominoID].length; mutation++) {
            int[][] pieceToPlace = PentominoDatabase.data[pentominoID][mutation];

            for (int x = 0; x <= horiGridSize - pieceToPlace.length; x++) {
                for (int y = 0; y <= vertGridSize - pieceToPlace[0].length; y++) {
                    if (canPlace(field, pieceToPlace, x, y)) {

                        addPiece(field, pieceToPlace, pentominoID, x, y);

                        if (ui != null) {
                            ui.setState(field);
                        }

                        if (!hasDeadSpot(field) && optimizedRecursiveSearch(field, pentominoIndex + 1, ui)) {
                            return true; // Found a solution
                        }

                        removePiece(field, pieceToPlace, x, y); // Backtrack
                    }
                }
            }
        }
        return false; // Couldn't place this pentomino
    }

    /**
     * Checks if the given field has a dead spot, i.e. a region of empty cells that
     * is not a multiple of 5 in size.
     * Uses flood fill algorithm to count the size of each empty region.
     * 
     * @param field a matrix representing the game board
     * @return true if the field has a dead spot, false otherwise
     */
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

    /**
     * Performs a flood fill algorithm on a 2D integer array, starting from the
     * specified cell (i, j).
     * Marks all cells that are reachable from the starting cell and have a value of
     * -1.
     *
     * @param field   a matrix representing the game board
     * @param i       the row index of the starting cell
     * @param j       the column index of the starting cell
     * @param visited a 2D boolean array representing which cells have already been
     *                visited
     * @return the number of cells that were marked by the flood fill algorithm
     */
    private static int floodFill(int[][] field, int i, int j, boolean[][] visited) {
        if (i < 0 || i >= field.length || j < 0 || j >= field[0].length || visited[i][j] || field[i][j] != -1) {
            return 0;
        }
        visited[i][j] = true;
        return 1 + floodFill(field, i + 1, j, visited) + floodFill(field, i - 1, j, visited) +
                floodFill(field, i, j + 1, visited) + floodFill(field, i, j - 1, visited);
    }

    /**
     * Determines whether a given piece can be placed on the field at the specified
     * position.
     * 
     * @param field a matrix representing the game board
     * @param piece a matrix representing the pentomino to be placed in the board
     * @param x     x position of the pentomino
     * @param y     y position of the pentomino
     * @return true if the piece can be placed at the specified position, false
     *         otherwise
     */
    private boolean canPlace(int[][] field, int[][] piece, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] == 1
                        && (x + i >= horiGridSize || y + j >= vertGridSize || field[x + i][y + j] != -1)) {
                    return false; // Piece goes out of bounds or overlaps another piece
                }
            }
        }
        return true;
    }

    /**
     * Removes a pentomino from the position on the field
     * 
     * @param field   a matrix representing the game board
     * @param piece   a matrix representing the pentomino to be removed from the
     *                board
     * @param pieceID ID of the relevant pentomino
     * @param x       x position of the pentomino
     * @param y       y position of the pentomino
     */
    private static void removePiece(int[][] field, int[][] piece, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] == 1) {
                    field[x + i][y + j] = -1;
                }
            }
        }
    }

    /**
     * Adds a pentomino to the position on the field (overriding current board at
     * that position)
     * 
     * @param field   a matrix representing the game board
     * @param piece   a matrix representing the pentomino to be placed in the board
     * @param pieceID ID of the relevant pentomino
     * @param x       x position of the pentomino
     * @param y       y position of the pentomino
     */
    public void addPiece(int[][] field, int[][] piece, int pieceID, int x, int y) {
        for (int i = 0; i < piece.length; i++) // loop over x position of pentomino
        {
            for (int j = 0; j < piece[i].length; j++) // loop over y position of pentomino
            {
                if (piece[i][j] == 1) {
                    // Add the ID of the pentomino to the board if the pentomino occupies this
                    // square
                    field[x + i][y + j] = pieceID;
                    if (ui != null) {
                        ui.setState(field);
                    }
                }
            }
        }
    }

    public void getUserInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the horizontal grid size:");
        try {
            horiGridSize = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please re-enter.");
            horiGridSize = Integer.parseInt(scanner.nextLine());
        }

        System.out.println("Enter the vertical grid size:");
        try {
            vertGridSize = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please re-enter.");
            vertGridSize = Integer.parseInt(scanner.nextLine());
        }

        while (horiGridSize * vertGridSize % 5 != 0) {
            System.out.println("The total grid size must be a multiple of 5. Please re-enter.");

            System.out.println("Enter the horizontal grid size:");
            horiGridSize = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter the vertical grid size:");
            vertGridSize = Integer.parseInt(scanner.nextLine());
        }

        System.out.println("Enter pentomino letters separated by commas (e.g., X, U, I):");
        String inputStr = scanner.nextLine();
        inputStr = inputStr.toUpperCase();

        List<String> lettersList = Arrays.asList(inputStr.split(","));
        input = new char[lettersList.size()];

        // Ensure that the letters are valid and will fit on the board perfectly
        while (lettersList.size() != horiGridSize * vertGridSize / 5) {
            System.out.println(
                    "The number of pentominoes must be equal to the total grid size divided by 5. Please re-enter.");

            System.out.println("Enter pentomino letters separated by commas (e.g., X, U, I):");
            inputStr = scanner.nextLine();
            lettersList = Arrays.asList(inputStr.split(","));
        }

        for (int i = 0; i < lettersList.size(); i++) {
            input[i] = lettersList.get(i).trim().toUpperCase().charAt(0);
        }

        scanner.close();

        ui = new UI(horiGridSize, vertGridSize, 60);

    }

    /**
     * Main function. Needs to be executed to start the search algorithm
     */
    public static void main(String[] args) {
        // TESTING CODE
        MoreOptimizedBacktrackingSearch search = new MoreOptimizedBacktrackingSearch();
        search.getUserInput();
        // search.horiGridSize = 5;
        // search.vertGridSize = 12;
        // search.input = new char[] { 'X', 'I', 'Z', 'T', 'U', 'V', 'W', 'Y', 'L', 'P',
        // 'N', 'F' };
        // search.ui = new UI(search.horiGridSize, search.vertGridSize, 50);
        System.out.println("Starting search...");
        search.search();
    }
}
