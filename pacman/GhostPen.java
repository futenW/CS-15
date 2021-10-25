package pacman;

import java.util.LinkedList;
import java.util.Queue;

// pen for housing ghosts
public class GhostPen {
    Queue<Ghost> _ghostQueue; // queue for FIFO

    // initialize pen queue
    public GhostPen() {
        _ghostQueue = new LinkedList<Ghost>();
    }

    // get pen queue
    public Queue<Ghost> getGhostQueue() {
        return _ghostQueue;
    }
}
