import java.io.*;
import java.util.*;

public class Map {

    // 2D grid composed of Tile objects
    final private ArrayList<ArrayList<Tile>> grid = new ArrayList<>();
    // the win condition of the map
    private int goldWinCondition;
    // current gold on the map
    private int goldCount;
    // used for reading the file input stream
    private BufferedReader br;
    private final Random rand = new Random();
    private String mapName;

    // reads file to create map when instantiated
    public Map(String filePath) {
        // Since the program is quite small, I think it's acceptable to couple file reading and Map
        // instantiation here
        this.openFileStream(filePath);
        this.initialiseMap();
        this.closeFileStream();
    }

    // Attempts to open file reader stream
    private void openFileStream(String filePath) {
        try {
            this.br = new BufferedReader(new FileReader(filePath));
            // shouldn't throw unless file was changed since MapLoader checked
        } catch (FileNotFoundException e) {
            // Cannot continue without reading the map, terminate by throwing a runtime exception
            throw new RuntimeException("There was an error reading the map file.");
        }
    }

    // Attempts to close file reader stream
    private void closeFileStream() {
        try {
            this.br.close();
        } catch (IOException e) {
            System.err.println("There was an error closing the file reader stream.");
        }
    }

    // reads the map file, composes the 2D grid with Tile objects and adds gold to tiles
    private void initialiseMap() {

        // 1. Get the map name
        try {

            // read first line
            String firstLine = this.br.readLine();
            // assumes the first line will always be in the form: 'name X'
            this.mapName = firstLine.substring(5);

        } catch (IOException e) {
            System.err.println("There was an error reading the map name.");
        }

        // 2. Get gold win condition
        try {

            // read second line
            String goldWinConditionString = this.br.readLine();
            // assumes the second line will always be in the form: 'win X'
            this.goldWinCondition = Integer.parseInt(goldWinConditionString.substring(4));

        } catch (IOException e) {
            System.err.println("There was an error reading the gold win condition of the map.");
        } catch (NumberFormatException e) {
            System.err.println("The gold win condition must be a number.");
        }

        // tracks the current row index of the map
        int rowIndex = 0;

        String row;

        // 3. Composes grid with rows of instantiated Tile objects
        try {

            // reads all other lines
            while (this.br.ready()) {

                // contains the current line from the file
                row = this.br.readLine();

                // stores tiles of this row
                ArrayList<Tile> tileRow = new ArrayList<>();

                // iterate over columns of the row
                for (int colIndex = 0; colIndex < row.length(); colIndex++) {

                    // character symbol
                    char symbol = row.charAt(colIndex);

                    // Wall
                    if (symbol == '#') {

                        tileRow.add(new WallTile(rowIndex, colIndex));

                        // Empty or Gold
                    } else if (symbol == '.' || symbol == 'G') {

                        EmptyTile et = new EmptyTile(rowIndex, colIndex);

                        // I chose to have gold tiles as EmptyTiles that have gold on top of them
                        // instead of converting between GoldTile and EmptyTile when gold is
                        // picked up or spawned in
                        if (symbol == 'G') {
                            // increment gold count of the map
                            this.incrementGoldCount();
                            // add gold to the empty tile
                            et.addGold();
                        }

                        tileRow.add(et);

                        // Exit
                    } else if (row.charAt(colIndex) == 'E') {

                        tileRow.add(new ExitTile(rowIndex, colIndex));

                        // Unknown character
                    } else {
                        // should be unreachable
                        System.err.printf("Found unexpected character '%c' at row %d column %d," +
                                " placing empty tile\n", row.charAt(colIndex), rowIndex, colIndex);
                        // Default to adding empty tile since it is the least likely to cause problems
                        EmptyTile et = new EmptyTile(rowIndex, colIndex);
                        tileRow.add(et);
                    }
                }

                rowIndex++;

                // append this row of Tiles to the 2D grid
                this.grid.add(tileRow);
            }
        } catch (IOException e) {
            System.err.println("There was an error reading the rows of the map.");
        }
    }

    public void describeMap() {
        System.out.println("The name of the map is: " + this.getMapName() + ".");
        System.out.println("To win this map you must pick up " + this.getGoldWinCondition() + " gold.");
    }

    // prints 5 by 5 grid centered on input parameters rowCenter, colCenter
    public void print5by5center(int rowCenter, int colCenter) {

        for (int r = rowCenter - 2; r <= rowCenter + 2; r++) {
            for (int c = colCenter - 2; c <= colCenter + 2; c++) {

                // if position is out of bounds of the grid, print wall
                if (this.isOutOfBounds(r, c)) {
                    System.out.print('#');
                    // position is within the grid, call the print method of the tile there
                } else {
                    Tile t = this.getTile(r, c);
                    t.print();
                }

            }
            // print newline after each row
            System.out.println();
        }
    }

    // used for testing purposes, to show the entire 2D grid
    public void print() {
        for (ArrayList<Tile> row : this.grid) {
            for (Tile t : row) {
                t.print();
            }
            System.out.println();
        }
    }

    // get the tile object in the grid at this position (at row, col)
    public Tile getTile(int row, int col) {
        if (this.isOutOfBounds(row, col)) {
            // should be unreachable, default to returning null
            System.err.println("You tried to get a Tile at " + row + " column " + col + " which is out of bounds.");
            return null;
        } else {
            return this.grid.get(row).get(col);
        }
    }

    // is this position (at row, col) in the grid out of bounds?
    public boolean isOutOfBounds(int row, int col) {
        if (this.grid.isEmpty()) { // edge case with no rows
            return false;
        }
        return (row < 0 || row >= this.grid.size() || col < 0 || col >= this.grid.get(0).size());
    }

    // can the player move to the tile at this position (at row, col)?
    public boolean playerCanMoveTo(int row, int col) {
        // grid bounds check
        if (this.isOutOfBounds(row, col)) {
            return false;
        }
        // the tile at the new proposed location
        Tile t = this.getTile(row, col);
        // the player is free to move to all tiles unless it is a wall
        return t.canEnter();
    }

    // spawns gold at a random tile in the grid
    public void spawnRandomGold() {
        int row;
        int col;
        Tile randomTile;
        do {
            // get a random position in the grid
            row = this.rand.nextInt(this.getRows());
            col = this.rand.nextInt(this.getColumns());
            // get the tile from that random position
            randomTile = this.getTile(row, col);
            // check whether gold can spawn on this tile
        } while (randomTile.isNotGoldSpawnPoint());

        // tile that passed checks, at random position, now has gold on it
        randomTile.addGold();
        // the map now has 1 more gold
        this.incrementGoldCount();
    }

    // getters/setters

    // count of gold existing on the map
    public int getGoldCount() {
        return this.goldCount;
    }

    // increment count of gold existing on the map
    public void incrementGoldCount() {
        this.goldCount++;
    }

    // decrement count of gold existing on the map
    public void decrementGoldCount() {
        if (this.goldCount == 0) {
            // should be unreachable but just in case
            System.err.println("Attempted to decrement map gold count below zero");
        }
        this.goldCount--;
    }

    // returns the gold win condition of the map
    public int getGoldWinCondition() {
        return this.goldWinCondition;
    }

    public String getMapName() {
        return this.mapName;
    }

    // returns total number of grid rows
    public int getRows() {
        return this.grid.size();
    }

    // returns total number of grid columns
    public int getColumns() {
        if (this.grid.isEmpty()) {
            // should be unreachable, no rows
            System.err.println("The grid has no rows");
            return 0;
        }
        return this.grid.get(0).size();
    }
}