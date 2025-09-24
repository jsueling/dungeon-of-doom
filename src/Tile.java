public abstract class Tile {

    // These properties store what type of entity is on top of the tile.
    // In the current implementation, all 3 could exist on 1 tile.
    private boolean gold = false;
    private boolean human = false;
    private boolean bot = false;

    // One of the current lose conditions (isHumanTouchingBot) depends
    // on this feature. If the program was extended, this would probably need restructuring.
    // Could separate Items (e.g. Gold) that the Tile holds e.g. Tile.getItem()
    // from Players i.e. Tile.getPlayer() and then logic regarding isHumanTouchingBot would then be moved

    // For example:
    // Bot turn, move command executed, peek next tile tile.getPlayer(), if it has a human then call game.endGame()
    // Human turn, move command executed, peek next tile tile.getPlayer(), if it has a bot then call game.endGame()

    private final int row;
    private final int col;

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // getters and setters

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public boolean hasGold() {
        return this.gold;
    }

    public void addGold() {
        this.gold = true;
    }

    public void removeGold() {
        this.gold = false;
    }

    public boolean hasHuman() {
        return this.human;
    }

    public void addHuman() {
        this.human = true;
    }

    public void removeHuman() {
        this.human = false;
    }

    public boolean hasBot() {
        return this.bot;
    }

    public void addBot() {
        this.bot = true;
    }

    public void removeBot() {
        this.bot = false;
    }

    // All tiles other than walls can be entered by players
    abstract boolean canEnter();

    public boolean isNotPlayerSpawnPoint() {
        // For a player to be able to spawn on this tile, it must not contain:
        // 1. Gold
        // 2. Another Player
        // 3. Wall
        // but may contain:
        // 1. Exit
        // only returns false when tile has no gold, no player, no wall
        return (this.hasGold() || this.hasBot() || this.hasHuman() || !this.canEnter());
    }

    public boolean isNotGoldSpawnPoint() {
        // I decided for gold to be able to spawn on this tile, it must not contain:
        // Gold, Player, Wall, Exit
        // Even though there is repetition, I think it's unwise for it to depend on isNotPlayerSpawnPoint
        return (this.hasGold() || this.hasBot() || this.hasHuman() || !this.canEnter() || this instanceof ExitTile);
    }

    public void print() {
        // Since players can be on top of gold and gold can be on top of tile,
        // Print priority:
        // 1. Players
        // 2. Gold
        // 3. Tile
         if (this.hasBot()) {
             System.out.print("B");
         } else if (this.hasHuman()) {
             System.out.print("P");
         } else if (this.hasGold()) {
             System.out.print("G");
         } else {
             printTile();
         }
    }

    // It doesn't make sense for Tile as an abstract class to have a symbol
    // field, so I defer to subclasses to implement that
    abstract void printTile();

}