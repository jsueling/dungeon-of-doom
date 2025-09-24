public class TileNode {

    // I decided to keep this class fully separate from the Tile class
    // since they have completely different responsibilities.

    // stores the tile associated with this TileNode
    private final Tile tile;
    // stores distance from the stationary bot to the tile of this TileNode using Manhattan distance
    private final int distanceFromBot;

    // These properties will allow us to traverse up and down the shortest path of TileNodes
    private final TileNode parent;
    private TileNode child;
    private TileNode pointerNode;

    public TileNode(Tile tile, int distanceFromBot, TileNode parentNode) {
        this.tile = tile;
        this.distanceFromBot = distanceFromBot;
        this.parent = parentNode;
    }

    // Must override equals and hashCode methods for intended comparison between HashSet objects.
    // The equals and hashCode methods below are adapted from: https://stackoverflow.com/a/62483457

    // We assert that TileNodes are the same if they have the same Tile
    // The Tiles will reference the same objects in the 2D grid since they do not change once the map is instantiated
    public boolean equals(Object obj) { // adapted from: https://stackoverflow.com/a/62483457
        if (obj instanceof TileNode) {
            // cast obj as a TileNode object since instanceof returned true
            TileNode tileNode = (TileNode) obj;
            return tileNode.getTile() == this.tile;
        }
        return false;
    }

    // This means that TileNodes that have the same coordinates (of underlying tiles) are put in the same bucket
    // in the HashSet for comparison but also distributed among buckets evenly for performance reasons
    public int hashCode() { // adapted from: https://stackoverflow.com/a/62483457
        final int prime = 31;
        int result = 1;
        result = prime * result + this.tile.getRow();
        result = prime * result + this.tile.getCol();
        return result;
    }

    // getters/setters

    public Tile getTile() {
        return this.tile;
    }

    public TileNode getParent() {
        return this.parent;
    }

    private TileNode getChild() {
        return this.child;
    }

    private void setChild(TileNode child) {
        this.child = child;
    }

    // Traverse parent nodes, moving the pointerNode to the start of the path,
    // assigning child nodes at each step therefore constructing a chain
    public void initialisePath() {
        this.pointerNode = this;
        TileNode parent;
        // traverses up the chain of parents, setting children along the way
        while (this.pointerNode.getParent() != null) {
            // get the parent of node
            parent = this.pointerNode.getParent();
            // set the child of parent to the node
            parent.setChild(this.pointerNode);
            // node becomes the new parent
            this.pointerNode = parent;
        }
    }

    // gets the first tile of the path of nodes
    public Tile getFirstTileOfPath() {
        // If initialisePath has not yet been called or the pointerNode is not at the
        // root parent of the path
        if (this.pointerNode == null || this.pointerNode.getParent() != null) {
            // traverse up the parents, initialising the path
            this.initialisePath();
        }
        return this.pointerNode.getTile();
    }

    // peek the next child node in the path
    public boolean hasNextTileNode() {
        return this.pointerNode.getChild() != null;
    }

    // advance pointer to the next child and return it
    public TileNode getNextTileNode() {
        this.pointerNode = this.pointerNode.getChild();
        return pointerNode;
    }

    // returns distance from the TileNode to the stationary bot
    public int getDistanceFromBot() {
        return this.distanceFromBot;
    }
}
