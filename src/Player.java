import java.util.*;

abstract class Player {

    // player starts with zero gold
    private int gold = 0;
    // I initially had rowPosition and colPosition fields in this class,
    // but I think that design is quite brittle as Tile and Player attributes would
    // duplicate holding the player's position which would need to be updated in sync.
    // This implementation should be fine (we are assuming the player is always on a tile)
    private Tile tile;
    // These attributes become read-only after Player is instantiated
    private final Game game;
    private final Map map;

    public Player(Map map, Game game) {

        this.map = map; // Player must know the map to spawn in a random location
        this.game = game; // Player must know the game to give the quit command
        int row;
        int col;
        Tile randomTile;
        Random rand = new Random();

        do {
            // get a random position in the grid
            row = rand.nextInt(map.getRows());
            col = rand.nextInt(map.getColumns());
            // get the tile from that random position
            randomTile = map.getTile(row, col);
            // loop until the player can spawn on this tile
        } while (randomTile.isNotPlayerSpawnPoint());

        // initialises the player on the random tile that passed the checks
        this.initialisePlayerOnTile(randomTile);
    }

    // Bot methods:
    // 1. playTurn
    // 2. move
    // 3. pickup
    // 4. look
    // 5. quit

    // For the bot I am only printing the responses to commands it gives

    // Human methods:
    // 1. playTurn
    // 2. move
    // 3. pickup
    // 4. look
    // 5. quit
    // 6. hello
    // 7. gold

    // Shared player methods
    // 1. playTurn
    // 2. move
    // 3. pickup
    // 4. look
    // 5. quit

    // 1. playTurn
    // Player logic for deciding what to do with their turn
    abstract void playTurn();

    // 2. move
    // Player attempts to move to the tile at this position (at row, col)
    public void moveToTile(int row, int col) {

        // check if the player can move to the proposed new location
        if (this.map.playerCanMoveTo(row, col)) {

            // fetches new Tile destination from map
            Tile destination = this.map.getTile(row, col);
            // removes the current tile's information on what player was there
            this.leaveTile();
            // changes the current tile to destination tile
            this.setTile(destination);
            // changes the destination tile's information on what player is now there
            this.enterTile(destination);
            // successful move
            System.out.println("Success");

        } else {
            // unsuccessful move
            System.out.println("Fail");
        }
    }

    // changes the destination tile's information on what player is now there
    public abstract void enterTile(Tile destination);

    // removes the current tile's information on what player was there
    public abstract void leaveTile();

    // changes the current tile to destination tile
    private void setTile(Tile destination) {
        this.tile = destination;
    }

    // Should be called only when spawning player onto the map
    private void initialisePlayerOnTile(Tile tile) {
        // changes the player's information about what tile they are on
        this.setTile(tile);
        // changes the destination tile's information on what player is now there
        this.enterTile(tile);
    }

    // 3. pickup
    // Player attempts to pick up gold on the tile
    public void pickup() {

        // Player's current tile
        Tile tile = this.getTile();

        // if gold exists on the tile, then pick it up
        if (tile.hasGold()) {

            // Add 1 to player's gold count
            this.incrementGold();
            // Decrement the gold count of the map
            this.map.decrementGoldCount();
            // removes gold from the player's current tile
            tile.removeGold();
            // successful pickup
            System.out.print("Success. ");

        } else {
            // no gold on the tile, failed pickup
            System.out.print("Fail. ");
        }

        // print new gold after attempted pickup
        int newGold = this.getGold();
        System.out.println("Gold owned: " + newGold);
    }

    // 4. prints a 5x5 grid with the player at the center
    public void look() {
        Tile t = this.getTile();
        int row = t.getRow();
        int col = t.getCol();
        this.getMap().print5by5center(row, col);
    }

    // 5. quit
    public void quit() {
        this.game.quitGame();
    }

    // getters/setters

    // how much gold does the player have?
    public int getGold() {
        return this.gold;
    }

    // increase gold count of the player
    public void incrementGold() {
        this.gold++;
    }

    // what tile is the player on?
    public Tile getTile() {
        return this.tile;
    }

    public Map getMap() {
        return this.map;
    }
}
