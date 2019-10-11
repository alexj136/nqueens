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
        /*HashSet<Board> solutions =*/ boardSearch(8);
        //if(solutions.isEmpty()) System.out.println("No solutions found.");
        //else for(Board solution : solutions) System.out.println(solution);
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
                if(solutions.add(current.board())) System.out.println(current);
            }
            for(int i = 0; i < Math.pow(current.nQueens, 2); i++) {
                if(!current.threatened(i)) {
                    BoardBuilder daughter = current.clone();
                    daughter.placeQueen(i);
                    if(!seen.contains(daughter.board())) {
                        toExplore.add(daughter);
                    }
                }
            }
        }
        return solutions;
    }
}

class Board {

    public final int nQueens;
    public HashSet<Pair<Integer, Integer>> queenCoords;
    protected Optional<Integer> hashCode = Optional.empty();
    protected Optional<HashSet<Pair<Integer, Integer>>> minHashCodePerm =
        Optional.empty();

    public Board(int nQueens, HashSet<Pair<Integer, Integer>> queenCoords) {
        this.queenCoords = queenCoords;
        this.nQueens = nQueens;
    }

    public Board(int nQueens, HashSet<Pair<Integer, Integer>> queenCoords,
            Optional<Integer> hashCode,
            Optional<HashSet<Pair<Integer, Integer>>> minHashCodePerm) {
        this.queenCoords = queenCoords;
        this.nQueens = nQueens;
        this.hashCode = hashCode;
        this.minHashCodePerm = minHashCodePerm;
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
        if(obj != null && obj instanceof Board) {
            Board other = (Board) obj;
            computeHashCodeAndMinPerm();
            other.computeHashCodeAndMinPerm();
            return minHashCodePerm.get().equals(other.minHashCodePerm.get());
        }
        else return false;
    }

    @Override
    public int hashCode() {
        computeHashCodeAndMinPerm();
        return hashCode.get();
    }

    private void computeHashCodeAndMinPerm() {
        if(hashCode.isPresent()) return;
        Board board = this;
        Board[] boards = new Board[8];
        for(int i = 0; i < 4; i++) {
            boards[2 * i] = board;
            boards[(2 * i) + 1] = board.mirror();
            board = board.rotate();
        }
        int minHashCode = boards[0].queenCoords.hashCode();
        int minHashCodeIdx = 0;
        for(int i = 1; i < 8; i++) {
            int nextHashCode = boards[i].queenCoords.hashCode();
            if(nextHashCode < minHashCode) {
                minHashCode = nextHashCode;
                minHashCodeIdx = i;
            }
        }
        hashCode = Optional.of(minHashCode);
        minHashCodePerm = Optional.of(boards[minHashCodeIdx].queenCoords);
        return;
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
        return new Board(nQueens, queenCoords, hashCode, minHashCodePerm);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean white = false;
        for(int rw = 0; rw < nQueens; rw++) {
            for(int cl = 0; cl < nQueens; cl++) {
                sb.append(queenCoords.contains(Pair.of(rw, cl)) ?
                        (white ? "◙█" : "◯ ") :
                        (threatened(Pair.of(rw, cl)) ? (white ? "▓▓" : "░░") :
                        (white ? "██" : "  ")));
                white = !white;
            }
            if(nQueens % 2 == 0) white = !white;
            sb.append('\n');
        }
        return sb.toString();
    }

    public boolean threatened(Pair<Integer, Integer> coord) {
        return bits.get(coordToLinearIndex(coord));
    }

    public boolean threatened(int bitIndex) {
        return bits.get(bitIndex);
    }

    public void setThreatened(Pair<Integer, Integer> coord) {
        bits.set(coordToLinearIndex(coord));
    }

    public void setThreatened(int row, int column) {
        bits.set(coordToLinearIndex(Pair.of(row, column)));
    }

    public void placeQueen(int bitIndex) {
        placeQueen(linearIndexToCoord(bitIndex));
    }

    public void placeQueen(Pair<Integer, Integer> coord) {
        if(threatened(coord)) {
            throw new RuntimeException(
                "Tried to place queen in spot marked as illegal");
        }
        else {
            setPlaceableBits(coord);
            queenCoords.add(coord);
            hashCode = Optional.empty();
            minHashCodePerm = Optional.empty();
        }
    }

    public Optional<Pair<Integer, Integer>> openCell() {
        int clearBit = bits.nextClearBit(0);
        if (clearBit < 0) return Optional.empty();
        else return Optional.of(linearIndexToCoord(clearBit));
    }

    public void setPlaceableBits(Pair<Integer, Integer> coord) {
        setThreatened(coord);
        int row = coord.getLeft(), column = coord.getRight();

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

        /*for(Pair<Integer, Integer> coord : queenCoords) {
            int rw = coord.getLeft(), cl = coord.getRight();
        }

        throw new RuntimeException("Not yet implemented");*/
    }
}
