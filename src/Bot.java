abstract class Bot extends Player {

    // up, down, left, right
    public int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

    public Bot(Map mp, Game g) {
        super(mp, g);
    }

    // Bot leaves the tile it's currently on
    public void leaveTile() {
        this.getTile().removeBot();
    }

    // Bot enters the destination tile
    public void enterTile(Tile destination) {
        destination.addBot();
    }

    // The bot makes decisions based on whether it has enough gold to win.
    // Is current gold owned greater than gold required to win the map?
    public boolean hasEnoughGoldToWin() {
        Map map = this.getMap();
        return this.getGold() >= map.getGoldWinCondition();
    }

    public void printBotTurn() {
        System.out.println("Bot's turn");
    }

    // Is the tile argument a current objective for the bot to chase/go to?
    public boolean isCurrentObjective(Tile tile) {

        // Can the bot go to the exit and win?
        boolean botHasEnoughGold = this.hasEnoughGoldToWin();

        // Bot objectives:
        //  ExitTile (must have enough gold):
        //      go to exit
        //  Gold (must have less gold than needed to win):
        //      go loot gold
        //  Human:
        //      chase human

        return (tile.hasHuman() || (botHasEnoughGold && (tile instanceof ExitTile))
                || tile.hasGold() && !botHasEnoughGold);
    }
}
