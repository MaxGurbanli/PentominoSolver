package dancinglinks;

/**
 * @author Heavely inspired by many different sources,
 * that said, sources are found in the gitlab README.
 */
import java.util.ArrayList;
import java.util.List;

public interface SolutionPrinter {
    void handleSolution(List<DancingLinks.DancingNode> solution, long start1) throws InterruptedException;
}

class DefaultPrinter implements SolutionPrinter {
    public void handleSolution(List<DancingLinks.DancingNode> answer, long start1) throws InterruptedException {
        long end1 = System.currentTimeMillis();
        System.out.println("Elapsed Time in milli seconds: " + (end1 - start1));

        ArrayList<String> the_answers = new ArrayList<>();
        // Takes each node and gets the column name
        // i get this string of names of columns and add it to the answers
        for (DancingLinks.DancingNode n : answer) {
            StringBuilder ret = new StringBuilder();
            ret.append(n.columnNode.name).append(" ");
            DancingLinks.DancingNode tmp = n.Right;
            while (tmp != n) {
                ret.append(tmp.columnNode.name).append(" ");
                tmp = tmp.Right;
            }
            the_answers.add(ret.toString());
        }
        // push the answers here to digest them
        Example.ReturnPentominoesUsed(the_answers);
        System.exit(0);
    }
}
