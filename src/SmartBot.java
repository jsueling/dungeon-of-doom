import java.util.*;

public class SmartBot extends Bot {

    final private Random random = new Random();
    // Does the bot have a current objective?
    private TileNode objectiveTileNode = null;
    // What is it in words? This is so the bot knows what to do once it's reached
    private String currentObjective;

    // How long ago did the bot look?
    // Initialise as 3 since the bot should start the game by looking
    private int turnsSinceLastLook = 3;

    public SmartBot(Map mp, Game g) {
        super(mp, g);
    }

    // This bot is only smart, it chases objectives based on its last look,
    // looks or moves randomly within its last look
    public void playTurn() {

        // prints out that it's the bot's turn
        this.printBotTurn();
        // increment turns since last look
        this.turnsSinceLastLook++;

        // I've assumed the SmartBot is similar to human, it doesn't know what type of
        // tiles are around it until it looks. When it looks it will find the closest objective,
        // in a 5x5 grid centered on itself that it can path to, and not stop chasing until reached.

        // SmartBot moves randomly but not into walls within that 5x5 grid since it has memory from its look.
        // The bot is forced to look if it has been more than 2 turns since it last looked.
        // Consistently not hitting walls for more than 2 moves without looking would be inhuman since
        // the bot wouldn't know if it was going to move into a wall e.g. NNN WWW EEE SSS, therefore it must look.
        // This is in line with human capabilities.

        // SmartBot priority:
        //  1. If no current objective:
        //      If more than 2 turns since last look:
        //              => Look
        //      Else:
        //          move randomly to adjacent empty tiles
        //  2. If it has a current objective and is at current objective:
        //      gold => pickup, exit => quit or human => Look
        //  3. If it has a current objective and is not at current objective:
        //      continue path to current objective

        // Potential further development of bot logic:
        // Exploring the map that the bot has not seen before:
        // 1. Going to the empty tiles on the edges of its look vision
        // 2. Going to the furthest empty tiles away from its position
        // 3. Some combination
        // This would mimic how the human plays to some extent

        // SmartBot has no current objective
        if (this.objectiveTileNode == null) {

            // gather information by looking after not looking for 3 or more turns
            if (this.turnsSinceLastLook > 2) {
                this.smartBotLook();
            } else {
                // move randomly if less than 3 turns since last look and no objective found
                this.moveRandomly();
            }

            // SmartBot has a current objective
        } else if (this.currentObjective != null) {

            // SmartBot has reached the objective
            if (this.getTile() == this.objectiveTileNode.getTile()) {

                // decide what to do based on the objective
                if (this.currentObjective.equals("human")) {
                    // If the human was still here, the game should have ended.
                    // Therefore, look. smartBotLook may set a new objective here
                    this.smartBotLook();
                } else if (this.currentObjective.equals("gold")) {
                    // The bot reached where he remembered gold was and tried to pickup
                    this.pickup();
                    // clear objective
                    this.clearObjective();
                } else if (this.currentObjective.equals("exit")) {
                    // Assume the current objective being set as the exit means
                    // that the SmartBot already has enough gold
                    // bot reached the exit and the game should end after this turn
                    this.quit();
                    // In any case, clear objective
                    this.clearObjective();
                }

                // SmartBot has an objective but not yet reached it
            } else {
                // Possibly redundant check. Once the bot reaches the tile at the head of the
                // path/objective it should've triggered the above condition.
                if (this.objectiveTileNode.hasNextTileNode()) {

                    // get next TileNode on shortest path to the current objective
                    TileNode nextTileNode = this.objectiveTileNode.getNextTileNode();
                    Tile nextTile = nextTileNode.getTile();
                    // move to the row, col position corresponding to the TileNode
                    this.moveToTile(nextTile.getRow(), nextTile.getCol());
                    // Note - the first node of path in SmartBot is initial position, so getNext is called before moving

                } else {
                    // should be unreachable
                    System.err.println("The bot should've reached its objective TileNode before its path ran out");
                }
            }
        }
    }

    private void smartBotLook() {
        // prints response from look
        this.look();
        // attempts to find a new objective based on 5x5 look centered on the bot
        this.findObjective();
        // resets look counter
        this.resetTurnsSinceLastLook();
    }

    // find new objective for the bot, based on 5x5 look centered on bot, sets if found
    private void findObjective() {

        Tile initialTile = this.getTile();
        Map map = this.getMap();

        // Importantly, the objective will only be set if the bot can path to it,
        // the TileNode will then store all the information to traverse the path from start to finish

        // credits to this discussion: https://stackoverflow.com/q/39266903
        // I used this, my own knowledge of Dijkstra's priority queue implementation in Python
        // and the docs https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html

        // 1. The priority queue will keep extending potential paths from the bot's initial stationary position.
        // 2. It will stop when it finds a path to one of its objectives.
        // 3. This path will have the shortest distance (greedy property of Dijkstra's) between bot and objective.
        // 4. The SmartBot will store the path, if found, and keep getting the next tile in the path.
        // It will move along the path via that tile each turn until reaching its objective (the end of the path).

        // The comparator is used to order the elements in the queue
        PriorityQueue<TileNode> minHeap = new PriorityQueue<>(new TileNodeComparator());

        // Creates a node from a tile (TileNode) at the bot's initial stationary position.
        // This TileNode will form a path leading from the bot to a potential objective.
        // Initialises the minHeap with this node
        minHeap.add(new TileNode(initialTile, 0, null));

        // stores TileNodes that have been visited to avoid an infinite loop when exploring
        HashSet<TileNode> visited = new HashSet<>();

        // Can the bot chase the exit and win?
        boolean botHasEnoughGold = this.hasEnoughGoldToWin();

        // The bot's stationary position
        int initialRow = initialTile.getRow();
        int initialCol = initialTile.getCol();

        // Search for the closest objective
        while (!minHeap.isEmpty()) {

            // Greedy property of Dijkstra's algorithm: each tileNode polled from the minHeap has
            // the shortest distance from the bot
            TileNode tileNode = minHeap.poll();

            // only visit new nodes to avoid an infinite loop when exploring
            if (visited.contains(tileNode)) {
                continue;
            }

            // add polled tileNode to visit set
            visited.add(tileNode);

            // attributes of the polled tileNode
            Tile tile = tileNode.getTile();
            int distanceFromBot = tileNode.getDistanceFromBot();

            // current position of the head of a path
            int row = tile.getRow();
            int col = tile.getCol();

            // SmartBot can only search within the 5x5 grid of its look
            if (Math.abs(initialRow - row) > 2 || Math.abs(initialCol - col) > 2) {
                continue;
            }

            // If objective is found, stop the search
            if (this.isCurrentObjective(tile)) {

                // Catching the human is unlikely since they may have moved
                // by the time the bot reaches them so SmartBot will prioritise looting
                // (also may catch the human on his way to the gold)

                // Exit tile with enough gold
                if (botHasEnoughGold && (tile instanceof ExitTile)) {
                    this.setCurrentObjective("exit");
                    // Gold tile and needs more gold
                } else if (tile.hasGold() && !botHasEnoughGold) {
                    this.setCurrentObjective("gold");
                    // Human
                } else {
                    this.setCurrentObjective("human");
                }

                // Once the objective is found, initialise the shortest path
                // so that the bot can take that path to the objective
                tileNode.initialisePath();

                // Store the tileNode as the objective
                this.setObjectiveTileNode(tileNode);
                return;
            }

            // Otherwise, explore neighbouring tiles of the polled tileNode
            for (int[] direction : this.directions) {

                // get position of neighbouring tile to search based off the path head's (TileNode) current position
                int neighbourTileRow = row + direction[0];
                int neighbourTileColumn = col + direction[1];

                if (map.isOutOfBounds(neighbourTileRow, neighbourTileColumn)) {
                    continue;
                }

                Tile neighbourTile = map.getTile(neighbourTileRow, neighbourTileColumn);

                // neighbouring tile is a wall tile, cannot explore
                if (!neighbourTile.canEnter()) {
                    continue;
                }

                // The neighbouring tile passed all checks
                // Add it to the minHeap as a TileNode with:
                // 1. the tile it represents
                // 2. distance has increased by 1,
                // 3. the parent is the old polled TileNode
                minHeap.add(new TileNode(neighbourTile, distanceFromBot + 1, tileNode));
            }
        }

        // Finally, if the heap empties and no objectives are found, clear objective
        // that may still exist (e.g. bot reached a human objective, looked and found nothing)
        this.clearObjective();
    }

    // moves the bot randomly but not into walls
    private void moveRandomly() {

        int randomIndex;
        int newRow;
        int newCol;
        int[] randomDirection;
        Map map = this.getMap();
        Tile tile = this.getTile();

        do {
            // try to move 4-directionally adjacent to the current tile
            randomIndex = random.nextInt(4);
            // access directions array at a random index
            randomDirection = this.directions[randomIndex];
            // proposed new coordinates based on random choice of direction
            newRow = tile.getRow() + randomDirection[0];
            newCol = tile.getCol() + randomDirection[1];
            // continue looping while the proposed position cannot be entered
        } while (!map.playerCanMoveTo(newRow, newCol));

        // It is possible for the player to move to this tile, so move to it
        this.moveToTile(newRow, newCol);
    }

    // reset counter of turns since last look
    private void resetTurnsSinceLastLook() {
        this.turnsSinceLastLook = 0;
    }

    // clear current objective
    private void clearObjective() {
        this.objectiveTileNode = null;
        this.currentObjective = null;
    }

    // setting objective

    private void setCurrentObjective(String currentObjective) {
        this.currentObjective = currentObjective;
    }

    private void setObjectiveTileNode(TileNode objectiveTileNode) {
        this.objectiveTileNode = objectiveTileNode;
    }
}
