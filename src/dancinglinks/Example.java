package dancinglinks;

/**
 * @author Irdi Zeneli
 * @version 2022.0
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import common.UI;
import pentominoes.PentominoDatabase;

/**
 * Class includes methods which make the inputs ready for DLX
 */
public class Example {
    private static final ArrayList<ArrayList<Integer>> theRows = new ArrayList<>();
    public static HashMap<ArrayList<Integer>, int[]> PentominoToRow = new HashMap<>();
    public static int global_rows;
    public static int global_cols;
    // Static ui to display board
    public static UI ui;
    static int[][] naive_field;

    /**
     * Takes a Character array of pentominoes then works through them and creates
     * all the possible rows it can make
     * so that the rows can be worked on by the DLX.
     * 
     * @param pentominoes Simple Character array which holds the pentominoes we got
     *                    from the user
     * @return The Sparse Matrix Which is used by the DLX. There is a cool concept I
     *         have followed here.
     *         If we have n inputs then the format is ->
     *         (uniqueInputs)[X*n](PossiblePositions)[Y*row*col]
     *         Say we have 2 pentominoes and 8 possible positions [2][4] array then
     *         format is -> XXYYYYYYYY
     *         This makes sure that the DLX works for different 'same' inputs like
     *         'U', 'X', 'U'
     */
    static int[][] makeSparseMatrix(Character[] pentominoes) {
        int[][] pseudoGrid = new int[global_rows][global_cols];
        // cycle through the pentominoes
        // Keeps track of which pentomino we are at
        int input_length = pentominoes.length;
        // We use this, so we can use different types of pentominoes but also if there
        // are
        // duplicates; See format line 28
        int pentominoIndex = 0;
        for (Character curr_pentomino : pentominoes) {
            int pentID = characterToID(curr_pentomino);
            // cycle through the mutations of said pentomino
            for (int j = 0; j < PentominoDatabase.data[pentID].length; j++) {
                // cycle through rows
                for (int k = 0; k < pseudoGrid.length; k++) {
                    // cycle though columns
                    for (int l = 0; l < pseudoGrid[k].length; l++) {
                        // add the pieces combination to the rows to get them ready for DLX
                        int[][] fullPiece = PentominoDatabase.data[pentID][j];
                        if (isPlacableForExample(pseudoGrid, pentID, j, k, l)) {
                            newAddPiece(pseudoGrid, fullPiece, k, l);
                            addSpecificRowToRows(pseudoGrid, pentominoIndex, k, l, j, input_length, pentID);
                            pseudoGrid = new int[global_rows][global_cols];
                        }
                    }
                }
            }
            pentominoIndex++;
        }
        // Turn List of Lists into Array with 2 ints
        return theRows.stream().map(u -> u.stream().mapToInt(i -> i).toArray()).toArray(int[][]::new);
    }

    /**
     * Get as input the character representation of a pentomino and translate it
     * into its corresponding numerical value (ID)
     * It just adds the specific ROW we just found to a group of rows. It does this
     * by
     * creating that format I specified up, creates seperately the first part {XXX}
     * and the second part {YYY}
     * then connects them as one singular row. Adds it to the hashmap and then adds
     * it to the rows.
     *
     * @param localGrid      simple local grid used to get the possible spaces
     *                       restricted by piece at position
     * @param pentominoIndex keeps track of the index of the input pentominoes
     * @param row            specific row got from above function, we use this data
     *                       for the hashmap
     * @param col            specific col got from above function, we use this data
     *                       for the hashmap
     * @param mut            mutation index
     * @param input_length   number of pentominoes inputted
     * @param pentID         pentomino ID
     *
     **/
    private static void addSpecificRowToRows(int[][] localGrid, int pentominoIndex, int row, int col, int mut,
            int input_length, int pentID) {
        ArrayList<Integer> theFirstN = new ArrayList<>();
        // Per each index put a 1 in its position so DLX can distinguish the different
        // pieces,
        // This will work even if a piece is put 2 times like {'A', 'A'}
        for (int i = 0; i < input_length; i++) {
            if (pentominoIndex == i)
                theFirstN.add(1);
            else
                theFirstN.add(0);
        }
        // keep the possible positions here
        ArrayList<Integer> positions = new ArrayList<>();
        // add the possible positions
        for (int[] ints : localGrid) {
            for (int j = 0; j < localGrid[0].length; j++) {
                positions.add(ints[j]);
            }
        }
        // Combine the first unique pentomino columns and the other position columns
        theFirstN.addAll(positions);
        // this keeps information for specific pentomino, so we can use it later.
        int[] Pentomino_Information = { pentID, mut, row, col };

        PentominoToRow.put(theFirstN, Pentomino_Information);
        theRows.add(theFirstN);
    }

    /**
     * Checks if we can place the piece at that position on the board.
     * 
     * @param pentID      pentominoe ID
     * @param current_col given column
     * @param field       field to try is piece is placable there
     * @param mutation    current mutation
     * @param current_row given row
     * @return true if we can place it, false if we cant
     */
    public static boolean isPlacableForExample(int[][] field, int pentID, int mutation, int current_row,
            int current_col) {

        int pentominoRowsIndex = PentominoDatabase.data[pentID][mutation].length;

        // assuming all the rows have same length we put [0]
        int pentominoColsIndex = PentominoDatabase.data[pentID][mutation][0].length;

        // check if there is space
        if (current_row + pentominoRowsIndex > field.length) {
            return false;
        }
        return current_col + pentominoColsIndex <= field[0].length;
    }

    /**
     * This method takes input from user then runs DLX
     */
    static void runExample() throws InterruptedException {

        // Takes row and column data from user
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input Row and Column Data");
        int row = scanner.nextInt();
        int col = scanner.nextInt();
        setGlobalRowsAndColumns(row, col);
        ui = new UI(global_rows, global_cols, 45);

        ArrayList<Character> pentomino_list = new ArrayList<>();

        // Takes pentominoes
        System.out.println("Input your pentominoes");
        System.out.println("---Type 'end' when you finish adding the pentominoes---");
        while (scanner.hasNext()) {
            String obj = scanner.next().toUpperCase();
            if (obj.equals("END"))
                break;
            char c = obj.charAt(0);
            pentomino_list.add(c);
        }
        // Turns arraylist into array
        Character[] pentominoes = new Character[pentomino_list.size()];
        for (int i = 0; i < pentomino_list.size(); i++) {
            pentominoes[i] = pentomino_list.get(i);
        }
        // F I L P N T U V W X Y Z
        // Follow this ordering
        String order = "FILPNTUVWXYZ";
        // Orders them by the order above
        Arrays.sort(pentominoes, (first, second) -> {
            for (int index = 0; index < 1; index++) {
                int charFirst = (int) (first);
                int charSecond = (int) (second);
                if (order.indexOf(charFirst) > order.indexOf(charSecond)) {
                    return 1;
                } else if (order.indexOf(charFirst) < order.indexOf(charSecond)) {
                    return -1;
                } else {
                    continue;
                }
            }
            return 0;
        });
        // if there are more blocks in the field than in the pentominoes its impossible
        // to solve
        if (pentomino_list.size() * 5 != global_cols * global_rows) {
            System.out.println("Impossible to solve, try more pentominoes?");
            System.exit(0);
        }
        // make int[][] example
        int[][] example = makeSparseMatrix(pentominoes);
        DancingLinks DLX = new DancingLinks(example);
        DLX.runSolver();
        scanner.close();
    }

    /**
     * @param character returns ID per pentomino character
     */
    public static int characterToID(char character) {
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
     * Sets the global variables
     * 
     * @param rows given rows from user
     * @param cols given cols from user
     */
    private static void setGlobalRowsAndColumns(int rows, int cols) {
        global_rows = rows;
        global_cols = cols;
    }

    /**
     * This gets the answer as used columns from the DLX per each row, so we need to
     * translate it into readable form.
     * 
     * @param answers Is an arraylist of string which includes each row given by dlx
     *                and has inside of it words which contain
     *                the columns which were activated by that specific row. So what
     *                we do is go through our options and check which option
     *                can activate those columns. This is unique because of the
     *                identifier XXXYYY.
     */
    public static void ReturnPentominoesUsed(ArrayList<String> answers) throws InterruptedException {
        // ran out of field names
        naive_field = new int[global_rows][global_cols];
        // empty it
        for (int i = 0; i < naive_field.length; i++) {
            for (int j = 0; j < naive_field[0].length; j++) {
                naive_field[i][j] = -1;
            }
        }
        // take the possible answers and turn each of them into readable user form
        for (String curr_answer : answers) {
            curr_answer = curr_answer.stripTrailing();
            String[] splitArray = curr_answer.split(" ");

            ArrayList<Integer> array_list = new ArrayList<>();

            for (String s : splitArray) {
                array_list.add(Integer.parseInt(s));
            }
            // Checking the rows now
            for (ArrayList<Integer> Row : theRows) {
                int index = 0;
                for (Integer vlera : array_list) {
                    if (Row.get(vlera) == 1) {
                        index++;
                    }
                }
                // This means that the Row had all the positions and their possible size
                // confirmed
                // So we get a unique result.
                if (index == array_list.size()) {
                    int[] pentominoInfo = PentominoToRow.get(Row);
                    int pentID = pentominoInfo[0];
                    int mutation = pentominoInfo[1];
                    int row = pentominoInfo[2];
                    int col = pentominoInfo[3];
                    // id, mut, row, col
                    int[][] full_piece = PentominoDatabase.data[pentID][mutation];
                    // add the piece to show to user
                    newAddPiece(naive_field, full_piece, pentID, row, col);
                }
            }
        }
        // last state
        ui.setState(naive_field);
        System.out.println("Congrats!");
        // holds 10s for fun
        TimeUnit.SECONDS.sleep(10);
    }

    /**
     * Adds a piece to the field, puts the pieceID at the positions on the board
     * which the piece corresponds.
     * 
     * @param field   the field to put the piece
     * @param pieceID piece id
     * @param piece   the full piece
     * @param x       row
     * @param y       col
     *
     */
    public static void newAddPiece(int[][] field, int[][] piece, int pieceID, int x, int y) {
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

    /**
     * Adds a piece to the field, puts a '1' at the positions on the board which the
     * piece corresponds.
     * 
     * @param field the field to put the piece
     * @param piece the full piece
     * @param x     row
     * @param y     col
     *
     */
    public static void newAddPiece(int[][] field, int[][] piece, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) // loop over y position of pentomino
            {
                if (piece[i][j] == 1) {
                    // Add the ID of the pentomino to the board if the pentomino occupies this
                    // square
                    field[x + i][y + j] = 1;
                }
            }
        }
    }

    /**
     * Main function. Needs to be executed to start the basic search algorithm
     */
    public static void main(String[] args) throws InterruptedException {
        runExample();
    }
}
