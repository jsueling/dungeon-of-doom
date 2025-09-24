public class WallTile extends Tile {

    char symbol = '#';

    public WallTile(int row, int col) {
        super(row, col);
    }

    // cannot enter wall
    boolean canEnter() {
        return false;
    }

    public void printTile() {
        System.out.print(this.symbol);
    }

}
