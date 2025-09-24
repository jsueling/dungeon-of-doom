public class ExitTile extends Tile {

    char symbol = 'E';

    public ExitTile(int row, int col) {
        super(row, col);
    }

    boolean canEnter() {
        return true;
    }

    public void printTile() {
        System.out.print(this.symbol);
    }

}