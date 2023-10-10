import java.util.HashMap;
import java.util.Map;

public class Search {
    public static final int horizontalGridSize = 5;
    public static final int verticalGridSize = 12;

    // 1 of each pentomino letter
    public static final char[] input = {'X', 'I', 'Z', 'T', 'U', 'V', 'W', 'Y', 'L', 'F', 'P', 'N'};

    // Map to get pentomino ID in constant time
    private static final Map<Character, Integer> pentIDMap = new HashMap<>();

    static {
        for (int i = 0; i < input.length; i++) {
            pentIDMap.put(input[i], i);
        }
    }

    //Static UI class to display the board
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
        System.out.println("Time taken: " + (endTime - startTime) / 1000 + " s");
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

                    // Check for dead spots after placing the pentomino
                    if (!hasDeadSpot(field)) {
                        if (backtrackSearch(field, pentominoIndex + 1, ui)) {
                            return true;  // Found a solution
                        }
                    }

                    removePiece(field, pieceToPlace, x, y);  // Backtrack
                }
            }
        }
    }
    return false;  // Couldn't place this pentomino
}

private static boolean hasDeadSpot(int[][] field) {
    // Check for dead spots in the field.
    // This is a naive approach that checks for single empty cells surrounded by other cells or boundaries.
    // More sophisticated checks can be added as needed.
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

    private static void removePiece(int[][] field, int[][] piece, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] == 1) {
                    field[x + i][y + j] = -1;  // Remove the piece
                }
            }
        }
    }

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
