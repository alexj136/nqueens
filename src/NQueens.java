public class NQueens {

    public static void main (String[] args) {
        System.out.println("Hello Gradle");
        for(int i = 0; i < 10; i++) {
            System.out.println(Board.empty(i).nQueens());
        }
    }
}

class Board {
    private java.util.BitSet bits;
    public int nQueens() {
        return (int) Math.sqrt(bits.length());
    }
    private Board(java.util.BitSet bits) {
        this.bits = bits;
    }
    public static Board empty(int nQueens) {
        return new Board(new java.util.BitSet(nQueens * nQueens));
    }
}
