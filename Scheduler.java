
import java.util.*;

public interface Scheduler {

    double nextTimeQuantum(double now, ProcessControlBlock running, Deque<ProcessControlBlock> readyQ);
}
