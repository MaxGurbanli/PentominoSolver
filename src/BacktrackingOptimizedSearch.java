
// Brute-force backtracking algorithm with optimizations
// Made by: Max Gurbanli
// Sources used: ChatGPT
// Optimizations: Dead spot detection, early termination, mutation order, constant time access to pentomino IDs

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class BacktrackingOptimizedSearch {

    public static int horizontalGridSize;
    public static int verticalGridSize;
    public static char[] input;

    // Map to get pentomino ID in constant time
    private static final Map<Character, Integer> pentIDMap = new HashMap<>();

    // Initialize the map
    // This is used for constant time access to the pentomino ID
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
    public static UI ui;

    public static void search() {
        int[][] field = new int[horizontalGridSize][verticalGridSize];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j] = -1;
            }
        }
        long startTime = System.currentTimeMillis();
        boolean solutionFound = optimizedRecursiveSearch(field, 0, ui);
        long endTime = System.currentTimeMillis();
        if (solutionFound) {
            System.out.println("Solution found");
        } else {
            System.out.println("No solution found");
        }
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    private static boolean optimizedRecursiveSearch(int[][] field, int pentominoIndex, UI ui) {
        if (pentominoIndex == input.length) {
            return true; // All pentominoes have been placed successfully
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
                            return true; // Found a solution
                        }
                        removePiece(field, pieceToPlace, x, y); // Backtrack
                    }
                }
            }
        }
        return false; // Couldn't place this pentomino
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
                if (piece[i][j] == 1
                        && (x + i >= horizontalGridSize || y + j >= verticalGridSize || field[x + i][y + j] != -1)) {
                    return false; // Piece goes out of bounds or overlaps another piece
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
                    field[x + i][y + j] = -1; // Remove the piece
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

    public static void getUserInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the horizontal grid size:");
        try {
            horizontalGridSize = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please re-enter.");
            horizontalGridSize = Integer.parseInt(scanner.nextLine());
        }

        System.out.println("Enter the vertical grid size:");
        try {
            verticalGridSize = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please re-enter.");
            verticalGridSize = Integer.parseInt(scanner.nextLine());
        }

        while (horizontalGridSize * verticalGridSize % 5 != 0) {
            System.out.println("The total grid size must be a multiple of 5. Please re-enter.");

            System.out.println("Enter the horizontal grid size:");
            horizontalGridSize = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter the vertical grid size:");
            verticalGridSize = Integer.parseInt(scanner.nextLine());
        }

        System.out.println("Enter pentomino letters separated by commas (e.g., X, U, I):");
        String inputStr = scanner.nextLine();
        inputStr = inputStr.toUpperCase();

        List<String> lettersList = Arrays.asList(inputStr.split(","));
        input = new char[lettersList.size()];

        // Ensure that the letters are valid and will fit on the board perfectly
        while (lettersList.size() != horizontalGridSize * verticalGridSize / 5) {
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

        ui = new UI(horizontalGridSize, verticalGridSize, 60);

    }

    public static void main(String[] args) {
        getUserInput();
        // TESTING CODE
        // input = new char[] {'X', 'I', 'Z', 'T', 'U', 'V', 'W', 'Y', 'L', 'P', 'N',
        // 'F'};
        // horizontalGridSize = 12;
        // verticalGridSize = 5;
        // ui = new UI(horizontalGridSize, verticalGridSize, 50);
        System.out.println("Starting search...");
        search();
    }
}
