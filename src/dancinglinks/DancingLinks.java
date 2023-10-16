package dancinglinks;

/**
 * @author Heavely inspired by many different sources,
 * that said, sources are found in the gitlab README.
 */
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class DancingLinks {
    static long start1;
    private final ColumnNode header;
    private final SolutionPrinter handler;
    private List<DancingNode> answer;

    class DancingNode {
        DancingNode Left, Right, Up, Down;
        ColumnNode columnNode;

        // Takes node's bottom then makes it own currents down.
        // make the node up from this node to the node in question
        // make the nodes up, this node. then returns it.
        DancingNode ConnectRight(DancingNode node) {
            node.Right = this.Right;
            node.Right.Left = node;
            node.Left = this;
            this.Right = node;
            return node;
        }

        void ConnectDown(DancingNode node) {
            node.Down = this.Down;
            node.Down.Up = node;
            node.Up = this;
            this.Down = node;
        }

        void unlinkLR() {
            this.Left.Right = this.Right;
            this.Right.Left = this.Left;
        }

        void relinkLR() {
            this.Left.Right = this.Right.Left = this;
        }

        void unlinkUD() {
            this.Up.Down = this.Down;
            this.Down.Up = this.Up;
        }

        void relinkUD() {
            this.Up.Down = this.Down.Up = this;
        }

        public DancingNode() {
            Left = Right = Up = Down = this;
        }

        public DancingNode(ColumnNode c) {
            this();
            columnNode = c;
        }
    }

    class ColumnNode extends DancingNode {
        // number of ones in column
        int size;
        String name;

        public ColumnNode(String n) {
            super();
            size = 0;
            name = n;
            columnNode = this;
        }

        void cover() {
            unlinkLR();
            for (DancingNode i = this.Down; i != this; i = i.Down) {
                for (DancingNode j = i.Right; j != i; j = j.Right) {
                    j.unlinkUD();
                    j.columnNode.size--;
                }
            }
        }

        void uncover() {
            for (DancingNode i = this.Up; i != this; i = i.Up) {
                for (DancingNode j = i.Left; j != i; j = j.Left) {
                    j.columnNode.size++;
                    j.relinkUD();
                }
            }
            relinkLR();
        }
    }

    private void search(int k) throws InterruptedException {
        // if we removed all the columns that means we found a solution
        if (header.Right == header) {
            handler.handleSolution(answer, start1);
        } else {
            ColumnNode c = selectColumnNodeSmallestSize();
            c.cover();

            for (DancingNode r = c.Down; r != c; r = r.Down) {
                answer.add(r);

                for (DancingNode j = r.Right; j != r; j = j.Right) {
                    j.columnNode.cover();
                }

                search(k + 1);

                r = answer.remove(answer.size() - 1);
                c = r.columnNode;

                for (DancingNode j = r.Left; j != r; j = j.Left) {
                    j.columnNode.uncover();
                }
            }
            c.uncover();
        }
    }

    // Selects the column node by taking the smalled sized one
    private ColumnNode selectColumnNodeSmallestSize() {
        int min = Integer.MAX_VALUE;
        ColumnNode ret = null;
        for (ColumnNode c = (ColumnNode) header.Right; c != header; c = (ColumnNode) c.Right) {
            if (c.size < min) {
                min = c.size;
                ret = c;
            }
        }
        return ret;
    }

    // Makes a grid of 0s and 1s, so we can solve for it.
    // it returns the root column header node
    private ColumnNode makeDLXBoard(int[][] grid) {
        final int COLS = grid[0].length;

        ColumnNode headerNode = new ColumnNode("header");
        ArrayList<ColumnNode> columnNodes = new ArrayList<>();

        for (int i = 0; i < COLS; i++) {
            ColumnNode n = new ColumnNode(Integer.toString(i));
            columnNodes.add(n);
            headerNode = (ColumnNode) headerNode.ConnectRight(n);
        }
        headerNode = headerNode.Right.columnNode;

        for (int[] ints : grid) {
            DancingNode prev = null;
            for (int j = 0; j < COLS; j++) {
                if (ints[j] == 1) {
                    ColumnNode col = columnNodes.get(j);
                    DancingNode newNode = new DancingNode(col);
                    if (prev == null)
                        prev = newNode;
                    col.Up.ConnectDown(newNode);
                    prev = prev.ConnectRight(newNode);
                    col.size++;
                }
            }
        }

        headerNode.size = COLS;

        return headerNode;
    }

    // Constructor for setting up the grid
    public DancingLinks(int[][] grid) {
        this(grid, new DefaultPrinter());
    }

    // Constructor for setting up the header and handler
    public DancingLinks(int[][] grid, SolutionPrinter h) {
        header = makeDLXBoard(grid);
        handler = h;
    }

    public void runSolver() throws InterruptedException {
        answer = new LinkedList<>();
        start1 = System.currentTimeMillis();
        search(0);
        System.out.println("Sorry, couldn't find a solution! :( ");
        System.exit(0);
    }
}
