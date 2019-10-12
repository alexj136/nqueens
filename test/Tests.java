import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class Tests {

    @Test
    public void correctCounts() {
        NQueens nq = new NQueens(1);
        nq.boardSearch();
        assertEquals(1, nq.getSolutions().size(), "1 solution for 1x1");

        nq = new NQueens(2);
        nq.boardSearch();
        assertEquals(0, nq.getSolutions().size(), "0 solutions for 2x2");

        nq = new NQueens(3);
        nq.boardSearch();
        assertEquals(0, nq.getSolutions().size(), "0 solutions for 3x3");

        nq = new NQueens(4);
        nq.boardSearch();
        assertEquals(1, nq.getSolutions().size(), "1 solution for 4x4");
    }
}
