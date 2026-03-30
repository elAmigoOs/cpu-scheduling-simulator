
import java.util.*;

public class SRoundRobinScheduler implements Scheduler {

    private final int baseQuantum;

    public SRoundRobinScheduler(int baseQuantum) {
        this.baseQuantum = baseQuantum;
    }

    @Override
    public double nextTimeQuantum(double now, ProcessControlBlock running, Deque<ProcessControlBlock> readyQ) {
        if (readyQ.isEmpty()) {
            // If no other jobs waiting, run to completion
            return running.remaining;
        } else {
            // Otherwise, use fixed quantum
            return baseQuantum;
        }
    }
}
