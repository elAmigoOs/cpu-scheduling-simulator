
public class Process {

    int id;
    double arrival;
    int mem;
    int dev;
    int burst;
    int prio;

    public Process(int id, double arrival, int mem, int dev, int burst, int prio) {
        this.id = id;
        this.arrival = arrival;
        this.mem = mem;
        this.dev = dev;
        this.burst = burst;
        this.prio = prio;
    }
}
