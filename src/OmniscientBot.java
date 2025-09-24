import java.util.*;

public class OmniscientBot extends Bot {

    public OmniscientBot(Map mp, Game g) {
        super(mp, g);
    }

    // This bot can see the entire map without looking
    // and updates its decision every turn based on all available information
    public void playTurn() {

        // prints out that it's the bot's turn
        this.printBotTurn();

        // OmniscientBot priority:
        // 1. If human is adjacent OR (has enough gold to win AND is on exit) => LOSE (Human loses, bot wins)
        // 2. If current tile has gold AND bot does not have enough gold to win => Pickup
        // 3. Search for the closest objective in the entire map
        //  Exit (must have enough gold):
        //      go to exit
        //  Gold (must have less gold than needed to win):
        //      go loot gold
        //  Human:
        //      chase human
        // 4. Immediately execute the first move of that objective

        Tile initialTile = this.getTile();
        int row = initialTile.getRow();
        int col = initialTile.getCol();
        Map map = this.getMap();

        // 1. The bot is on an exit tile with enough gold, calls quit to win the game => LOSE
        if ((initialTile instanceof ExitTile) && this.hasEnoughGoldToWin()) {
            this.quit();
            // end turn
            return;
        }

        // tiles that are 4-directionally adjacent to the bot's tile that the bot can enter
        ArrayList<Tile> adjacentTiles = new ArrayList<>();

        // try all 4 directions
        for (int[] direction : this.directions) {

            // get new position based off the bot's position
            int candidateRow = row + direction[0];
            int candidateCol = col + direction[1];

            // new position must be within the grid to be a possible candidate
            if (map.isOutOfBounds(candidateRow, candidateCol)) {
                continue;
            }

            // fetch the tile
            Tile candidateTile = map.getTile(candidateRow, candidateCol);

            // check it's not a wall tile
            if (!candidateTile.canEnter()) {
                continue;
            }

            // 1. human is adjacent, bot catches the human => LOSE
            // I want this to be prioritised over (2. Picking up gold)
            // since the bot can win faster by catching the human
            if (candidateTile.hasHuman()) {
                // attempt to move to the row, col of the adjacent tile with the human
                this.moveToTile(candidateRow, candidateCol);
                // end turn
                return;
            }

            // candidate tile now passed all checks and becomes part of adjacent tiles
            adjacentTiles.add(candidateTile);
        }

        // 2. If the current tile has gold AND the bot does not have enough gold to win => Pickup
        if (initialTile.hasGold() && !this.hasEnoughGoldToWin()) {
            this.pickup();
            // end turn
            return;
        }

        // 3. Search for the closest objective
        //  Exit (must have enough gold):
        //      go to exit
        //  Gold (must have less gold than needed to win):
        //      go loot gold
        //  Human:
        //      chase human

        // credits to this discussion: https://stackoverflow.com/q/39266903
        // I used this, my own knowledge of Dijkstra's priority queue implementation in Python
        // and the docs https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html

        // 1. The priority queue will keep extending potential paths from the first nodes.
        // 2. It will stop when it finds a path to one of its objectives.
        // 3. This path will have the shortest distance (greedy property of Dijkstra's) from the bot's stationary position.
        // 4. The OmniscientBot will then immediately act on that path by moving to the first tile of the path.

        // The comparator is used to order the elements in the queue
        PriorityQueue<TileNode> minHeap = new PriorityQueue<>(new TileNodeComparator());

        // stores tiles that have been visited to avoid an infinite loop when exploring
        HashSet<TileNode> visited = new HashSet<>();

        // Add the initial bot's tile to the visit set, so that it isn't searched unnecessarily
        visited.add(new TileNode(initialTile, 0, null));

        // initialise the minHeap with nodes created from the tiles (TileNodes) which are adjacent to the bot.
        // Each TileNode will form a path leading from the bot to a potential objective
        for (Tile startingTile : adjacentTiles) {
            // Add each adjacent tile to the minHeap as a TileNode with:
            // 1. the tile it represents
            // 2. distance away is 1 since tiles are adjacent
            // 3. the parent node as null since these are the starting nodes
            TileNode tileNode = new TileNode(startingTile, 1, null);
            // add each TileNode created from adjacent tiles to the minHeap
            minHeap.add(tileNode);
        }

        // 3. Search for the closest objective
        while (!minHeap.isEmpty()) {

            // Greedy property of Dijkstra's algorithm: each tileNode polled from the minHeap has
            // the shortest distance from the stationary bot (See TileNodeComparator)
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

            // If one of the objectives is found while expanding paths, stop searching and take that path
            if (this.isCurrentObjective(tile)) {

                // Get the first tile of the shortest path to the nearest objective
                Tile firstTileOfPath = tileNode.getFirstTileOfPath();
                // 4. Immediately execute the first move of the path to that objective
                this.moveToTile(firstTileOfPath.getRow(), firstTileOfPath.getCol());
                // end the turn
                return;
            }

            // Otherwise, explore neighbouring tiles of the polled tileNode
            int tileRow = tile.getRow();
            int tileColumn = tile.getCol();

            // try all 4 directions
            for (int[] direction : this.directions) {

                // get position of neighbouring tiles to search next,
                // based off the path head node's current position
                int neighbourTileRow = tileRow + direction[0];
                int neighbourTileColumn = tileColumn + direction[1];

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
    }
}
