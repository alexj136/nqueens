import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

/* Compute all solutions for N up to 13 inclusive. Check that there are the 
 * correct number of solutions (for values of N that I could find oracles for).
 * For all solutions, check all the important properties:
 *     - Exactly one queen per row and column
 *     - No more than one queen per diagonal
 *     - No more than two queens per straight line
 *     - N queens are present
 */
public class Tests {

    static List<Set<Board>> solutionsPerN = new ArrayList<>();

    @BeforeAll
    public static void computeSolutions() {
        for(int nQueens = 0; nQueens < 14; nQueens++) {
            NQueens nq = new NQueens(nQueens);
            nq.boardSearch();
            solutionsPerN.add(nq.getSolutions());
        }
    }

    @Test
    public void correctNumbersOfSolutionsForSmallValuesOfN() {
        int[] counts = {1, 1, 0, 0, 1, 0, 0, 0, 1};
        for(int i = 0; i < counts.length; i++) {
            int solutions = solutionsPerN.get(i).size();
            assertEquals(counts[i], solutions,
                    solutions + " solutions for " + i + "x" + i);
        }
    }

    @Test
    public void alwaysOneQueenOnEveryRowAndColumn() {
        for(int nQueens = 0; nQueens < solutionsPerN.size(); nQueens++) {
            Set<Board> solutions = solutionsPerN.get(nQueens);
            for(Board board : solutions) {
                for(int row = 0; row < nQueens; row++) {
                    int queenCount = 0;
                    for(Coord coord : board.queenCoords) {
                        queenCount += coord.row == row ? 1 : 0;
                    }
                    assertEquals(queenCount, 1, "rows have exactly 1 queen");
                }
                for(int column = 0; column < nQueens; column++) {
                    int queenCount = 0;
                    for(Coord coord : board.queenCoords) {
                        queenCount += coord.column == column ? 1 : 0;
                    }
                    assertEquals(queenCount, 1, "columns have exactly 1 queen");
                }
            }
        }
    }

    @Test
    public void atMostOneQueenOnEveryDiagonal() {
        for(int nQueens = 0; nQueens < solutionsPerN.size(); nQueens++) {

            List<Coord> rightStartingPoints = new ArrayList<>();
            for(int i = 0; i < nQueens; i++)
                rightStartingPoints.add(new Coord(0, i));
            for(int i = 1; i < nQueens; i++)
                rightStartingPoints.add(new Coord(i, 0));

            List<Coord> leftStartingPoints = new ArrayList<>();
            for(int i = 0; i < nQueens; i++)
                leftStartingPoints.add(new Coord(0, i));
            for(int i = 1; i < nQueens; i++)
                leftStartingPoints.add(new Coord(i, nQueens - 1));

            Set<Board> solutions = solutionsPerN.get(nQueens);
            for(Board board : solutions) {
                for(Coord startingPoint : rightStartingPoints) {
                    int queenCount = 0;
                    Coord current = startingPoint;
                    while(current.inBounds(board)) {
                        queenCount += board.queenAt(current) ? 1 : 0;
                        current = current.plus(new Coord(1, 1));
                    }
                    assertTrue(queenCount <= 1, "one queen max on diagonal");
                }
                for(Coord startingPoint : leftStartingPoints) {
                    int queenCount = 0;
                    Coord current = startingPoint;
                    while(current.inBounds(board)) {
                        queenCount += board.queenAt(current) ? 1 : 0;
                        current = current.plus(new Coord(1, -1));
                    }
                    assertTrue(queenCount <= 1, "one queen max on diagonal");
                }
            }
        }
    }

    @Test
    public void noMoreThanTwoOnALine() {
        for(int nQueens = 0; nQueens < solutionsPerN.size(); nQueens++) {
            Set<Board> solutions = solutionsPerN.get(nQueens);
            for(Board board : solutions) {
                List<Coord> coords = new ArrayList<>(board.queenCoords);
                for(int coord1Idx = 0; coord1Idx < nQueens; coord1Idx++) {
                    for(int coord2Idx = coord1Idx + 1; coord2Idx < nQueens;
                            coord2Idx++) {
                        Coord coord1 = coords.get(coord1Idx);
                        Coord coord2 = coords.get(coord2Idx);

                        Coord unitDiff =
                            coord1.minus(coord2).simplifyAsFraction();
                        Coord coordToCheck = coord1.minus(unitDiff);
                        while(coordToCheck.inBounds(board)) {
                            if(!coordToCheck.equals(coord2)) {
                                assertFalse(board.queenAt(coordToCheck),
                                        "no more than two queens on a line");
                            }
                            coordToCheck = coordToCheck.minus(unitDiff);
                        }
                        coordToCheck = coord1.plus(unitDiff);
                        while(coordToCheck.inBounds(board)) {
                            if(!coordToCheck.equals(coord2)) {
                                assertFalse(board.queenAt(coordToCheck),
                                        "no more than two queens on a line");
                            }
                            coordToCheck = coordToCheck.plus(unitDiff);
                        }

                    }
                }
            }
        }
    }

    @Test
    public void nQueensPresent() {
        for(int nQueens = 0; nQueens < solutionsPerN.size(); nQueens++) {
            Set<Board> solutions = solutionsPerN.get(nQueens);
            for(Board board : solutions) {
                assertEquals(nQueens, board.queenCoords.size(),
                        nQueens + "x" + nQueens + " solutions have " +
                        board.queenCoords.size() + " queens");
            }
        }
    }
}
