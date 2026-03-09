package asia.axientstudio.axientclient.features;

import java.util.ArrayDeque;
import java.util.Deque;

public class CpsTracker {

    private static final Deque<Long> leftClicks = new ArrayDeque<>();
    private static final Deque<Long> rightClicks = new ArrayDeque<>();
    private static final long WINDOW_MS = 1000L;

    public static void recordLeft() {
        leftClicks.addLast(System.currentTimeMillis());
    }

    public static void recordRight() {
        rightClicks.addLast(System.currentTimeMillis());
    }

    public static int getLeftCps() {
        return countRecent(leftClicks);
    }

    public static int getRightCps() {
        return countRecent(rightClicks);
    }

    private static int countRecent(Deque<Long> clicks) {
        long now = System.currentTimeMillis();
        while (!clicks.isEmpty() && now - clicks.peekFirst() > WINDOW_MS) {
            clicks.pollFirst();
        }
        return clicks.size();
    }
}
