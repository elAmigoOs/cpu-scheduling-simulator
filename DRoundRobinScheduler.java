
import java.util.*;

public class DRoundRobinScheduler implements Scheduler {

    public double nextTimeQuantum(double now, ProcessControlBlock running, Deque<ProcessControlBlock> readyQ) {

        // basic dynamic RR: use average remaining time
        if (running == null) {
            return 1;
        }

        int count = readyQ.size() + 1; // running + ready
        double sum = running.remaining;

        for (ProcessControlBlock p : readyQ) {
            sum += p.remaining;
        }

        double avg = sum / count;
        long q = Math.round(avg);

        if (q < 1) {
            q = 1;
        }

        return q;
    }
}
