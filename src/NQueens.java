import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigInteger;
import org.apache.commons.lang3.tuple.Pair;

public class NQueens {

    public static BoardSet solutions = new BoardSet();

    public static void main (String[] args) {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("=-=-=-=-= YAY IT COMPILES =-=-=-=-=");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        boardSearch(8);
        if(solutions.isEmpty()) System.out.println("No solutions found.");
        else for(Board solution : solutions) System.out.println(solution);
    }

    public static void boardSearch(int nQueens) {
        BoardBuilder toExplore = BoardBuilder.empty(nQueens);
        explore(0, nQueens, toExplore);
    }

    public static void explore(int row, int nQueens, BoardBuilder board) {
        if(row >= nQueens) {
            solutions.add(board.board());
        }
        else for(int column = 0; column < nQueens; column++) {
            if(!board.threatened(new Coord(row, column))) {
                BoardBuilder daughter = board.clone();
                daughter.placeQueen(new Coord(row, column));
                explore(row + 1, nQueens, daughter);
            }
        }
    }
}

class Board {

    public final int nQueens;
    public HashSet<Coord> queenCoords;

    public Board(int nQueens, HashSet<Coord> queenCoords) {
        this.queenCoords = queenCoords;
        this.nQueens = nQueens;
    }

    public Board(int nQueens, HashSet<Coord> queenCoords,
            Optional<Integer> hashCode,
            Optional<HashSet<Coord>> minHashCodePerm) {
        this.queenCoords = queenCoords;
        this.nQueens = nQueens;
    }

    public int numPlacedQueens() {
        return queenCoords.size();
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

    public ArrayList<Board> permutations() {
        ArrayList<Board> perms = new ArrayList<>();
        Board b = this;
        for(int i = 0; i < 4; i++) {
            perms.add(b);
            perms.add(b.mirror());
            b = b.rotate();
        }
        return perms;
    }

    protected int coordToLinearIndex(Coord coord) {
        return coord.row * nQueens + coord.column;
    }

    protected Coord linearIndexToCoord(int linearIndex) {
        return new Coord(linearIndex / nQueens, linearIndex % nQueens);
    }
}

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
        return bits.get(coordToLinearIndex(coord));
    }

    public boolean threatened(int bitIndex) {
        return bits.get(bitIndex);
    }

    public void setThreatened(Coord coord) {
        bits.set(coordToLinearIndex(coord));
    }

    public void setThreatened(int row, int column) {
        bits.set(coordToLinearIndex(new Coord(row, column)));
    }

    public Optional<Integer> nonthreatenedColumnInRow(int row) {
        int clearBit = bits.nextClearBit(coordToLinearIndex(new Coord(row, 0)));
        if(clearBit < 0) return Optional.empty();
        Coord clearCoord = linearIndexToCoord(clearBit);
        if(clearCoord.row == row) {
            return Optional.of(clearCoord.column);
        }
        else return Optional.empty();
    }

    public void placeQueen(int bitIndex) {
        placeQueen(linearIndexToCoord(bitIndex));
    }

    public void placeQueen(Coord coord) {
        if(threatened(coord)) {
            throw new RuntimeException(
                "Tried to place queen in spot marked as illegal");
        }
        else {
            setPlaceableBits(coord);
            queenCoords.add(coord);
        }
    }

    public Optional<Coord> openCell() {
        int clearBit = bits.nextClearBit(0);
        if (clearBit < 0) return Optional.empty();
        else return Optional.of(linearIndexToCoord(clearBit));
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

    public boolean inBounds(Board board) {
        return row >= 0 && column >= 0
            && row < board.nQueens && column < board.nQueens;
    }

    public int gcd() {
        return BigInteger.valueOf(row)
            .gcd(BigInteger.valueOf(column)).intValue();
    }

    public Coord simplifyAsFraction() {
        int gcd = gcd();
        return new Coord(row / gcd, column / gcd);
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
}

class BoardSet extends HashSet<Board> {
    @Override
    public boolean add(Board board) {
        List<Board> permutations = board.permutations();
        int minHashCode = permutations.get(0).hashCode();
        int minHashIdx = 0;
        for(int i = 1; i < permutations.size(); i++) {
            int hashCode = permutations.get(i).hashCode();
            if(hashCode < minHashCode) {
                minHashCode = hashCode;
                minHashIdx = i;
            }
        }
        return super.add(permutations.get(minHashIdx));
    }
}
