import java.util.*;

// credits to this discussion: https://stackoverflow.com/q/39266903
// I used this, my own knowledge of Dijkstra's priority queue implementation in Python
// and the docs https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html,
// https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#compare-T-T-

// The comparator is used to order the elements in the queue
public class TileNodeComparator implements Comparator<TileNode> {
    public int compare(TileNode t1, TileNode t2) {
        // orders the heap based on distance from the bot's stationary position
        // t1 will come before t2 when polling the heap if the distance from the bot is lower
        // note - unspecified order if equal, which is fine for this implementation
        return t1.getDistanceFromBot() - t2.getDistanceFromBot();
    }
}
