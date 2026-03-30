
import java.util.*;

public class PrManager {

    private final OtherKerServices os;
    private final PriorityQueue<ProcessControlBlock> HQ1;
    private final QueueQ HQ2 = new QueueQ();
    private final QueueQ readyQ = new QueueQ();
    private Scheduler scheduler;
    private ProcessControlBlock running = null;
    private double curTime = 0.0;
    private double nextInternalEvent = Double.POSITIVE_INFINITY;
    private boolean finalDisplaySeen = false;
    private double lastSlice = 0.0;
    private final java.util.List<ProcessControlBlock> finished = new java.util.ArrayList<>();

    public PrManager(OtherKerServices os) {
        this.os = os;
        HQ1 = new PriorityQueue<>((a, b) -> {
            int cmp = Integer.compare(a.memReq, b.memReq);
            if (cmp != 0) {
                return cmp;
            }
            return Double.compare(a.arrivalTime, b.arrivalTime);
        });
    }

    public void setScheduler(Scheduler s) {
        this.scheduler = s;
    }

    public boolean hasRunnableWork() {
        return running != null || !readyQ.isEmpty() || !HQ1.isEmpty() || !HQ2.isEmpty();
    }

    public void cpuTimeAdvance(double t) {
        this.curTime = t;
    }

    public double getNextInternalEventTime(double now) {
        if (running == null && !readyQ.isEmpty()) {
            dispatch(now);
        }
        return nextInternalEvent;
    }

    public void procArrivalRoutine(Process j) {
        if (j.mem > os.totalMem || j.dev > os.totalDev) {
            return;
        }
        if (os.canAllocate(j.mem, j.dev)) {
            os.allocate(j.mem, j.dev);
            ProcessControlBlock pcb = new ProcessControlBlock(j);
            readyQ.add(pcb);
            if (running == null) {
                dispatch(curTime);
            }
        } else {
            ProcessControlBlock pcb = new ProcessControlBlock(j);
            if (j.prio == 1) {
                HQ1.add(pcb);
            } else {
                HQ2.add(pcb);
            }
        }
    }

    private void dispatch(double now) {
        if (running != null || readyQ.isEmpty()) {
            return;
        }
        running = readyQ.poll();
        double tq = scheduler.nextTimeQuantum(now, running, readyQ.getDeque());
        lastSlice = Math.min(tq, running.remaining);
        nextInternalEvent = now + lastSlice;
    }

    public void handleInternalEvent() {
        if (running == null) {
            nextInternalEvent = Double.POSITIVE_INFINITY;
            return;
        }
        running.remaining -= lastSlice;
        if (running.remaining <= 1e-9) {
            running.completionTime = curTime;
            running.turnaroundTime = running.completionTime - running.arrivalTime;
            running.waitingTime = running.turnaroundTime - running.originalBurst;
            finished.add(running);
            os.release(running.memReq, running.devReq);
            admitFromHolds();
            running = null;
            nextInternalEvent = Double.POSITIVE_INFINITY;
            if (!readyQ.isEmpty()) {
                dispatch(curTime);
            }
        } else {
            readyQ.add(running);
            running = null;
            nextInternalEvent = Double.POSITIVE_INFINITY;
            if (!readyQ.isEmpty()) {
                dispatch(curTime);
            }
        }
    }

    private void admitFromHolds() {
        boolean moved = true;
        while (moved) {
            moved = false;
            if (!HQ1.isEmpty()) {
                ProcessControlBlock p = HQ1.peek();
                if (os.canAllocate(p.memReq, p.devReq)) {
                    HQ1.poll();
                    os.allocate(p.memReq, p.devReq);
                    readyQ.add(p);
                    moved = true;
                }
            }
        }
        while (!HQ2.isEmpty()) {
            ProcessControlBlock p = HQ2.peek();
            if (os.canAllocate(p.memReq, p.devReq)) {
                HQ2.poll();
                os.allocate(p.memReq, p.devReq);
                readyQ.add(p);
            } else {
                break;
            }
        }
    }

    public void display(double curTime, OtherKerServices os, boolean isFinal) {
        System.out.println("-------------------------------------------------------");
        System.out.println("System Status:                                         ");
        System.out.println("-------------------------------------------------------");
        System.out.printf(java.util.Locale.US, "          Time: %.2f%n", curTime);
        System.out.println("  Total Memory: " + os.totalMem);
        System.out.println(" Avail. Memory: " + os.availMem);
        System.out.println(" Total Devices: " + os.totalDev);
        System.out.println("Avail. Devices: " + os.availDev);
        System.out.println();
        System.out.println("Jobs in Ready List                                      ");
        System.out.println("--------------------------------------------------------");

        // ONLY print ready queue (running job is NOT in readyQ)
        if (readyQ.isEmpty()) {
            System.out.println("  EMPTY");
        } else {
            for (ProcessControlBlock p : readyQ.snapshot()) {
                System.out.printf(java.util.Locale.US,
                        "Job ID %d , %.2f Cycles left to completion.%n",
                        p.jobId, p.remaining);
            }
        }

        System.out.println();
        System.out.println("Jobs in Long Job List                                   ");
        System.out.println("--------------------------------------------------------");
        System.out.println("  EMPTY");
        System.out.println();

        System.out.println("Jobs in Hold List 1                                     ");
        System.out.println("--------------------------------------------------------");
        if (HQ1.isEmpty()) {
            System.out.println("  EMPTY");
        } else {
            for (ProcessControlBlock p : new java.util.ArrayList<>(HQ1)) {
                System.out.printf(java.util.Locale.US,
                        "Job ID %d , %.2f Cycles left to completion.%n",
                        p.jobId, p.remaining);
            }
        }

        System.out.println();
        System.out.println("Jobs in Hold List 2                                     ");
        System.out.println("--------------------------------------------------------");
        if (HQ2.isEmpty()) {
            System.out.println("  EMPTY");
        } else {
            for (ProcessControlBlock p : HQ2.snapshot()) {
                System.out.printf(java.util.Locale.US,
                        "Job ID %d , %.2f Cycles left to completion.%n",
                        p.jobId, p.remaining);
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("Finished Jobs (detailed)                                ");
        System.out.println("--------------------------------------------------------");
        System.out.println("  Job    ArrivalTime     CompleteTime     TurnaroundTime    WaitingTime");
        System.out.println("------------------------------------------------------------------------");
        if (finished.isEmpty()) {
            System.out.println("  EMPTY");
        } else {
            List<ProcessControlBlock> sortedFinished = new ArrayList<>(finished);
            sortedFinished.sort(Comparator.comparingInt(p -> p.jobId));
            for (ProcessControlBlock p : sortedFinished) {
                System.out.printf(java.util.Locale.US,
                        "  %-6d %-14.2f %-15.2f %-16.2f %-14.2f%n",
                        p.jobId, p.arrivalTime, p.completionTime,
                        p.turnaroundTime, p.waitingTime);
            }
        }
        if (isFinal) {
            System.out.printf("Total Finished Jobs:             %d%n", finished.size());
        }
    }

    double getAverageTurnaround() {
        if (finished.isEmpty()) {
            return 0.0;
        }
        double s = 0;
        for (ProcessControlBlock p : finished) {
            s += p.turnaroundTime;
        }
        return s / finished.size();
    }

    public boolean isFinalDisplay() {
        return finalDisplaySeen;
    }

    public void markFinalDisplay() {
        finalDisplaySeen = true;
    }

    public void clearAll() {
        running = null;
        readyQ.getDeque().clear();
        HQ1.clear();          // ✅ Safe — comparator is already correct
        HQ2.getDeque().clear();
        finished.clear();
        nextInternalEvent = Double.POSITIVE_INFINITY;
        finalDisplaySeen = false;
    }
}
