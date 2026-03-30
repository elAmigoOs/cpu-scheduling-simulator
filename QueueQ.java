
import java.util.*;

public class QueueQ {

    private final Deque<ProcessControlBlock> q = new ArrayDeque<>();

    public void add(ProcessControlBlock p) {
        q.addLast(p);
    }

    public ProcessControlBlock poll() {
        return q.pollFirst();
    }

    public ProcessControlBlock peek() {
        return q.peekFirst();
    }

    public boolean isEmpty() {
        return q.isEmpty();
    }

    public Deque<ProcessControlBlock> getDeque() {
        return q;
    }

    public java.util.List<ProcessControlBlock> snapshot() {
        return new java.util.ArrayList<>(q);
    }
}
