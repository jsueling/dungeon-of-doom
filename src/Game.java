public class Game {

    private final Human humanPlayer;
    private Bot botPlayer;
    private Player currentPlayer;
    // map chosen by the user
    private final Map map;
    // difficulty chosen by the user
    private String difficulty;
    // Game will have responsibility of distributing the user input stream to those that need it and finally closing it
    private final UserInput userInput;
    // player chooses to quit the game
    private boolean hasQuit = false;

    // Throughout the program, I use System.out for user interaction including corrective feedback on user input,
    // System.err is used for unexpected errors which are more relevant for debugging.

    public Game(Map map, UserInput userInput) {

        this.map = map;
        this.userInput = userInput;

        // creates human player, passes access to user input stream
        this.humanPlayer = new Human(map, this, userInput);

        // prints choice and prompts user for difficulty
        this.chooseDifficulty();

        // creates the bot based on difficulty selection
        if (this.difficulty.equals("Normal")) {
            // This bot is only smart, it either looks, chases objectives based on its last look
            // or moves randomly within its last look
            this.botPlayer = new SmartBot(map, this);
        } else if (this.difficulty.equals("Impossible")) {
            // This bot can see the entire map without looking
            // and updates its decision every turn based on all available information
            this.botPlayer = new OmniscientBot(map, this);
        }

        // human player plays first
        this.currentPlayer = this.humanPlayer;

        System.out.println("Welcome to the Dungeon of Doom!");
    }

    public static void main(String[] args) {

        // opens user input stream
        UserInput userInput = new UserInput();

        try {
            // loads valid maps
            MapLoader mapLoader = new MapLoader();
            // print selection of maps
            mapLoader.printMapSelection();
            // prompts user for map choice, instantiates and returns a map
            Map map = mapLoader.chooseMap(userInput);
            // instantiate new game based on map choice, creating players
            Game game = new Game(map, userInput);
            // describe game difficulty and map choice
            game.describeDifficulty();
            map.describeMap();

            // Game starts

            // continue to play while the game is not over
            while (!game.isGameOver()) {
                game.currentPlayer.playTurn();
                game.switchPlayer();
                // checks if there is enough gold for both players to win by only looting
                if (!game.existsEnoughGoldToWin()) {
                    // if there is not, spawn gold at a random point in the map
                    map.spawnRandomGold();
                }
            }

            // cleanup UserInput resource
            userInput.closeStream();
            // Successful termination
            System.exit(0);

        } catch (Exception e) {

            // globally catch any errors and print to stderr
            e.printStackTrace();

            // cleanup UserInput resource
            userInput.closeStream();
            // Unsuccessful termination
            System.exit(1);
        }
    }

    // switches current player between bot and human
    private void switchPlayer() {
        if (this.currentPlayer == this.humanPlayer) {
            this.currentPlayer = this.botPlayer;
        } else {
            this.currentPlayer = this.humanPlayer;
        }
    }

    // Conditions for the game ending:
    // 1. (bot or human) player inputs QUIT,
    // WIN only when human player on exit tile with enough gold
    // 2. human touches the bot LOSE

    public void quitGame() {
        // player inputted quit
        this.hasQuit = true;
    }

    // handles logic for winning and losing and informing the player
    private boolean isGameOver() {

        // 1. the bot and human share the same tile LOSE
        if (isHumanTouchingBot()) {
            System.out.println("LOSE. The bot caught you!");
            return true;
        }

        // 2. A player has called quit
        if (hasQuit) {

            Map map = this.map;
            Human h = this.humanPlayer;
            Bot b = this.botPlayer;
            int goldToWin = map.getGoldWinCondition();

            // The human is on the exit tile with enough gold WIN
            if ((h.getTile() instanceof ExitTile) && (h.getGold() >= goldToWin)) {
                System.out.println("WIN. You escaped the Dungeon of Doom!");
                return true;
            }

            // The bot is on the exit tile with enough gold LOSE
            if ((b.getTile() instanceof ExitTile) && (b.getGold() >= goldToWin)) {
                System.out.println("LOSE. The bot collected enough gold and won!");
                return true;
            }

            // A player called quit and none of the above conditions match
            System.out.println("LOSE. You quit the game early, better luck next time!");
            return true;
        }

        return false;
    }

    // human is on the same tile in the map as the bot
    private boolean isHumanTouchingBot() {
        return this.humanPlayer.getTile().equals(this.botPlayer.getTile());
    }

    // check if there is enough gold to win the game for either player
    private boolean existsEnoughGoldToWin() {
        int botGold = this.botPlayer.getGold();
        int humanGold = this.humanPlayer.getGold();
        Map map = this.map;
        // Both players must be able to choose to loot only and win
        return map.getGoldCount() + Math.min(botGold, humanGold) >= map.getGoldWinCondition();
    }

    // prints difficulties and sets difficulty based on user selection
    private void chooseDifficulty() {

        String[] difficulties = {"Normal", "Impossible"};

        // prints a one-indexed list of difficulty choices
        for (int index = 0; index < difficulties.length; index++) {

            System.out.printf("%d. %s", index + 1, difficulties[index]);

            // print new line
            if (difficulties[index].equals("Impossible")) {
                System.out.println(" - This difficulty is just for demonstration, since this bot cheats by seeing without looking.");
            } else {
                System.out.println();
            }
        }

        // prompts user for difficulty selection
        System.out.print("Select a difficulty by entering the index: ");

        // delegate all responsibility to UserInput regarding handling user input.
        // getIndexWithinRange will repeatedly prompt the user for a valid index
        // according to size parameter passed in
        int listIndex = this.userInput.getIndexWithinRange(difficulties.length);
        // set the difficulty to the choice selected
        this.difficulty = difficulties[listIndex];
    }

    private void describeDifficulty() {
        System.out.println("The chosen difficulty is: " + this.difficulty + ".");
    }
}