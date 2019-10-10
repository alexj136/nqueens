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
        boardSearch(5);
    }

    public static void boardSearch(int nQueens) {
        ArrayList<Board> toExplore = new ArrayList<>();
        ArrayList<Board> solutions = new ArrayList<>();
        HashSet<Board> seen = new HashSet<>();
        toExplore.add(Board.empty(nQueens));
        while(toExplore.size() > 0) {
            Board current = toExplore.remove(0);
            seen.add(current);
            if(current.placedQueens() >= nQueens) {
                solutions.add(current);
                System.out.println(current);
            }
            for(int i = 0; i < Math.pow(current.nQueens, 2); i++) {
                if(current.occupied(i)) {}
                else {
                    Board daughter = current.clone();
                    daughter.placeQueen(i);
                    if(!seen.contains(daughter)) toExplore.add(daughter);
                }
            }
        }
    }
}

class Board {

    private BitSet bits;
    private ArrayList<Pair<Integer, Integer>> queenCoords;
    public final int nQueens;

    private Board(int nQueens, BitSet bits,
            ArrayList<Pair<Integer, Integer>> queenCoords) {
        this.queenCoords = queenCoords;
        this.nQueens = nQueens;
        this.bits = bits;
    }

    public static Board empty(int nQueens) {
        return new Board(nQueens, new BitSet(nQueens * nQueens),
                new ArrayList<>());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean white = false;
        for(int rw = 0; rw < nQueens; rw++) {
            for(int cl = 0; cl < nQueens; cl++) {
                sb.append(queenCoords.contains(Pair.of(rw, cl)) ?
                        (white?'◯':'◙'):
                        (white?' ':'█'));
                white = !white;
            }
            if(nQueens % 2 == 0) white = !white;
            sb.append('\n');
        }
        return sb.toString();
    }

    public Board clone() {
        return new Board(nQueens, (BitSet) bits.clone(),
                (ArrayList<Pair<Integer, Integer>>) queenCoords.clone());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj instanceof Board) {
            Board other = (Board) obj;
            return other.nQueens == nQueens &&
                other.queenCoords.equals(queenCoords);
        }
        else return false;
    }

    public int placedQueens() {
        return queenCoords.size();
    }

    private int coordToLinearIndex(Pair<Integer, Integer> coord) {
        return coord.getLeft() * nQueens + coord.getRight();
    }

    private Pair<Integer, Integer> linearIndexToCoord(int linearIndex) {
        return Pair.of(linearIndex / nQueens, linearIndex % nQueens);
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
