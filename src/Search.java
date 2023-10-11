import java.util.HashMap;
import java.util.Map;

// Brute-force backtracking algorithm with optimizations
// Made by: Max Gurbanli
// Sources used: ChatGPT
// Optimizations: Dead spot detection, early termination, mutation order, constant time access to pentomino IDs

public class Search {
    public static final int horizontalGridSize = 3;
    public static final int verticalGridSize = 20;

    // Pentominoes for the input
    public static final char[] input = {'X', 'I', 'Z', 'T', 'U', 'V', 'W', 'Y', 'L', 'P', 'N', 'F'};

    // Map to get pentomino ID in constant time
    private static final Map<Character, Integer> pentIDMap = new HashMap<>();

    static {
        pentIDMap.put('X', 0);
        pentIDMap.put('I', 1);
        pentIDMap.put('Z', 2);
        pentIDMap.put('T', 3);
        pentIDMap.put('U', 4);
        pentIDMap.put('V', 5);
        pentIDMap.put('W', 6);
        pentIDMap.put('Y', 7);
        pentIDMap.put('L', 8);
        pentIDMap.put('P', 9);
        pentIDMap.put('N', 10);
        pentIDMap.put('F', 11);
    }

    // Static UI class to display the board
    public static UI ui = new UI(horizontalGridSize, verticalGridSize, 50);

    public static void search() {
        int[][] field = new int[horizontalGridSize][verticalGridSize];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = -1;
            }
        }
        long startTime = System.currentTimeMillis();
        boolean solutionFound = backtrackSearch(field, 0, ui);
        long endTime = System.currentTimeMillis();
        if (solutionFound) {
            System.out.println("Solution found");
        } else {
            System.out.println("No solution found");
        }
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    private static boolean backtrackSearch(int[][] field, int pentominoIndex, UI ui) {
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
                        if (!hasDeadSpot(field) && backtrackSearch(field, pentominoIndex + 1, ui)) {
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
    // Check for dead spots in the field. If there are, skip this branch of the search tree.
    // This is a naive approach that checks for single empty cells surrounded by other cells or boundaries.
    for (int i = 0; i < field.length; i++) {
        for (int j = 0; j < field[i].length; j++) {
            if (field[i][j] == -1) {
                if ((i == 0 || field[i - 1][j] != -1) &&
                    (i == field.length - 1 || field[i + 1][j] != -1) &&
                    (j == 0 || field[i][j - 1] != -1) &&
                    (j == field[i].length - 1 || field[i][j + 1] != -1)) {
                    return true;  // Dead spot found
                }
            }
        }
    }
    return false;
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

    // Add a piece to the board
    public static void addPiece(int[][] field, int[][] piece, int pieceID, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] == 1) {
                    field[x + i][y + j] = pieceID;
                }
            }
        }
    }

    public static void main(String[] args) {
        search();
    }
}
