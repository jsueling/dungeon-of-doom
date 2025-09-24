public class Human extends Player {

    // stores access to UserInput implementation to read input from the user
    private final UserInput userInput;

    public Human(Map mp, Game g, UserInput userInput) {
        super(mp, g);
        this.userInput = userInput;
    }

    public void playTurn() {

        System.out.print("Your turn: ");

        // delegate all responsibility to UserInput regarding handling user input
        String lowerCaseLine = this.userInput.readLowerCaseString();

        if (lowerCaseLine.equals("hello")) {

            // prints gold win condition of the map
            System.out.println("Gold to win: " + this.getMap().getGoldWinCondition());

        } else if (lowerCaseLine.equals("gold")) {

            // prints gold owned on this turn
            System.out.println("Gold owned: " + this.getGold());

        } else if (lowerCaseLine.equals("pickup")) {

            // attempt pickup
            this.pickup();

        } else if (lowerCaseLine.equals("look")) {

            // print 5x5 grid around human
            this.look();

        } else if (lowerCaseLine.equals("quit")) {

            // end the game, win with enough gold on exit tile
            this.quit();

        } else if (lowerCaseLine.length() == 6 && lowerCaseLine.startsWith("move ")) {

            // handles how to move the human based on move command given as argument
            this.humanMove(lowerCaseLine);

        } else {

            // does not match any of the known commands, turn is skipped
            System.out.println("Fail, not a valid command.");

        }
    }

    // Human leaves the tile it's currently on
    public void leaveTile() {
        this.getTile().removeHuman();
    }

    // Human enters the destination tile
    public void enterTile(Tile destination) {
        destination.addHuman();
    }

    // Parses lower case line string from user input and moves the human
    private void humanMove(String lowerCaseLine) {

        Tile t = this.getTile();
        int row = t.getRow();
        int col = t.getCol();

        // parse direction from user input
        char direction = lowerCaseLine.charAt(5);

        // modify row/col based on direction
        if (direction == 'n') {
            row--;
        } else if (direction == 'e') {
            col++;
        } else if (direction == 's') {
            row++;
        } else if (direction == 'w') {
            col--;
        } else {
            // last character was not any of 'n' 'e' 's' 'w'
            System.out.println("Fail, not a valid command.");
            return;
        }

        // attempt to move to this tile
        this.moveToTile(row, col);
    }
}