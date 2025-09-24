public class EmptyTile extends Tile {

    char symbol = '.';

    public EmptyTile(int row, int col) {
        super(row, col);
    }

    boolean canEnter() {
        return true;
    }

    public void printTile() {
        System.out.print(this.symbol);
    }
}
