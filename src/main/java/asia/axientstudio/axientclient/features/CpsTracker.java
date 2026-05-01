package asia.axientstudio.axientclient.features;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsTracker {
    private static final Deque<Long> leftClicks  = new ArrayDeque<>();
    private static final Deque<Long> rightClicks = new ArrayDeque<>();
    private static final long WINDOW = 1000L;

    public static void recordLeft()  { leftClicks.addLast(System.currentTimeMillis()); }
    public static void recordRight() { rightClicks.addLast(System.currentTimeMillis()); }

    public static int getLeftCps()  { return count(leftClicks); }
    public static int getRightCps() { return count(rightClicks); }

    private static int count(Deque<Long> q) {
        long cutoff = System.currentTimeMillis() - WINDOW;
        while (!q.isEmpty() && q.peekFirst() < cutoff) q.pollFirst();
        return q.size();
    }
}
