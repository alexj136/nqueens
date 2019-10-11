import java.util.BitSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public class NQueens {

    public static void main (String[] args) {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("=-=-=-=-= YAY IT COMPILES =-=-=-=-=");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        HashSet<Board> solutions = boardSearch(6);
        if(solutions.isEmpty()) System.out.println("No solutions found.");
        else for(Board solution : solutions) System.out.println(solution);
    }

    public static HashSet<Board> boardSearch(int nQueens) {
        ArrayList<BoardBuilder> toExplore = new ArrayList<>();
        HashSet<Board> solutions = new HashSet<>();
        HashSet<Board> seen = new HashSet<>();
        toExplore.add(BoardBuilder.empty(nQueens));
        while(toExplore.size() > 0) {
            BoardBuilder current = toExplore.remove(0);
            seen.add(current.board());
            if(current.numPlacedQueens() >= nQueens) {
                solutions.add(current.board());
            }
            for(int i = 0; i < Math.pow(current.nQueens, 2); i++) {
                if(!current.occupied(i)) {
                    BoardBuilder daughter = current.clone();
                    daughter.placeQueen(i);
                    if(!seen.contains(daughter)) toExplore.add(daughter);
                }
            }
        }
        return solutions;
    }
}

class Board {

    public final int nQueens;
    public HashSet<Pair<Integer, Integer>> queenCoords;

    public Board(int nQueens, HashSet<Pair<Integer, Integer>> queenCoords) {
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
                sb.append(queenCoords.contains(Pair.of(rw, cl)) ?
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
        if(obj == null || obj instanceof Board) {
            Board other = (Board) obj;
            Board _90 = rotate();
            Board _180 = _90.rotate();
            Board _270 = _180.rotate();
            return other.nQueens == nQueens && (
                    other.queenCoords.equals(queenCoords) ||
                    other.queenCoords.equals(mirror().queenCoords) ||
                    other.queenCoords.equals(_90.queenCoords) ||
                    other.queenCoords.equals(_90.mirror().queenCoords) ||
                    other.queenCoords.equals(_180.queenCoords) ||
                    other.queenCoords.equals(_180.mirror().queenCoords) ||
                    other.queenCoords.equals(_270.queenCoords) ||
                    other.queenCoords.equals(_270.mirror().queenCoords));
        }
        else return false;
    }

    @Override
    public int hashCode() {
        Board board = this;
        int[] codes = new int[8];
        for(int i = 0; i < 4; i++) {
            codes[2 * i] = board.queenCoords.hashCode();
            codes[(2 * i) + 1] = board.mirror().queenCoords.hashCode();
            board = board.rotate();
        }
        int minCode = codes[0];
        for(int i = 1; i < 8; i++) {
            if(codes[i] < minCode) minCode = codes[i];
        }
        return minCode;
    }

    public Board rotate() {
        HashSet<Pair<Integer, Integer>> newCoords = new HashSet<>();
        for(Pair<Integer, Integer> coord : queenCoords) {
            newCoords.add(Pair.of(
                    nQueens - coord.getRight() - 1, coord.getLeft()));
        }
        return new Board(nQueens, newCoords);
    }

    public Board mirror() {
        HashSet<Pair<Integer, Integer>> newCoords = new HashSet<>();
        for(Pair<Integer, Integer> coord : queenCoords) {
            newCoords.add(Pair.of(
                    nQueens - coord.getLeft() - 1, coord.getRight()));
        }
        return new Board(nQueens, newCoords);
    }

    protected int coordToLinearIndex(Pair<Integer, Integer> coord) {
        return coord.getLeft() * nQueens + coord.getRight();
    }

    protected Pair<Integer, Integer> linearIndexToCoord(int linearIndex) {
        return Pair.of(linearIndex / nQueens, linearIndex % nQueens);
    }
}

class BoardBuilder extends Board {

    private BitSet bits;

    public Board board() {
        return new Board(nQueens, queenCoords);
    }

    private BoardBuilder(int nQueens, BitSet bits,
            HashSet<Pair<Integer, Integer>> queenCoords) {
        super(nQueens, queenCoords);
        this.bits = bits;
    }

    public static BoardBuilder empty(int nQueens) {
        return new BoardBuilder(nQueens, new BitSet(nQueens * nQueens),
                new HashSet<>());
    }

    public BoardBuilder clone() {
        return new BoardBuilder(nQueens, (BitSet) bits.clone(),
                (HashSet<Pair<Integer, Integer>>) queenCoords.clone());
    }

    public boolean occupied(Pair<Integer, Integer> coord) {
        return bits.get(coordToLinearIndex(coord));
    }

    public boolean occupied(int bitIndex) {
        return bits.get(bitIndex);
    }

    public void setOccupied(Pair<Integer, Integer> coord) {
        bits.set(coordToLinearIndex(coord));
    }

    public void setOccupied(int row, int column) {
        bits.set(coordToLinearIndex(Pair.of(row, column)));
    }

    public void placeQueen(int bitIndex) {
        placeQueen(linearIndexToCoord(bitIndex));
    }

    public void placeQueen(Pair<Integer, Integer> coord) {
        if(occupied(coord)) {
            throw new RuntimeException(
                "Tried to place queen in spot marked as illegal");
        }
        else {
            setPlaceableBits(coord);
            queenCoords.add(coord);
        }
    }

    public Optional<Pair<Integer, Integer>> openCell() {
        int clearBit = bits.nextClearBit(0);
        if (clearBit < 0) return Optional.empty();
        else return Optional.of(linearIndexToCoord(clearBit));
    }

    public void setPlaceableBits(Pair<Integer, Integer> coord) {
        setOccupied(coord);
        int row = coord.getLeft(), column = coord.getRight();

        // Vertical
        for(int rw = 0; rw < row; rw++) setOccupied(rw, column);
        for(int rw = row + 1; rw < nQueens; rw++) setOccupied(rw, column);

        // Horizontal
        for(int cl = 0; cl < column; cl++) setOccupied(row, cl);
        for(int cl = column + 1; cl < nQueens; cl++) setOccupied(row, cl);

        // Diagonal UL -> BR
        for(int rw = row - 1, cl = column - 1; rw >= 0 && cl >= 0; rw--, cl--)
            setOccupied(rw, cl);
        for(int rw = row + 1, cl = column + 1;
            rw < nQueens && cl < nQueens; rw++, cl++) setOccupied(rw, cl);

        // Diagonal UR -> BL
        for(int rw = row - 1, cl = column + 1;
            rw >= 0 && cl < nQueens; rw--, cl++) setOccupied(rw, cl);
        for(int rw = row + 1, cl = column - 1;
                rw < nQueens && cl >= 0; rw++, cl--) setOccupied(rw, cl);

        /*for(Pair<Integer, Integer> coord : queenCoords) {
            int rw = coord.getLeft(), cl = coord.getRight();
        }

        throw new RuntimeException("Not yet implemented");*/
    }
}
