import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigInteger;
import org.apache.commons.lang3.tuple.Pair;

/* Compute solutions to the NQueens problem with the additional constraint that
 * no 3 queens can lie on the same straight line. The argument to main is N -
 * the number of queens and also the side length of the board.
 */
public class NQueens {

    public static void main (String[] args) {
        int nQueens = Integer.parseInt(args[0]);
        NQueens nq = new NQueens(nQueens);
        nq.boardSearch();
        for(Board solution : nq.getSolutions()) System.out.println(solution);
        System.out.println(nq.getSolutions().size() + " solutions found.");
    }

    private Set<Board> solutions;
    private int nQueens;

    public NQueens(int nQueens) {
        this.nQueens = nQueens;
        solutions = new HashSet<>();
    }

    public Set<Board> getSolutions() {
        return solutions;
    }

    public void boardSearch() {
        BoardBuilder toExplore = BoardBuilder.empty(nQueens);
        explore(0, toExplore);
    }

    /* We use recursive backtracking to find solutions. We try to place a queen
     * on the given row index. If we can't, we do nothing. If we can, we
     * recursively try to place another queen on the next row for each queen we
     * could put in the current row.
     *     If we find a board with all N queens placed, we add it to the set of
     * solutions, assuming we haven't seen it or any of its permutations
     * (rotations or reflections) already.
     */
    public void explore(int row, BoardBuilder board) {
        if(row >= nQueens) {
            List<Board> perms = board.permutations();
            perms.removeAll(solutions);
            if(perms.size() == 8) solutions.add(board.board());
        }
        else for(int column = 0; column < nQueens; column++) {
            if(!board.threatened(new Coord(row, column))) {
                BoardBuilder daughter = board.clone();
                daughter.placeQueen(new Coord(row, column));
                explore(row + 1, daughter);
            }
        }
    }
}

/* Represents chess boards. The field nQueens tells us the dimensions and number
 * of queens, and the set queenCoords stores coordinates of the placed queens.
 * Contains code for rotating and reflecting boards so that we can consider
 * rotations and reflections as equal, simplifying the printouts.
 */
class Board {

    public final int nQueens;
    public HashSet<Coord> queenCoords;

    public Board(int nQueens, HashSet<Coord> queenCoords) {
        this.queenCoords = queenCoords;
        this.nQueens = nQueens;
    }

    public int numPlacedQueens() {
        return queenCoords.size();
    }

    public boolean queenAt(Coord coord) {
        return queenCoords.contains(coord);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean white = false;
        for(int rw = 0; rw < nQueens; rw++) {
            for(int cl = 0; cl < nQueens; cl++) {
                sb.append(queenCoords.contains(new Coord(rw, cl)) ?
                        (white?"◙█":"◯ "):
                        (white?"██":"  "));
                white = !white;
            }
            if(nQueens % 2 == 0) white = !white;
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Board) {
            Board other = (Board) obj;
            return queenCoords.equals(other.queenCoords);
            /*return permutations().stream().map(board -> board.queenCoords)
                .collect(Collectors.toSet()).contains(other.queenCoords);*/
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return queenCoords.hashCode();
    }

    public Board rotate() {
        return new Board(nQueens, new HashSet(queenCoords.stream()
            .map(coord -> new Coord(nQueens - coord.column - 1, coord.row))
            .collect(Collectors.toSet())));
    }

    public Board mirror() {
        return new Board(nQueens, new HashSet(queenCoords.stream()
            .map(coord -> new Coord(nQueens - coord.row - 1, coord.column))
            .collect(Collectors.toSet())));
    }

    public List<Board> permutations() {
        List<Board> perms = new ArrayList<>();
        Board b = this;
        for(int i = 0; i < 4; i++) {
            perms.add(b);
            perms.add(b.mirror());
            b = b.rotate();
        }
        return perms;
    }
}

/* We use BoardBuilders to build boards (surprise!). Along with the coordinates,
 * we store a BitSet with one bit for every cell on the board. For any given
 * cell, its bit is 1 if it is threatened by another queen, or lies on a
 * straight line drawn by two queens in other spots. When we place a queen in
 * the board, we iterate over the row, column and diagonals of the spot we place
 * it in, and set all corresponding bits to 1, and likewise for the spots that
 * would allow 3 queens on the same straight line.
 */
class BoardBuilder extends Board {

    private BitSet bits;

    public Board board() {
        return new Board(nQueens, queenCoords);
    }

    private BoardBuilder(int nQueens, BitSet bits,
            HashSet<Coord> queenCoords) {
        super(nQueens, queenCoords);
        this.bits = bits;
    }

    public static BoardBuilder empty(int nQueens) {
        return new BoardBuilder(nQueens, new BitSet(nQueens * nQueens),
                new HashSet<>());
    }

    public BoardBuilder clone() {
        return new BoardBuilder(nQueens, (BitSet) bits.clone(),
                (HashSet<Coord>) queenCoords.clone());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean white = false;
        for(int rw = 0; rw < nQueens; rw++) {
            for(int cl = 0; cl < nQueens; cl++) {
                sb.append(queenCoords.contains(new Coord(rw, cl)) ?
                        (white ? "◙█" : "◯ ") :
                        (threatened(new Coord(rw, cl)) ? (white ? "▓▓" : "░░") :
                        (white ? "██" : "  ")));
                white = !white;
            }
            if(nQueens % 2 == 0) white = !white;
            sb.append('\n');
        }
        return sb.toString();
    }

    public boolean threatened(Coord coord) {
        return bits.get(coord.toLinearIndex(nQueens));
    }

    public boolean threatened(int bitIndex) {
        return bits.get(bitIndex);
    }

    public void setThreatened(Coord coord) {
        bits.set(coord.toLinearIndex(nQueens));
    }

    public void setThreatened(int row, int column) {
        bits.set((new Coord(row, column)).toLinearIndex(nQueens));
    }

    public Optional<Integer> nonthreatenedColumnInRow(int row) {
        int clearBit =
            bits.nextClearBit((new Coord(row, 0)).toLinearIndex(nQueens));
        if(clearBit < 0) return Optional.empty();
        Coord clearCoord = Coord.fromLinearIndex(clearBit, nQueens);
        if(clearCoord.row == row) {
            return Optional.of(clearCoord.column);
        }
        else return Optional.empty();
    }

    public void placeQueen(int bitIndex) {
        placeQueen(Coord.fromLinearIndex(bitIndex, nQueens));
    }

    public void placeQueen(Coord coord) {
        if(threatened(coord)) {
            throw new RuntimeException(
                "tried to place queen in a threatened spot");
        }
        else {
            setPlaceableBits(coord);
            queenCoords.add(coord);
        }
    }

    public Optional<Coord> openCell() {
        int clearBit = bits.nextClearBit(0);
        if (clearBit < 0) return Optional.empty();
        else return Optional.of(Coord.fromLinearIndex(clearBit, nQueens));
    }

    public void setPlaceableBits(Coord coord) {
        setThreatened(coord);
        int row = coord.row, column = coord.column;

        // Vertical
        for(int rw = 0; rw < row; rw++) setThreatened(rw, column);
        for(int rw = row + 1; rw < nQueens; rw++) setThreatened(rw, column);

        // Horizontal
        for(int cl = 0; cl < column; cl++) setThreatened(row, cl);
        for(int cl = column + 1; cl < nQueens; cl++) setThreatened(row, cl);

        // Diagonal UL -> BR
        for(int rw = row - 1, cl = column - 1; rw >= 0 && cl >= 0; rw--, cl--)
            setThreatened(rw, cl);
        for(int rw = row + 1, cl = column + 1;
            rw < nQueens && cl < nQueens; rw++, cl++) setThreatened(rw, cl);

        // Diagonal UR -> BL
        for(int rw = row - 1, cl = column + 1;
            rw >= 0 && cl < nQueens; rw--, cl++) setThreatened(rw, cl);
        for(int rw = row + 1, cl = column - 1;
                rw < nQueens && cl >= 0; rw++, cl--) setThreatened(rw, cl);

        /* For the new queen and all other queens, ensure no other queens can be
         * on the same straight line. This is done by looking at all other
         * queens, and for each, computing horizontal and vertical distance
         * away and computing the smallest vector (simplest fraction) in that
         * direction, and setting threatened all spots that are a multiple of
         * that vector away.
         */
        for(Coord otherCoord : queenCoords) {
            Coord unitDiff = coord.minus(otherCoord).simplifyAsFraction();
            Coord coordToSet = coord.minus(unitDiff);
            while(coordToSet.inBounds(this)) {
                setThreatened(coordToSet);
                coordToSet = coordToSet.minus(unitDiff);
            }
            coordToSet = coord.plus(unitDiff);
            while(coordToSet.inBounds(this)) {
                setThreatened(coordToSet);
                coordToSet = coordToSet.plus(unitDiff);
            }
        }
    }

}

// Represents Coordinates with a pair of integers. They're immutable.
class Coord {
    public final int row, column;

    public Coord(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Coord minus(Coord other) {
        return new Coord(row - other.row, column - other.column);
    }

    public Coord plus(Coord other) {
        return new Coord(row + other.row, column + other.column);
    }

    // Does the coordinate lie within the bounds of the given Board?
    public boolean inBounds(Board board) {
        return row >= 0 && column >= 0
            && row < board.nQueens && column < board.nQueens;
    }

    // Computes the greatest common divisor of the row and column.
    public int gcd() {
        return BigInteger.valueOf(row)
            .gcd(BigInteger.valueOf(column)).intValue();
    }

    // Treating the coordinate instead as a fraction i.e. row/column, compute
    // the simplest form of the fraction.
    public Coord simplifyAsFraction() {
        int gcd = gcd();
        return new Coord(row / gcd, column / gcd);
    }

    // Since we use a linear BitSet to represent cells that can be placed, we
    // sometimes need to convert 2D coordinates into 1D indexes in a linear
    // BitSet, but we need the row length (nQueens) to do this.
    public int toLinearIndex(int nQueens) {
        return row * nQueens + column;
    }

    // Go back from linear index to coordinate.
    public static Coord fromLinearIndex(int linearIndex, int nQueens) {
        return new Coord(linearIndex / nQueens, linearIndex % nQueens);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Coord) {
            Coord other = (Coord) obj;
            return row == other.row && column == other.column;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return Pair.of(row, column).hashCode();
    }

    @Override
    public String toString() {
        return "Coord(" + row + ", " + column + ")";
    }
}
