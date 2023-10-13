import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SearchAlgorithm {
    public int horizontalGridSize;
    public int verticalGridSize;
    public char[] input;
    public UI ui;

    public SearchAlgorithm(int gridWidth, int gridHeight) {
    };

    public SearchAlgorithm(int gridWidth, int gridHeight, char[] input) {
    };

    public SearchAlgorithm(int gridWidth, int gridHeight, char[] input, UI ui) {
    };

    public void getUserInput() {
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
}
