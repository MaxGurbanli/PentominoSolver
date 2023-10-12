import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class AlgorithmTesting {

        public static PrintWriter writer;

        // Testing parameters
        public static final TestingParameter[] parameters = new TestingParameter[] {
                        new TestingParameter(5, 6, new char[] { 'X', 'I', 'Z', 'T', 'U', 'V', }),
                        new TestingParameter(10, 12,
                                        new char[] { 'X', 'I', 'Z', 'T', 'U', 'V', 'W', 'Y', 'L', 'P', 'N', 'F' })
        };

        public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
                // Test basic search
                BasicSearch basic = new BasicSearch();

                // Test brute force algorithm
                // BacktrackingSearch backtracking = new BacktrackingSearch();

                // Test BacktrackingOptimized
                OptimizedBacktrackingSearch optimizedSearch = new OptimizedBacktrackingSearch();

                // Test BacktrackingOptimized with FloodFill optimization
                MoreOptimizedBacktrackingSearch improvedSearch = new MoreOptimizedBacktrackingSearch();

                writer = new PrintWriter("testing.csv", "UTF-8");

                for (TestingParameter param : parameters) {
                        System.out.println("Testing paramter: " + param);
                        // Set parameters
                        basic.horizontalGridSize = param.pentominoWidth;
                        basic.verticalGridSize = param.pentominoHeight;
                        basic.input = param.pentominoes;

                        optimizedSearch.horiGridSize = param.pentominoWidth;
                        optimizedSearch.vertGridSize = param.pentominoHeight;
                        optimizedSearch.input = param.pentominoes;

                        improvedSearch.horiGridSize = param.pentominoWidth;
                        improvedSearch.vertGridSize = param.pentominoHeight;
                        improvedSearch.input = param.pentominoes;

                        // Test basic
                        System.out.println("Testing basic");
                        long startTime = System.currentTimeMillis();
                        basic.search();
                        long endTime = System.currentTimeMillis();
                        writeToFile("basic", param.pentominoWidth, param.pentominoHeight, param.pentominoes.length,
                                        (endTime - startTime));

                        // Test backtracking
                        // startTime = System.currentTimeMillis();
                        // backtracking.search();
                        // endTime = System.currentTimeMillis();
                        // writeToFile("basic", param.pentominoWidth, param.pentominoHeight,
                        // param.pentominoes.length,
                        // (endTime - startTime));

                        // Test optimized
                        System.out.println("Testing Optimized");
                        startTime = System.currentTimeMillis();
                        optimizedSearch.search();
                        endTime = System.currentTimeMillis();
                        writeToFile("optimized", param.pentominoWidth, param.pentominoHeight, param.pentominoes.length,
                                        (endTime - startTime));

                        // Test improved optimized
                        System.out.println("Testing More Optimized");
                        startTime = System.currentTimeMillis();
                        improvedSearch.search();
                        endTime = System.currentTimeMillis();
                        writeToFile("improved", param.pentominoWidth, param.pentominoHeight,
                                        param.pentominoes.length,
                                        (endTime - startTime));

                }
        }

        public static void writeToFile(String algorithm, int pentominoWidth, int pentominoHeight,
                        int pentominoCount, long solvingTime) {
                writer.print(algorithm + "," + pentominoWidth + "," + pentominoHeight + "," + pentominoCount
                                + "," + solvingTime);
        }

}
