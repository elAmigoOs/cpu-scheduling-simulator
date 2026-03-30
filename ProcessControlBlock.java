
public class ProcessControlBlock {

    int jobId;
    int memReq;
    int devReq;
    double arrivalTime;
    double remaining;
    double originalBurst;
    double completionTime = -1;
    double turnaroundTime = 0;
    double waitingTime = 0;

    public ProcessControlBlock(Process j) {
        this.jobId = j.id;
        this.memReq = j.mem;
        this.devReq = j.dev;
        this.arrivalTime = j.arrival;
        this.remaining = j.burst;
        this.originalBurst = j.burst;
    }
}
